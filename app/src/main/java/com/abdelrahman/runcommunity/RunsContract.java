package com.abdelrahman.runcommunity;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by abdalrahman on 1/22/2018.
 */

public class RunsContract {

    public static final String CONTENT_AUTHORITY = "com.abdelrahman.runcommunity";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RUNS = "runs";


    public static final class RunsEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RUNS)
                .build();

        public static final String TABLE_NAME = "runs";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_DURATION = "duration";


        public static Uri buildWeatherUriWithID(long ID) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(ID))
                    .build();
        }
    }
}
