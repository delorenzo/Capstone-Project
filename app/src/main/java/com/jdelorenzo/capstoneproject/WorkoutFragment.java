package com.jdelorenzo.capstoneproject;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.adapters.ExerciseAdapter;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;
import com.jdelorenzo.capstoneproject.data.WorkoutContract.ExerciseEntry;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_WORKOUT_ID = "workoutId";
    private long mWorkoutId;
    private ExerciseAdapter mExerciseAdapter;
    private long mDay;
    @BindView(R.id.workout_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.workout_empty_view) TextView mEmptyView;
    private Unbinder unbinder;
    private final static int WORKOUT_LOADER = 0;

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
     * @param workoutId Workout ID.
     * @return A new instance of fragment WorkoutFragment.
     */
    public static WorkoutFragment newInstance(long workoutId) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_WORKOUT_ID, workoutId);
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
        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mExerciseAdapter = new ExerciseAdapter(getActivity(), new ExerciseAdapter.ExerciseAdapterOnClickHandler() {
            @Override
            public void onClick(Long id, ExerciseAdapter.ExerciseAdapterViewHolder vh) {
                //do nothing for now
            }
        }, mEmptyView, AbsListView.CHOICE_MODE_NONE);
        mRecyclerView.setAdapter(mExerciseAdapter);
        if (savedInstanceState != null) {
            mExerciseAdapter.onRestoreInstanceState(savedInstanceState);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mWorkoutId = getArguments().getLong(ARG_WORKOUT_ID);
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
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Uri exerciseUri = WorkoutContract.ExerciseEntry.buildWorkoutIdDayOfWeek(mWorkoutId, day);
        return new CursorLoader(getActivity(),
                exerciseUri,
                null,
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
