package com.jdelorenzo.capstoneproject.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jdelorenzo.capstoneproject.data.WorkoutContract.ExerciseEntry;
import com.jdelorenzo.capstoneproject.data.WorkoutContract.DayEntry;
import com.jdelorenzo.capstoneproject.data.WorkoutContract.WorkoutEntry;

public class WorkoutProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private WorkoutDbHelper mOpenHelper;

    static final int WORKOUT = 100;
    static final int WORKOUT_WITH_DAY = 101;
    static final int DAY = 200;
    static final int EXERCISE = 300;

    private static final SQLiteQueryBuilder sWorkoutByDayQueryBuilder;

    static {
        sWorkoutByDayQueryBuilder = new SQLiteQueryBuilder();


    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WorkoutDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case WORKOUT:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WorkoutEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case DAY:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DayEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case EXERCISE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ExerciseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:  " + uri);
        }
        if (getContext() != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WorkoutContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WorkoutContract.PATH_WORKOUT, WORKOUT);
        matcher.addURI(authority, WorkoutContract.PATH_DAY, DAY);
        matcher.addURI(authority, WorkoutContract.PATH_EXERCISE, EXERCISE);

        return matcher;
    }
}
