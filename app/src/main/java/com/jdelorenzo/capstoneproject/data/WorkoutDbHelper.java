package com.jdelorenzo.capstoneproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jdelorenzo.capstoneproject.data.WorkoutContract.*;

public class WorkoutDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "workout.db";

    public WorkoutDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ROUTINE_TABLE = "CREATE TABLE " +
                RoutineEntry.TABLE_NAME + " (" +
                RoutineEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RoutineEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RoutineEntry.COLUMN_LAST_MODIFIED + " TEXT" +
                " );";

        final String SQL_CREATE_DAY_TABLE = "CREATE TABLE " +
                DayEntry.TABLE_NAME + " ( " +
                DayEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DayEntry.COLUMN_ROUTINE_KEY + " INTEGER NOT NULL, " +
                DayEntry.COLUMN_DAY_OF_WEEK + " INTEGER NOT NULL, " +
                DayEntry.COLUMN_LAST_DATE + " TEXT, " +
                "FOREIGN KEY (" + DayEntry.COLUMN_ROUTINE_KEY + ") REFERENCES " +
                RoutineEntry.TABLE_NAME + "(" + RoutineEntry._ID + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE " +
                " );";

        final String SQL_CREATE_EXERCISE_TABLE = "CREATE TABLE " +
                ExerciseEntry.TABLE_NAME + " ( " +
                ExerciseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ExerciseEntry.COLUMN_DAY_KEY + " INTEGER NOT NULL, " +
                ExerciseEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ExerciseEntry.COLUMN_REPS + " INTEGER, " +
                ExerciseEntry.COLUMN_SETS + " INTEGER, " +
                ExerciseEntry.COLUMN_WEIGHT + " REAL, " +
                "FOREIGN KEY (" + ExerciseEntry.COLUMN_DAY_KEY + ") REFERENCES " +
                DayEntry.TABLE_NAME + "(" + DayEntry._ID + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE " +
                " );";

        final String SQL_CREATE_WEIGHT_TABLE = "CREATE TABLE " +
                WeightEntry.TABLE_NAME + " ( " +
                WeightEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeightEntry.COLUMN_EXERCISE_KEY + " INTEGER NOT NULL, " +
                WeightEntry.COLUMN_WEIGHT + " REAL NOT NULL, " +
                WeightEntry.COLUMN_DATE + " TEXT, " +
                "FOREIGN KEY (" + WeightEntry.COLUMN_EXERCISE_KEY + ") REFERENCES " +
                ExerciseEntry.TABLE_NAME + "(" + ExerciseEntry._ID + ") " +
                "ON DELETE CASCADE ON UPDATE CASCADE);";

        db.execSQL(SQL_CREATE_ROUTINE_TABLE);
        db.execSQL(SQL_CREATE_DAY_TABLE);
        db.execSQL(SQL_CREATE_EXERCISE_TABLE);
        db.execSQL(SQL_CREATE_WEIGHT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RoutineEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DayEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExerciseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeightEntry.TABLE_NAME);
        onCreate(db);
    }
}
