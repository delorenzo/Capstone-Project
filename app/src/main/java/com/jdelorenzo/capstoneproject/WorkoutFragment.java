package com.jdelorenzo.capstoneproject;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.adapters.ExerciseAdapter;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;
import com.jdelorenzo.capstoneproject.data.WorkoutContract.ExerciseEntry;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_WORKOUT_ID = "workout";
    private long mWorkoutId;
    private ExerciseAdapter mExerciseAdapter;
    private long mDay;
    @Bind(R.id.workout_recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.workout_empty_view) TextView mEmptyView;

    public String[] EXERCISE_COLUMNS = {
            ExerciseEntry.TABLE_NAME + "." + ExerciseEntry._ID,
            ExerciseEntry.COLUMN_WEIGHT,
            ExerciseEntry.COLUMN_SETS,
            ExerciseEntry.COLUMN_REPS,
            ExerciseEntry.COLUMN_DESCRIPTION
    };
    public final static int COL_EXERCISE_ID = 0;
    public final static int COL_WEIGHT = 1;
    public final static int COL_SETS = 2;
    public final static int COL_REPS = 3;
    public final static int COL_DESCRIPTION = 4;

    public WorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * @param workoutID Workout ID.
     * @return A new instance of fragment WorkoutFragment.
     */
    public static WorkoutFragment newInstance(long workoutID) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_WORKOUT_ID, workoutID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWorkoutId = getArguments().getLong(ARG_WORKOUT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout, container);
        ButterKnife.bind(this, rootView);
        mRecyclerView.setHasFixedSize(true);
        mExerciseAdapter = new ExerciseAdapter(getActivity(), new ExerciseAdapter.ExerciseAdapterOnClickHandler() {
            @Override
            public void onClick(Long id, ExerciseAdapter.ExerciseAdapterViewHolder vh) {
                //do nothing for now
            }
        }, mEmptyView, AbsListView.CHOICE_MODE_NONE);
        if (savedInstanceState != null) {
            mExerciseAdapter.onRestoreInstanceState(savedInstanceState);
        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri workoutForDayUri = WorkoutContract.WorkoutEntry.buildWorkoutId(mWorkoutId);
        return new CursorLoader(getActivity(),
                workoutForDayUri,
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
}
