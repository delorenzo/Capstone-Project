package com.jdelorenzo.capstoneproject;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jdelorenzo.capstoneproject.adapters.DayAdapter;
import com.jdelorenzo.capstoneproject.data.WorkoutContract;

public class SelectWorkoutFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public String DAY_COLUMNS [] = {
            WorkoutContract.DayEntry._ID,
            WorkoutContract.DayEntry.COLUMN_DAY_OF_WEEK
    };
    public static final int COL_ID = 0;
    public static final int COL_DAY_OF_WEEK = 1;

    private DayAdapter mDayAdapter;
    private long mDayId;


    interface SelectWorkoutListener {
        void onWorkoutSelected(int workoutId);
    }

    SelectWorkoutListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SelectWorkoutListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement SelectWorkoutListener");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri workoutForDayUri = WorkoutContract.DayEntry.buildDayId(mDayId);
        return new CursorLoader(getActivity(),
                workoutForDayUri,
                DAY_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDayAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDayAdapter.swapCursor(null);
    }
}
