package com.jdelorenzo.capstoneproject.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class WorkoutContract {
    public static final String CONTENT_AUTHORITY = "com.jdelorenzo.casptoneproject.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_WORKOUT = "workout";
    public static final String PATH_DAY = "day";
    public static final String PATH_EXERCISE = "exercise";
    public static final String PATH_NAME = "name";

    public static final class WorkoutEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORKOUT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +  CONTENT_AUTHORITY + "/" + PATH_WORKOUT;

        public static final String TABLE_NAME = "workout";

        public static final String COLUMN_NAME = "name";

        public static Uri buildWorkoutId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWorkoutName(String name) {
            return CONTENT_URI.buildUpon().appendQueryParameter(PATH_NAME, name).build();
        }

        public static String getWorkoutNameFromUri(Uri uri) {
            String name = uri.getPathSegments().get(2);
            return uri.getPathSegments().get(2);
        }
    }

    public static final class DayEntry implements  BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DAY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DAY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DAY;

        public static final String TABLE_NAME = "day";

        public static final String COLUMN_WORKOUT_KEY = "workout_id";
        public static final String COLUMN_DAY_OF_WEEK = "day_of_week";

        public static Uri buildDayId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ExerciseEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXERCISE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE;

        public static final String TABLE_NAME = "exercise";

        public static final String COLUMN_DAY_KEY = "day_id";
        public static final String COLUMN_REPS = "repetitions";
        public static final String COLUMN_SETS = "sets";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_DESCRIPTION = "description";

        public static Uri buildExerciseId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
