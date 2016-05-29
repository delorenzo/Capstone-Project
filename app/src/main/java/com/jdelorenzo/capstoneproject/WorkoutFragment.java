package com.jdelorenzo.capstoneproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jdelorenzo.capstoneproject.adapters.ExerciseAdapter;
import com.jdelorenzo.capstoneproject.data.WorkoutContract.*;
import com.jdelorenzo.capstoneproject.dialogs.ModifyWeightDialogFragment;
import com.jdelorenzo.capstoneproject.dialogs.WorkoutCompleteDialogFragment;
import com.jdelorenzo.capstoneproject.model.Weight;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_ROUTINE_ID = "routineId";
    private long mRoutineId;
    private ExerciseAdapter mExerciseAdapter;
    private long mDay;
    @BindView(R.id.workout_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.workout_empty_view) TextView mEmptyView;
    @BindView(R.id.workout_completed_view) TextView mCompletedView;
    private Unbinder unbinder;
    private final static int WORKOUT_LOADER = 0;
    private static final String FTAG_MODIFY_WEIGHT = "modifyWeightDialogFragment";
    private static final String FTAG_WORKOUT_COMPLETE = "workoutCompleteDialogFragment";

    public String[] EXERCISE_COLUMNS = {
            ExerciseEntry.TABLE_NAME + "." + ExerciseEntry._ID,
            ExerciseEntry.COLUMN_WEIGHT,
            ExerciseEntry.COLUMN_SETS,
            ExerciseEntry.COLUMN_REPS,
            ExerciseEntry.COLUMN_DESCRIPTION,
            DayEntry.COLUMN_LAST_DATE,
            DayEntry.TABLE_NAME + DayEntry._ID
    };
    public final static int COL_EXERCISE_ID = 0;
    public final static int COL_WEIGHT = 1;
    public final static int COL_SETS = 2;
    public final static int COL_REPS = 3;
    public final static int COL_DESCRIPTION = 4;
    public static final int COL_LAST_DATE = 5;
    public static final int COL_DAY_ID = 6;

    public WorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * @param workoutId Workout ID.
     * @return A new instance of fragment WorkoutFragment.
     */
    public static WorkoutFragment newInstance(long workoutId) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ROUTINE_ID, workoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRoutineId = getArguments().getLong(ARG_ROUTINE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mExerciseAdapter = new ExerciseAdapter(getActivity(), new ExerciseAdapter.ExerciseAdapterOnClickHandler() {
            @Override
            public void onClick(long id, double weight, ExerciseAdapter.ExerciseAdapterViewHolder vh) {
                ModifyWeightDialogFragment fragment = ModifyWeightDialogFragment.newInstance(id, weight);
                fragment.show(getFragmentManager(), FTAG_MODIFY_WEIGHT);
            }

            @Override
            public void allItemsChecked() {
                endWorkout();
            }
        }, mEmptyView, mCompletedView, AbsListView.CHOICE_MODE_NONE);
        mRecyclerView.setAdapter(mExerciseAdapter);
        if (savedInstanceState != null) {
            mExerciseAdapter.onRestoreInstanceState(savedInstanceState);
        }
        return rootView;
    }

    private void endWorkout() {
        WorkoutCompleteDialogFragment fragment =
                WorkoutCompleteDialogFragment.newInstance(mExerciseAdapter.getDayId());
        fragment.show(getFragmentManager(), FTAG_WORKOUT_COMPLETE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mRoutineId = getArguments().getLong(ARG_ROUTINE_ID);
        }
        getLoaderManager().initLoader(WORKOUT_LOADER, null, this);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Calendar calendar = Calendar.getInstance();
        //the days are indexed by 0, calendar is indexed by 1
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Uri exerciseUri = ExerciseEntry.buildRoutineIdDayOfWeek(mRoutineId, day);
        return new CursorLoader(getActivity(),
                exerciseUri,
                EXERCISE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mExerciseAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mExerciseAdapter.swapCursor(null);
    }

    public ArrayList<Weight> getWeights() {
        return mExerciseAdapter.getWeights();
    }
}
