package com.jdelorenzo.capstoneproject;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.jdelorenzo.capstoneproject.service.DatabaseIntentService;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        CreateWorkoutDialogFragment.CreateWorkoutDialogListener {
    @Bind(R.id.toolbar) Toolbar toolbar;
    private static final String FTAG_DIALOG_FRAGMENT = "CreateWorkoutDialogFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_work_out)
    public void onWorkOut() {
        Intent intent = new Intent(this, WorkoutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.button_create_workout)
    public void onCreateWorkout() {
        DialogFragment dialog = new CreateWorkoutDialogFragment();
        dialog.show(getFragmentManager(), FTAG_DIALOG_FRAGMENT);
    }

    @OnClick(R.id.button_edit_workout)
    public void onEditWorkout() {
        Intent intent = new Intent(this, ModifyWorkoutActivity.class);
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
        intent.putExtra(ModifyWorkoutActivity.ARG_NAME, name);
        startActivity(intent);
    }
}
