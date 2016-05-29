package com.jdelorenzo.capstoneproject;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.data.WorkoutContract;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GraphFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Unbinder unbinder;
    @BindView(R.id.graph) GraphView graphView;
    @BindView(R.id.empty_graph_textview) TextView mEmptyView;
    private static final String LOG_TAG = GraphFragment.class.getSimpleName();
    private static final String ARG_EXERCISE_ID = "exerciseId";
    private long mExerciseId;
    private Cursor mCursor;
    private static final int PROGRESS_LOADER = 100;

    public String[] WEIGHT_COLUMNS = {
            WorkoutContract.WeightEntry.TABLE_NAME + "." + WorkoutContract.WeightEntry._ID,
            WorkoutContract.WeightEntry.COLUMN_WEIGHT,
            WorkoutContract.WeightEntry.COLUMN_DATE,
            WorkoutContract.WeightEntry.COLUMN_EXERCISE_KEY
    };
    public final static int COL_WEIGHT_ID = 0;
    public final static int COL_WEIGHT = 1;
    public final static int COL_DATE = 2;
    public final static int COL_EXERCISE_KEY = 3;

    public static GraphFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_EXERCISE_ID, itemId);
        GraphFragment fragment = new GraphFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mExerciseId = args.getLong(ARG_EXERCISE_ID);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mExerciseId = savedInstanceState.getLong(ARG_EXERCISE_ID);
        }
        getLoaderManager().initLoader(PROGRESS_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(ARG_EXERCISE_ID, mExerciseId);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);
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
        Uri exerciseUri = WorkoutContract.WeightEntry.buildExerciseId(mExerciseId);
        return new CursorLoader(getActivity(),
                exerciseUri,
                WEIGHT_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(LOG_TAG, "Error reading from cursor");
            mCursor.close();
            mCursor = null;
        }
        bindViews();
    }

    //set up the graph based on the data points
    private void bindViews() {
        if (graphView == null) return;
        if (mCursor == null || !mCursor.moveToFirst()) {
            mEmptyView.setVisibility(View.VISIBLE);
            graphView.setVisibility(View.GONE);
        }
        else {
            graphView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            DataPoint[] points = new DataPoint[mCursor.getCount()];
            for (int i = 0; i < points.length; i++) {
                Double weight = mCursor.getDouble(COL_WEIGHT);
                String dateString = mCursor.getString(COL_DATE);
                LocalDate date = new LocalDate(dateString);
                points[i] = new DataPoint(date.toDate(), weight);
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
            series.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            graphView.addSeries(series);
            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        bindViews();
    }
}
