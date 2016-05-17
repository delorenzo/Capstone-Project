package com.jdelorenzo.capstoneproject;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.jdelorenzo.capstoneproject.data.WorkoutContract;
import com.jdelorenzo.capstoneproject.service.DatabaseIntentService;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        CreateWorkoutDialogFragment.CreateWorkoutDialogListener {
    @Bind(R.id.toolbar) Toolbar toolbar;
    private static final String FTAG_DIALOG_FRAGMENT = "CreateWorkoutDialogFragment";
    String [] mWorkoutLabels;
    long [] mWorkoutIds;
    @Bind({R.id.button_work_out, R.id.button_edit_workout, R.id.button_view_stats}) List<Button> workoutButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        new RetrieveWorkoutsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_work_out)
    public void onWorkOut() {
        if (mWorkoutIds == null || mWorkoutIds.length == 0) {
            Toast.makeText(this, getString(R.string.no_workouts_text), Toast.LENGTH_SHORT).show();
        }
        else if (mWorkoutIds.length > 1) {
            DialogFragment selectWorkoutFragment = SelectWorkoutDialogFragment.newInstance(new SelectWorkoutDialogFragment.SelectWorkoutListener() {
                @Override
                public void onWorkoutSelected(long id) {
                    workOut(id);
                }
            }, mWorkoutLabels, mWorkoutIds);
            selectWorkoutFragment.show(getFragmentManager(), FTAG_DIALOG_FRAGMENT);
        }
        else {
            workOut(mWorkoutIds[0]);
        }
    }

    private void workOut(long id) {
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.putExtra(WorkoutActivity.ARG_WORKOUT_ID, mWorkoutIds[0]);
        startActivity(intent);
    }

    @OnClick(R.id.button_create_workout)
    public void onCreateWorkout() {
        DialogFragment dialog = new CreateWorkoutDialogFragment();
        dialog.show(getFragmentManager(), FTAG_DIALOG_FRAGMENT);
    }

    @OnClick(R.id.button_edit_workout)
    public void onEditWorkout() {
        if (mWorkoutIds.length == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.no_workouts_text), Toast.LENGTH_SHORT).show();
        }
        else if (mWorkoutIds.length == 1) {
            modifyWorkout(mWorkoutIds[0]);
        }
        else {
            DialogFragment selectWorkoutFragment = SelectWorkoutDialogFragment.newInstance(new SelectWorkoutDialogFragment.SelectWorkoutListener() {
                @Override
                public void onWorkoutSelected(long id) {
                    modifyWorkout(id);
                }
            }, mWorkoutLabels, mWorkoutIds);
            selectWorkoutFragment.show(getFragmentManager(), FTAG_DIALOG_FRAGMENT);
        }
    }

    private void modifyWorkout(long workout) {
        Intent intent = new Intent(this, ModifyWorkoutActivity.class);
        intent.putExtra(WorkoutActivity.ARG_WORKOUT_ID, workout);
        startActivity(intent);
    }

    @OnClick(R.id.button_view_stats)
    public void onViewStats() {
        Intent intent = new Intent(this, ViewStatsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDialogPositiveClick(String name) {
        DatabaseIntentService.startActionAddWorkout(getApplicationContext(), name);
        Intent intent = new Intent(this, ModifyWorkoutActivity.class);
        intent.putExtra(ModifyWorkoutActivity.ARG_WORKOUT_NAME, name);
        startActivity(intent);
    }

    static final ButterKnife.Action<View> DISABLE = new ButterKnife.Action<View>() {
        @Override
        public void apply(View view, int index) {
            view.setAlpha(0.2f);
            view.setClickable(false);
        }
    };

    private class RetrieveWorkoutsTask extends AsyncTask<Void, Void, long[]> {
        public String WORKOUT_COLUMNS[] = {
                WorkoutContract.WorkoutEntry.TABLE_NAME + "." + WorkoutContract.WorkoutEntry._ID,
                WorkoutContract.WorkoutEntry.COLUMN_NAME
        };
        public int COL_ID = 0;
        public int COL_NAME = 1;

        @Override
        protected long [] doInBackground(Void... params) {
            Cursor retCursor = getContentResolver().query(
                    WorkoutContract.WorkoutEntry.CONTENT_URI,
                    WORKOUT_COLUMNS,
                    null,
                    null,
                    null
            );
            if (retCursor != null && retCursor.moveToFirst()) {
                int count = retCursor.getCount();
                mWorkoutIds = new long[count];
                mWorkoutLabels = new String[count];
                for (int i = 0; i < count; i++) {
                    mWorkoutIds[i] = retCursor.getLong(COL_ID);
                    mWorkoutLabels[i] = retCursor.getString(COL_NAME);
                    retCursor.moveToNext();
                }
                retCursor.close();
            }
            return mWorkoutIds;
        }

        @Override
        protected void onPostExecute(long[] result) {
            if (null == result || result.length == 0) {
                 ButterKnife.apply(workoutButtons, DISABLE);
            }
        }
    }
}
