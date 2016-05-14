package com.jdelorenzo.capstoneproject;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ModifyWorkoutActivity extends AppCompatActivity implements
        SelectWorkoutFragment.SelectWorkoutListener {
    @Bind(R.id.toolbar) Toolbar toolbar;
    public static final String ARG_NAME = "workoutName";
    private String mWorkoutName;

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
            mWorkoutName = b.getString(ARG_NAME);
        }
        else {
            SelectWorkoutFragment fragment = new SelectWorkoutFragment();

        }
    }

    @Override
    public void onWorkoutSelected(int workoutId) {

    }
}
