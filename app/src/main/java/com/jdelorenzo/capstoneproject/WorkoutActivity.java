package com.jdelorenzo.capstoneproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jdelorenzo.capstoneproject.dialogs.RestDialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkoutActivity extends AppCompatActivity {
    private long mWorkoutId;
    public static final String ARG_WORKOUT_ID = "workoutId";
    private static final String FTAG_WORKOUT = "workoutFragment";
    private static final String FTAG_REST_DIALOG = "restDialogFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle b = getIntent().getExtras();
        if (b!= null) {
            mWorkoutId = b.getLong(ARG_WORKOUT_ID);
        }
        if (savedInstanceState == null) {
            WorkoutFragment workoutFragment = WorkoutFragment.newInstance(mWorkoutId);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, workoutFragment, FTAG_WORKOUT)
                    .commit();
        }
    }

    @OnClick(R.id.fab_time)
    public void rest() {
        RestDialogFragment restDialogFragment = new RestDialogFragment();
        restDialogFragment.show(getFragmentManager(), FTAG_REST_DIALOG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
