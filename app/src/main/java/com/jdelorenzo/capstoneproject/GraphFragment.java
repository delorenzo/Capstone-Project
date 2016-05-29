package com.jdelorenzo.capstoneproject;

import android.content.Context;
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

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            Log.d(LOG_TAG, "No graph data.");
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
            mEmptyView.setVisibility(View.GONE);
            DataPoint[] points;
            if (mCursor.getCount() > 1) {
                points = new DataPoint[mCursor.getCount()];
                for (int i = 0; i < points.length && !mCursor.isAfterLast(); i++) {
                    Double weight = mCursor.getDouble(COL_WEIGHT);
                    String dateString = mCursor.getString(COL_DATE);
                    LocalDate date = new LocalDate(dateString);
                    //this hacky way of storing the date is a consequence of the
                    //library being finicky on how it handles dates
                    String dateFormatString = date.toString("MMddYYYY");
                    double dateDouble = Double.parseDouble(dateFormatString);
                    points[i] = new DataPoint(dateDouble, weight);
                    mCursor.moveToNext();
                }
            }
            //if only one point exists, make it two to force the graph
            //to display something
            else {
                points = new DataPoint[2];
                Double weight = mCursor.getDouble(COL_WEIGHT);
                String dateString = mCursor.getString(COL_DATE);
                LocalDate date = new LocalDate(dateString);
                //this hacky way of storing the date is a consequence of the
                //library being finicky on how it handles dates
                String dateFormatString = date.toString("MMddYYYY");
                double dateDouble = Double.parseDouble(dateFormatString);
                DataPoint point = new DataPoint(dateDouble, weight);
                points[0] = point;
                points[1] = point;
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
            series.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            series.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.graphSeriesBackground));
            series.setDrawBackground(true);
            series.setDrawDataPoints(true);
            graphView.addSeries(series);
            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        if (value % 1 != 0) {
                            return "";
                        }
                        //this hacky way of displaying the string is a consequence of the
                        //library being finicky on how it handles dates
                        String dateString = String.valueOf(value);
                        return dateString.substring(0, 1) + "/" + dateString.substring(1, 2);
                    }
                    else {
                        return Utility.getFormattedWeightString(getContext(), value);
                    }
                }
            });
            if (mCursor.getCount() == 1) {
                //two labels is the minimum required for the graph to display
                graphView.getGridLabelRenderer().setNumHorizontalLabels(2);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        bindViews();
    }
}
