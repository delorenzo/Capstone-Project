package com.jdelorenzo.capstoneproject;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.jdelorenzo.capstoneproject.dialogs.SelectDaysDialogFragment;
import com.jdelorenzo.capstoneproject.service.DatabaseIntentService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class ModifyWorkoutActivity extends AppCompatActivity implements SelectDaysDialogFragment.SelectDaysListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    private String mWorkoutName;
    private long mWorkoutId;
    private boolean [] mCheckedDays;

    private static final String EXTRA_CHECKED_DAYS = "checkedDays";
    private static final String EXTRA_WORKOUT_ID = "workoutId";

    public static final String ARG_WORKOUT_NAME = "workout";
    public static final String ARG_WORKOUT_ID = "workoutId";
    private static final String FTAG_EDIT_DAY = "editDayFragment";
    private static final String FTAG_EDIT_WORKOUT = "editWorkoutFragment";
    private static final String FTAG_SELECT_DAYS = "selectDaysDialogFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mWorkoutId = savedInstanceState.getLong(EXTRA_WORKOUT_ID);
            mCheckedDays = savedInstanceState.getBooleanArray(EXTRA_CHECKED_DAYS);
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
        EditDayFragment selectDayFragment = EditDayFragment.newInstance(mWorkoutId, new EditDayFragment.SelectDayListener() {
            @Override
            public void onDaySelected(long dayId) {
                EditWorkoutFragment editWorkoutFragment = EditWorkoutFragment.newInstance(mWorkoutId, dayId);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, editWorkoutFragment, FTAG_EDIT_WORKOUT)
                        .commit();
            }
        });
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectDayFragment, FTAG_EDIT_DAY)
                .commit();
    }

    @Override
    public void onDaysSelected(ArrayList<Integer> indices) {
        ArrayList<String> selectedDays = new ArrayList<>(indices.size());
        String [] dayStrings = getResources().getStringArray(R.array.days);
        mCheckedDays = new boolean[7];
        for (int i : indices) {
            selectedDays.add(dayStrings[i]);
            mCheckedDays[i] = true;
        }
        DatabaseIntentService.startActionEditDays(this, selectedDays, mWorkoutId);
    }

    //In single pane mode, there is one FAB button for this activity, and it handles callbacks
    //for its child fragments
    @Optional @OnClick(R.id.fab)
    public void onFabClick() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.findFragmentByTag(FTAG_EDIT_DAY) != null) {
            onDayFab();
        }
        else if (fragmentManager.findFragmentByTag(FTAG_EDIT_WORKOUT) != null) {
            onExerciseFab();
        }
    }

    //in multi pane mode, this is one of the FAB menu options
    @Optional @OnClick(R.id.fab_day)
    public void onDayFab() {
        SelectDaysDialogFragment dialogFragment = SelectDaysDialogFragment.newInstance(mCheckedDays);
        dialogFragment.show(getFragmentManager(), FTAG_SELECT_DAYS);
    }

    //in multi pane mode, this is one of the fab menu options
    @Optional @OnClick(R.id.fab_exercise)
    public void onExerciseFab() {
        EditWorkoutFragment fragment = (EditWorkoutFragment) getFragmentManager().findFragmentByTag(FTAG_EDIT_WORKOUT);
        fragment.fabAction();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBooleanArray(EXTRA_CHECKED_DAYS, mCheckedDays);
        outState.putLong(EXTRA_WORKOUT_ID, mWorkoutId);
        super.onSaveInstanceState(outState);
    }
}
