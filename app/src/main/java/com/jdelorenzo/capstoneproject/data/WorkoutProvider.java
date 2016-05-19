package com.jdelorenzo.capstoneproject.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private static final String sWorkoutIdSelection = WorkoutEntry.TABLE_NAME + "." +
            WorkoutEntry._ID + " = ?";
    private static final String sDayIdSelection = DayEntry.TABLE_NAME + "." +
            DayEntry._ID + " = ?";
    private static  final String sExerciseIdSelection = ExerciseEntry.TABLE_NAME + "." +
            ExerciseEntry._ID + " = ?";
    private static final String sWorkoutIdDayOfWeekSelection = WorkoutEntry.TABLE_NAME + "." +
            WorkoutEntry._ID + " = ? AND " + DayEntry.TABLE_NAME + "." +
            DayEntry.COLUMN_DAY_OF_WEEK + " = ?";
    private static final String sDayWorkoutKeySelection = DayEntry.TABLE_NAME + "." +
            DayEntry.COLUMN_WORKOUT_KEY + " = ?";
    private static final String sExerciseDayIdSelection = ExerciseEntry.TABLE_NAME + "." +
            ExerciseEntry.COLUMN_DAY_KEY + " = ?";
    private static final String sExerciseWorkoutIdDayOfWeekSelection = DayEntry.TABLE_NAME + "." +
            DayEntry.COLUMN_WORKOUT_KEY + " = ? AND " + DayEntry.TABLE_NAME + "." +
            DayEntry.COLUMN_DAY_OF_WEEK + " = ?";

    static final int WORKOUTS = 100;
    static final int WORKOUT_WITH_ID = 101;
    static final int DAYS = 200;
    static final int DAY_WITH_ID = 201;
    static final int DAYS_WITH_WORKOUT_ID = 202;
    static final int DAY_WITH_WORKOUT_ID_AND_DAY_OF_WEEK = 203;
    static final int EXERCISES = 300;
    static final int EXERCISE_WITH_ID = 301;
    static final int EXERCISES_WITH_WORKOUT_ID = 302;
    static final int EXERCISES_WITH_DAY_ID = 303;
    static final int EXERCISES_WITH_WORKOUT_ID_AND_DAY_OF_WEEK = 304;

    private static final SQLiteQueryBuilder sDayByWorkoutQueryBuilder;

    static {
        sDayByWorkoutQueryBuilder = new SQLiteQueryBuilder();

        sDayByWorkoutQueryBuilder.setTables(
                WorkoutEntry.TABLE_NAME + " INNER JOIN " + DayEntry.TABLE_NAME +
                        " ON " + WorkoutEntry.TABLE_NAME + "." + WorkoutEntry._ID + " = " +
                        DayEntry.TABLE_NAME + "." + DayEntry.COLUMN_WORKOUT_KEY
        );
    }

    private static final SQLiteQueryBuilder sExerciseByDayQueryBuilder;

    static {
        sExerciseByDayQueryBuilder = new SQLiteQueryBuilder();

        sExerciseByDayQueryBuilder.setTables(
                DayEntry.TABLE_NAME + " INNER JOIN " + ExerciseEntry.TABLE_NAME +
                        " ON " + DayEntry.TABLE_NAME + "." + DayEntry._ID +
                        " = " + ExerciseEntry.TABLE_NAME + "." + ExerciseEntry.COLUMN_DAY_KEY
        );
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
            case WORKOUTS:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WorkoutEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case WORKOUT_WITH_ID:
                long workoutId = WorkoutEntry.getWorkoutIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WorkoutEntry.TABLE_NAME,
                        projection,
                        sWorkoutIdSelection,
                        new String [] {Long.toString(workoutId)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case DAYS:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DayEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case DAY_WITH_ID:
                long dayId = DayEntry.getDayIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DayEntry.TABLE_NAME,
                        projection,
                        sDayIdSelection,
                        new String[] {Long.toString(dayId)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case DAYS_WITH_WORKOUT_ID:
                workoutId = DayEntry.getWorkoutIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DayEntry.TABLE_NAME,
                        projection,
                        sDayWorkoutKeySelection,
                        new String [] {Long.toString(workoutId)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case DAY_WITH_WORKOUT_ID_AND_DAY_OF_WEEK:
                workoutId = DayEntry.getWorkoutIdFromUri(uri);
                String dayOfWeek = DayEntry.getDayOfWeekFromUri(uri);
                retCursor = sDayByWorkoutQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        sWorkoutIdDayOfWeekSelection,
                        new String [] {Long.toString(workoutId), dayOfWeek},
                        null,
                        null,
                        sortOrder
                );
                break;
            case EXERCISES:
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
            case EXERCISE_WITH_ID:
                long exerciseId = ExerciseEntry.getExerciseIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ExerciseEntry.TABLE_NAME,
                        projection,
                        sExerciseIdSelection,
                        new String [] {Long.toString(exerciseId)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case EXERCISES_WITH_DAY_ID:
                dayId = ExerciseEntry.getDayIdFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ExerciseEntry.TABLE_NAME,
                        projection,
                        sExerciseDayIdSelection,
                        new String[] {Long.toString(dayId)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case EXERCISES_WITH_WORKOUT_ID_AND_DAY_OF_WEEK:
                workoutId = ExerciseEntry.getWorkoutIdFromUri(uri);
                int day = ExerciseEntry.getDayOfWeekFromUri(uri);
                retCursor = sExerciseByDayQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        sExerciseWorkoutIdDayOfWeekSelection,
                        new String[] {Long.toString(workoutId), Integer.toString(day)},
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
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case WORKOUTS:
                return WorkoutEntry.CONTENT_TYPE;
            case WORKOUT_WITH_ID:
                return WorkoutEntry.CONTENT_ITEM_TYPE;
            case DAYS:
                return DayEntry.CONTENT_TYPE;
            case DAY_WITH_ID:
                return DayEntry.CONTENT_ITEM_TYPE;
            case DAY_WITH_WORKOUT_ID_AND_DAY_OF_WEEK:
                return DayEntry.CONTENT_ITEM_TYPE;
            case DAYS_WITH_WORKOUT_ID:
                return DayEntry.CONTENT_TYPE;
            case EXERCISES:
                return ExerciseEntry.CONTENT_TYPE;
            case EXERCISE_WITH_ID:
                return ExerciseEntry.CONTENT_ITEM_TYPE;
            case EXERCISES_WITH_DAY_ID:
                return ExerciseEntry.CONTENT_TYPE;
            case EXERCISES_WITH_WORKOUT_ID:
                return ExerciseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:  " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case WORKOUTS:
                long workoutId = db.insert(WorkoutEntry.TABLE_NAME, null, values);
                if (workoutId > 0)
                    returnUri = WorkoutEntry.buildWorkoutId(workoutId);
                else
                    throw new android.database.SQLException("Failed to insert workout row into " + uri);
                break;
            case DAYS:
                long dayId = db.insert(DayEntry.TABLE_NAME, null, values);
                if (dayId > 0)
                    returnUri = DayEntry.buildDayId(dayId);
                else
                    throw new android.database.SQLException("Failed to insert day row into " + uri);
                break;
            case EXERCISES:
                long exerciseId = db.insert(ExerciseEntry.TABLE_NAME, null, values);
                if (exerciseId > 0)
                    returnUri = ExerciseEntry.buildExerciseId(exerciseId);
                else
                    throw new android.database.SQLException("Failed to insert exercise row into " + uri);
                break;
            case EXERCISES_WITH_DAY_ID:
                exerciseId = db.insert(ExerciseEntry.TABLE_NAME, null, values);
                if (exerciseId > 0)
                    returnUri = ExerciseEntry.buildExerciseId(exerciseId);
                else
                    throw new android.database.SQLException("Failed to insert exercise row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri in inset:  " + uri);
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case WORKOUTS:
                rowsDeleted = db.delete(WorkoutEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case WORKOUT_WITH_ID:
                long workoutId = WorkoutEntry.getWorkoutIdFromUri(uri);
                rowsDeleted = db.delete(WorkoutEntry.TABLE_NAME,
                        sWorkoutIdSelection,
                        new String[] {Long.toString(workoutId)});
                break;
            case DAYS:
                rowsDeleted = db.delete(DayEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case DAY_WITH_ID:
                long dayId = DayEntry.getDayIdFromUri(uri);
                rowsDeleted = db.delete(DayEntry.TABLE_NAME,
                        sDayIdSelection,
                        new String[] {Long.toString(dayId)});
                break;
            case DAYS_WITH_WORKOUT_ID:
                workoutId = DayEntry.getWorkoutIdFromUri(uri);
                rowsDeleted = db.delete(DayEntry.TABLE_NAME,
                        sDayWorkoutKeySelection,
                        new String[] {Long.toString(workoutId)});
                break;
            case EXERCISES:
                rowsDeleted = db.delete(ExerciseEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case EXERCISE_WITH_ID:
                long exerciseId = ExerciseEntry.getExerciseIdFromUri(uri);
                rowsDeleted = db.delete(ExerciseEntry.TABLE_NAME,
                        sExerciseIdSelection,
                        new String[] {Long.toString(exerciseId)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri in delete:  " + uri);
        }
        if (rowsDeleted != 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case WORKOUT_WITH_ID:
                long workoutId = WorkoutEntry.getWorkoutIdFromUri(uri);
                rowsUpdated = db.update(WorkoutEntry.TABLE_NAME,
                        values,
                        sWorkoutIdSelection,
                        new String[] {Long.toString(workoutId)});
                break;
            case DAY_WITH_ID:
                long dayId = DayEntry.getDayIdFromUri(uri);
                rowsUpdated = db.update(DayEntry.TABLE_NAME,
                        values,
                        sDayIdSelection,
                        new String[] {Long.toString(dayId)});
                break;
            case EXERCISE_WITH_ID:
                long exerciseId = ExerciseEntry.getExerciseIdFromUri(uri);
                rowsUpdated = db.update(ExerciseEntry.TABLE_NAME,
                        values,
                        sExerciseIdSelection,
                        new String[] {Long.toString(exerciseId)});
                break;
            case EXERCISES_WITH_DAY_ID:
                dayId = ExerciseEntry.getDayIdFromUri(uri);
                rowsUpdated = db.update(ExerciseEntry.TABLE_NAME,
                        values,
                        sExerciseDayIdSelection,
                        new String[] {Long.toString(dayId)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri in update:  " + uri);
        }
        if (rowsUpdated != 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case WORKOUTS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WorkoutEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case DAYS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DayEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case DAYS_WITH_WORKOUT_ID:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DayEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case EXERCISES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ExerciseEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case EXERCISES_WITH_DAY_ID:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ExerciseEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri in bulk insert");
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnCount;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WorkoutContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WorkoutContract.PATH_WORKOUT, WORKOUTS);
        matcher.addURI(authority, WorkoutContract.PATH_WORKOUT + "/#", WORKOUT_WITH_ID);

        matcher.addURI(authority, WorkoutContract.PATH_DAY, DAYS);
        matcher.addURI(authority, WorkoutContract.PATH_DAY + "/#", DAY_WITH_ID);
        matcher.addURI(authority, WorkoutContract.PATH_DAY + "/" + WorkoutContract.PATH_WORKOUT +
        "/#", DAYS_WITH_WORKOUT_ID);
        matcher.addURI(authority, WorkoutContract.PATH_DAY + "/" + WorkoutContract.PATH_WORKOUT +
                "/#/" + WorkoutContract.PATH_DAY + "/*", DAY_WITH_WORKOUT_ID_AND_DAY_OF_WEEK);

        matcher.addURI(authority, WorkoutContract.PATH_EXERCISE, EXERCISES);
        matcher.addURI(authority, WorkoutContract.PATH_EXERCISE + "/#", EXERCISE_WITH_ID);
        matcher.addURI(authority, WorkoutContract.PATH_EXERCISE + "/" +
                WorkoutContract.PATH_WORKOUT + "/#", EXERCISES_WITH_WORKOUT_ID);
        matcher.addURI(authority, WorkoutContract.PATH_EXERCISE + "/" + WorkoutContract.PATH_DAY +
                "/#", EXERCISES_WITH_DAY_ID);
        return matcher;
    }
}
