package com.abdelrahman.runcommunity;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by abdalrahman on 1/22/2018.
 */

public class RunsProvider extends ContentProvider {

    public static final int CODE_RUNS = 100;
    public static final int CODE_RUNS_WITH_ID = 101;


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RunsDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RunsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, RunsContract.PATH_RUNS, CODE_RUNS);
        matcher.addURI(authority, RunsContract.PATH_RUNS+ "/#", CODE_RUNS_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {

        mOpenHelper = new RunsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case CODE_RUNS_WITH_ID: {
                String normalizedUtcDateString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{normalizedUtcDateString};
                cursor = mOpenHelper.getReadableDatabase().query(
                        RunsContract.RunsEntry.TABLE_NAME,
                        projection,
                        RunsContract.RunsEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CODE_RUNS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        RunsContract.RunsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {

            case CODE_RUNS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RunsContract.RunsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_RUNS : {
                db.beginTransaction();
                try {
                    long _id = db.insert(RunsContract.RunsEntry.TABLE_NAME, null, contentValues);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;
            }

        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_RUNS:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        RunsContract.RunsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
