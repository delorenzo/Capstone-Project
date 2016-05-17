package com.jdelorenzo.capstoneproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ModifyWorkoutActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    private String mWorkoutName;
    private long mWorkoutId;

    public static final String ARG_WORKOUT_NAME = "workout";
    public static final String ARG_WORKOUT_ID = "workoutId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        setContentView(R.layout.activity_add_modify_workout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Bundle b = getIntent().getExtras();
        if (b!= null) {
            mWorkoutName = b.getString(ARG_WORKOUT_NAME);
            mWorkoutId = b.getLong(ARG_WORKOUT_ID);
        }
    }
}
