package com.abdelrahman.runcommunity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.abdelrahman.runcommunity.RunsContract.*;

/**
 * Created by abdalrahman on 1/22/2018.
 */

public class RunsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "runs.db";
    private static final int DATABASE_VERSION = 1;

/*
    public RunsDbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    } */
    public RunsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_RUNS_TABLE = "CREATE TABLE "  +   RunsEntry.TABLE_NAME +" ("  +

                RunsEntry._ID             +   " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                RunsEntry.COLUMN_DATE     +   " TEXT NOT NULL, "                       +
                RunsEntry.COLUMN_DISTANCE +   " INTEGER NOT NULL, "                     +
                RunsEntry.COLUMN_DURATION +   " INTEGER NOT NULL " +");";
        sqLiteDatabase.execSQL(SQL_CREATE_RUNS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}


}
