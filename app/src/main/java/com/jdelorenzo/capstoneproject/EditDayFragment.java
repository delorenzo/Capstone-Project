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
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.adapters.DayAdapter;
import com.jdelorenzo.capstoneproject.animators.SlideAnimator;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;
import com.jdelorenzo.capstoneproject.service.DatabaseIntentService;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditDayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_ROUTINE_ID = "routineId";
    private static final String ARG_CALLBACK = "callback";
    private long mRoutineId;
    private DayAdapter mAdapter;
    @BindView(R.id.empty_workout_textview) TextView mEmptyView;
    @BindView(R.id.add_workout_recycler_view) RecyclerView mRecyclerView;
    private SelectDayListener mCallback;
    private Unbinder unbinder;

    private static final int DAY_LOADER = 0;

    private static final String[] DAY_COLUMNS = {
            WorkoutContract.DayEntry.TABLE_NAME + "." + WorkoutContract.DayEntry._ID,
            WorkoutContract.DayEntry.COLUMN_DAY_OF_WEEK,
            WorkoutContract.DayEntry.COLUMN_ROUTINE_KEY
    };

    public static final int COL_DAY_ID = 0;
    public static final int COL_DAY_OF_WEEK = 1;
    public static final int COL_WORKOUT_KEY = 2;

    public interface SelectDayListener extends Serializable {
        void onDaySelected(long dayId);
    }

    public static EditDayFragment newInstance(long workoutId, SelectDayListener listener) {
        EditDayFragment fragment = new EditDayFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_ROUTINE_ID, workoutId);
        b.putSerializable(ARG_CALLBACK, listener);
        fragment.setArguments(b);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mRoutineId = getArguments().getLong(ARG_ROUTINE_ID);
            mAdapter.onRestoreInstanceState(savedInstanceState);
        }
        getLoaderManager().initLoader(DAY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(ARG_ROUTINE_ID, mRoutineId);
        mAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    //This method is never called on older APIs ( < 22 )
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            mCallback = (SelectDayListener) getArguments().getSerializable(ARG_CALLBACK);
        }
        try {
            if (null == mCallback) {
                mCallback = (SelectDayListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement SelectDayListener");
        }
    }

    //This deprecated method left in place to support older APIs
    @Override
    @SuppressWarnings("deprecated")
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (null == mCallback) {
                mCallback = (SelectDayListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement SelectDayListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_workout, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setItemAnimator(new SlideAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new DayAdapter(getActivity(), new DayAdapter.DayAdapterOnClickHandler() {
            @Override
            public void onClick(Long id, DayAdapter.DayAdapterViewHolder vh) {
                mCallback.onDaySelected(id);
            }

            @Override
            public void onDelete(Long id, DayAdapter.DayAdapterViewHolder vh) {
                DatabaseIntentService.startActionDeleteDay(getActivity(), id);
                getActivity().getContentResolver().notifyChange(WorkoutContract.DayEntry.buildRoutineId(mRoutineId), null);
            }
        }, mEmptyView);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = WorkoutContract.DayEntry.COLUMN_DAY_OF_WEEK + " ASC";
        Uri dayForWorkoutUri = WorkoutContract.DayEntry.buildRoutineId(mRoutineId);
        return new CursorLoader(getActivity(),
                dayForWorkoutUri,
                DAY_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public boolean[] getChecked() {
        return mAdapter.getChecked();
    }



}
