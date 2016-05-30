package com.jdelorenzo.capstoneproject.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jdelorenzo.capstoneproject.R;

//TODO:  Sync SQLITE database with Firebase database
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private ContentResolver mContentResolver;
    public static final String ACTION_DATA_UPDATED =
            "com.jdelorenzo.capstoneproject.app.ACTION_DATA_UPDATED";

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private final static String LOG_TAG = SyncAdapter.class.getSimpleName();
    private Context mContext;
    private DatabaseReference mDatabase;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mContext = context;
    }

    //todo
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Performing sync");
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    //todo
    private String formJSONString() {
        return "";
    }

    //todo
    private void handleJSONString(String json) {

    }

    private void updateWidgets() {
        Context context = getContext();
        Intent intent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = createSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(createSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String id = sharedPreferences.getString(context.getString(R.string.prefs_google_user_id_token), "");
        if (id.isEmpty()) {
            FirebaseCrash.logcat(Log.WARN, LOG_TAG, "Sync adapter called without a valid Google sign on");
            return null;
        }
        Account newAccount = new Account(
                id, context.getString(R.string.sync_account_type));
        // Get an instance of the Android account manager
        AccountManager accountManager = AccountManager.get(context);

        //if the password does not exist, the account is new
        if (null == accountManager.getPassword(newAccount)) {
            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, null, null)) {
                Log.e(LOG_TAG, "Failed to add sync account");
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            //the sync adapter is only syncable when the user uses Google Sign In, so set
            //is syncable here.
            ContentResolver.setIsSyncable(newAccount, context.getString(R.string.content_authority), 1);
            onAccountCreated(newAccount, context);
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
            Log.e(LOG_TAG, "Sync adapter account exists.");
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        createSyncAccount(context);
    }
}
