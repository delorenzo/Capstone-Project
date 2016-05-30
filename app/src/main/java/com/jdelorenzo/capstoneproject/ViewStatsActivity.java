package com.jdelorenzo.capstoneproject;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jdelorenzo.capstoneproject.data.WorkoutContract;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewStatsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private StatisticsPagerAdapter mPagerAdapter;
    private int currentPosition;
    private int startingPosition;
    @BindView(R.id.pager) ViewPager mPager;
    @BindView(R.id.pager_tab_strip) PagerTabStrip pagerTabStrip;
    @BindView(R.id.pager_empty_view) TextView emptyView;
    private static final String CURRENT_POSITION = "startingPosition";

    public String[] EXERCISE_COLUMNS = {
            WorkoutContract.ExerciseEntry.TABLE_NAME + "." + WorkoutContract.ExerciseEntry._ID,
            WorkoutContract.ExerciseEntry.COLUMN_DESCRIPTION
    };
    public static final int COL_EXERCISE_ID = 0;
    public static final int COL_NAME = 1;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);
        ButterKnife.bind(this);

        getLoaderManager().initLoader(0, null, this);
        mPagerAdapter = new StatisticsPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        startingPosition = 0;
        currentPosition = savedInstanceState == null ? startingPosition : savedInstanceState.getInt(CURRENT_POSITION);
        mPager.setCurrentItem(currentPosition);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    currentPosition = position;
                    mCursor.moveToPosition(position);
                }
            }
        });
        pagerTabStrip.setTabIndicatorColorResource(R.color.colorAccent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_stats, menu);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_POSITION, currentPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class StatisticsPagerAdapter extends FragmentPagerAdapter {

        public StatisticsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (mCursor == null) return GraphFragment.newInstance(-1);
            mCursor.moveToPosition(position);
            return GraphFragment.newInstance(mCursor.getLong(COL_EXERCISE_ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mCursor == null || !mCursor.moveToFirst()) {
                return getString(R.string.empty_progress_text);
            }
            mCursor.moveToPosition(position);
            return mCursor.getString(COL_NAME);
        }

        //this is called when notifyDataSetChanged() is called
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return FragmentPagerAdapter.POSITION_NONE;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri exerciseUri = WorkoutContract.ExerciseEntry.CONTENT_URI;
        return new CursorLoader(this,
                exerciseUri,
                EXERCISE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mPagerAdapter.notifyDataSetChanged();
        if (mCursor != null && mCursor.moveToFirst()) {
            mCursor.moveToPosition(currentPosition);
            mPager.setCurrentItem(currentPosition, false);
            emptyView.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }
}
