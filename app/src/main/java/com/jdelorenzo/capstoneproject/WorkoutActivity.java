package com.jdelorenzo.capstoneproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import butterknife.OnClick;

public class WorkoutActivity extends AppCompatActivity {
    private long mWorkoutId;
    public static final String ARG_WORKOUT_ID = "workoutId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        if (b!= null) {
            mWorkoutId = b.getLong(ARG_WORKOUT_ID);
        }
    }

    @OnClick(R.id.fab_time)
    public void rest() {
        Toast.makeText(this, "Take a break!", Toast.LENGTH_SHORT).show();
    }
}
