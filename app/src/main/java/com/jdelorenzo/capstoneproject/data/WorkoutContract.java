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

    public static final class WorkoutEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORKOUT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORKOUT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +  CONTENT_AUTHORITY + "/" + PATH_WORKOUT;

        public static final String TABLE_NAME = "workout";

        public static final String COLUMN_DAY = "day";

        public static Uri buildWorkoutUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWorkoutDay(long day) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_DAY, Long.toString(day)).build();
        }

        public long getDayFromUri(Uri uri) {
            String day = uri.getQueryParameter(COLUMN_DAY);
            if (null != day && day.length() > 0) {
                return Long.parseLong(day);
            }
            else {
                return 0;
            }
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
        public static final String COLUMN_EXERCISE = "exercise";

        public static Uri buildDayUri(long id) {
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

        public static Uri buildExerciseUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
