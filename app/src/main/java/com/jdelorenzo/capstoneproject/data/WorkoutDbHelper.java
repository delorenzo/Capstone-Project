package com.jdelorenzo.capstoneproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jdelorenzo.capstoneproject.data.WorkoutContract.WorkoutEntry;
import com.jdelorenzo.capstoneproject.data.WorkoutContract.DayEntry;
import com.jdelorenzo.capstoneproject.data.WorkoutContract.ExerciseEntry;

public class WorkoutDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "workout.db";

    public WorkoutDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WORKOUT_TABLE = "CREATE TABLE " +
                WorkoutEntry.TABLE_NAME + " (" +
                WorkoutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WorkoutEntry.COLUMN_DAY + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + WorkoutEntry.COLUMN_DAY + ") REFERENCES " +
                DayEntry.TABLE_NAME + "(" + DayEntry.COLUMN_WORKOUT_KEY + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE" +
                ");";

        final String SQL_CREATE_DAY_TABLE = "CREATE TABLE " +
                DayEntry.TABLE_NAME + " ( " +
                DayEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DayEntry.COLUMN_DAY_OF_WEEK + " INTEGER NOT NULL, " +
                DayEntry.COLUMN_EXERCISE + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + DayEntry.COLUMN_EXERCISE + ") REFERENCES " +
                ExerciseEntry.TABLE_NAME + "(" + ExerciseEntry.COLUMN_DAY_KEY + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE" +
                ");";

        final String SQL_CREATE_EXERCISE_TABLE = "CREATE TABLE " +
                ExerciseEntry.TABLE_NAME + " ( " +
                ExerciseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ExerciseEntry.COLUMN_DAY_KEY + " INTEGER NOT NULL, " +
                ExerciseEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ExerciseEntry.COLUMN_REPS + " TEXT, " +
                ExerciseEntry.COLUMN_SETS + " INTEGER, " +
                ExerciseEntry.COLUMN_WEIGHT + " TEXT, " +
                ");";

        db.execSQL(SQL_CREATE_WORKOUT_TABLE);
        db.execSQL(SQL_CREATE_DAY_TABLE);
        db.execSQL(SQL_CREATE_EXERCISE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DayEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExerciseEntry.TABLE_NAME);
        onCreate(db);
    }
}
