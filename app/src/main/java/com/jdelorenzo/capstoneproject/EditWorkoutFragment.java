package com.jdelorenzo.capstoneproject;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jdelorenzo.capstoneproject.adapters.EditExerciseAdapter;
import com.jdelorenzo.capstoneproject.adapters.ExerciseAdapter;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;
import com.jdelorenzo.capstoneproject.service.DatabaseIntentService;

import java.io.Serializable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditWorkoutFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_WORKOUT_ID = "workoutId";
    private static final String ARG_DAY_ID = "dayId";
    private static final String ARG_CALLBACK = "callback";
    private long mWorkoutId;
    private long mDayId;
    private EditExerciseAdapter mAdapter;
    @BindView(R.id.empty_exercise_textview) TextView mEmptyView;
    @BindView(R.id.exercise_recyclerview) RecyclerView mRecyclerView;
    private static final int WORKOUT_LOADER = 0;
    private Unbinder unbinder;

    public String[] EXERCISE_COLUMNS = {
            WorkoutContract.ExerciseEntry.TABLE_NAME + "." + WorkoutContract.ExerciseEntry._ID,
            WorkoutContract.ExerciseEntry.COLUMN_WEIGHT,
            WorkoutContract.ExerciseEntry.COLUMN_SETS,
            WorkoutContract.ExerciseEntry.COLUMN_REPS,
            WorkoutContract.ExerciseEntry.COLUMN_DESCRIPTION
    };
    public final static int COL_EXERCISE_ID = 0;
    public final static int COL_WEIGHT = 1;
    public final static int COL_SETS = 2;
    public final static int COL_REPS = 3;
    public final static int COL_DESCRIPTION = 4;

    public static EditWorkoutFragment newInstance(long dayId, long workoutId) {
        EditWorkoutFragment fragment = new EditWorkoutFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_WORKOUT_ID, workoutId);
        b.putLong(ARG_DAY_ID, dayId);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWorkoutId = getArguments().getLong(ARG_WORKOUT_ID);
            mDayId = getArguments().getLong(ARG_DAY_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mWorkoutId = getArguments().getLong(ARG_WORKOUT_ID);
            mDayId = getArguments().getLong(ARG_DAY_ID);
        }
        getLoaderManager().initLoader(WORKOUT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(ARG_WORKOUT_ID, mWorkoutId);
        outState.putLong(ARG_DAY_ID, mDayId);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_workout_day, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new EditExerciseAdapter(getActivity(), new EditExerciseAdapter.ExerciseAdapterOnClickHandler() {
            @Override
            public void onClick(Long id, EditExerciseAdapter.ExerciseAdapterViewHolder vh) {

            }

            @Override
            public void onDelete(Long id, EditExerciseAdapter.ExerciseAdapterViewHolder vh) {
                DatabaseIntentService.startActionDeleteExercise(getActivity(), id);
                getActivity().getContentResolver().notifyChange(WorkoutContract.ExerciseEntry.buildDayId(mDayId), null);
            }
        }, mEmptyView, ListView.CHOICE_MODE_SINGLE);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri dayForWorkoutUri = WorkoutContract.ExerciseEntry.buildDayId(mDayId);
        return new CursorLoader(getActivity(),
                dayForWorkoutUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public long getDayId() {
        return mDayId;
    }
}
