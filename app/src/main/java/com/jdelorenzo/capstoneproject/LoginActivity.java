package com.jdelorenzo.capstoneproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.jdelorenzo.capstoneproject.dialogs.SignInInfoDialogFragment;
import com.jdelorenzo.capstoneproject.sync.SyncAdapter;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A login screen that offers login via Google Sign In.
 * {@see https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/GoogleSignInActivity.java}
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.login_progress) View mProgressView;
    @BindView(R.id.login_form) View mLoginFormView;
    @BindView(R.id.sign_in_button) SignInButton mSignInButton;
    private GoogleApiClient mGoogleApiClient;
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean showLogin = prefs.getBoolean(getString(R.string.prefs_login_key),
                getResources().getBoolean(R.bool.prefs_login_default_value));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (showLogin) {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mAuth = FirebaseAuth.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.d(LOG_TAG, "Signed in with :  " + user.getUid());
                        mFirebaseAnalytics.setUserId(user.getUid());
                        //SyncAdapter.initializeSyncAdapter(getApplicationContext());
                    } else {
                        Log.d(LOG_TAG, "Signed out");
                    }
                }
            };

            OptionalPendingResult<GoogleSignInResult> pendingResult =
                    Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (pendingResult.isDone()) {
                // There's immediate result available.
                handleSignInResult(pendingResult.get());
            } else {
                // There's no immediate result ready, displays some progress indicator and waits for the
                // async callback.
                showProgress(true);
                pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult result) {
                        handleSignInResult(result);
                        showProgress(false);
                    }
                });
            }
            //mSignInButton.setSize(SignInButton.SIZE_STANDARD);
            mSignInButton.setScopes(gso.getScopeArray());
        }
        else {
            noSignIn();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @OnClick(R.id.sign_in_button)
    public void signIn() {
        mFirebaseAnalytics.setUserProperty(getString(R.string.analytics_sign_in_key),
                getString(R.string.analytics_sign_in__google));
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.button_no_sign_in)
    public void noSignIn() {
        mFirebaseAnalytics.setUserProperty(getString(R.string.analytics_sign_in_key),
                getString(R.string.analytics_sign_in_none));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

//    @Optional @OnClick(R.id.button_why_sign_in)
//    public void whySignIn() {
//        DialogFragment fragment = new SignInInfoDialogFragment();
//        fragment.show(getFragmentManager(), "SignInInfoDialogFragment");
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            Toast.makeText(this, "Authentication successful.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            if (acct != null) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                preferences.edit()
                        .putString(getString(R.string.prefs_display_name_key), acct.getGivenName())
                        .putString(getString(R.string.prefs_google_user_id), acct.getId())
                        .apply();
                mFirebaseAnalytics.setUserProperty(getString(R.string.analytics_user_name),
                        acct.getGivenName());
            }
            startActivity(intent);
        } else {
            // Signed out, show unauthenticated UI.
            String resultMessage = result.getStatus().getStatusMessage();
            int code = result.getStatus().getStatusCode();
            String errorMessage = getString(R.string.authentication_generic_failure);
            if (code == GoogleSignInStatusCodes.SIGN_IN_REQUIRED) {
                errorMessage = getString(R.string.common_google_play_services_sign_in_failed_text);
            }
            else if (code == GoogleSignInStatusCodes.NETWORK_ERROR) {
                errorMessage = getString(R.string.common_google_play_services_network_error_text);
            }
            FirebaseCrash.logcat(Log.ERROR, LOG_TAG, "Google sign in unsuccessful.  Code " + code);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(LOG_TAG, "Authenticating Firebase with Google:  " + account.getId());
        showProgress(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "Completed firebase sign on with Google:  " +
                                task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseCrash.logcat(Log.WARN, LOG_TAG,
                                    "Firebase sign on unsuccessful:  " + task.getException());
                        }
                        showProgress(false);
                    }
                });
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.error_connection_failed), Toast.LENGTH_SHORT).show();
        Log.e(LOG_TAG, "Connection failed:  " + connectionResult.toString());
        showProgress(false);
    }
}

