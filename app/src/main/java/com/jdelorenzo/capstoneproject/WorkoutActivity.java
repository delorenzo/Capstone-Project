package com.jdelorenzo.capstoneproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jdelorenzo.capstoneproject.data.WorkoutContract;
import com.jdelorenzo.capstoneproject.dialogs.ModifyWeightDialogFragment;
import com.jdelorenzo.capstoneproject.dialogs.RestDialogFragment;
import com.jdelorenzo.capstoneproject.dialogs.WorkoutCompleteDialogFragment;
import com.jdelorenzo.capstoneproject.model.Weight;
import com.jdelorenzo.capstoneproject.service.DatabaseIntentService;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkoutActivity extends AppCompatActivity implements
        ModifyWeightDialogFragment.ModifyWeightListener,
        WorkoutCompleteDialogFragment.WorkoutCompleteListener {
    private long mRoutineId;
    public static final String ARG_ROUTINE_ID = "routineId";
    private static final String FTAG_WORKOUT = "workoutFragment";
    private static final String FTAG_REST_DIALOG = "restDialogFragment";
    private static final String LOG_TAG = WorkoutActivity.class.getSimpleName();

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
            mRoutineId = b.getLong(ARG_ROUTINE_ID);
        }
        if (savedInstanceState == null) {
            WorkoutFragment workoutFragment = WorkoutFragment.newInstance(mRoutineId);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_master_container, workoutFragment, FTAG_WORKOUT)
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

    @Override
    public void onWeightModified(long id, double weight) {
        weight = Utility.convertWeightToMetric(getApplicationContext(), weight);
        DatabaseIntentService.startActionEditExerciseWeight(this, id, weight);
        Calendar calendar = Calendar.getInstance();
        //the days are indexed by 0, calendar is indexed by 1
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        //update the correct URI so that the recyclerview is updated
        Uri exerciseUri = WorkoutContract.ExerciseEntry.buildRoutineIdDayOfWeek(mRoutineId, day);
        getContentResolver().notifyChange(exerciseUri, null);
    }

    //update the workout day to mark it as complete
    @Override
    public void onWorkoutComplete(long dayId) {
        Calendar calendar = Calendar.getInstance();
        DatabaseIntentService.startActionCompleteWorkout(this, dayId);

        WorkoutFragment workoutFragment = (WorkoutFragment) getFragmentManager().findFragmentByTag(FTAG_WORKOUT);
        if (workoutFragment != null) {
            ArrayList<Weight> weights = workoutFragment.getWeights();
            DatabaseIntentService.startActionRecordWeights(this, weights);
        }
        else {
            Log.e(LOG_TAG, "Unable to find child fragment to record weights");
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
