package com.gd.downloader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper
{
    private final String TAG = getClass().getSimpleName();
    private static final int VERSION = 1;
    private SQLiteDatabase db;

    private static final String CREATE = "CREATE TABLE " + TaskEntry.TABLE_NAME
            + " (" +
            TaskEntry._ID + " INTEGER PRIMARY KEY, " +
            TaskEntry.BYTES + " INTEGER, " +
            TaskEntry.PATH + " TEXT, " +
            TaskEntry.URL + " TEXT, " +
            TaskEntry.STATE + " TEXT" +
            ")";


    public static class TaskEntry implements BaseColumns
    {
        public static final String BYTES = "BYTES" ;
        public static final String PATH = "PATH";
        public static final String URL = "URL" ;
        public static final String STATE = "STATE" ;
        public static final String SIZE = "SIZE";
        public static String TABLE_NAME = "DownloadTask";
    }

    public DBHelper(Context context)
    {
        super(context, TaskEntry.TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE);
        Log.d(TAG, "onCreate: executing create statement");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
        db.execSQL(CREATE);
        Log.d(TAG, "onUpgrade: updating database");
    }

    public void open()
    {
        db = getWritableDatabase();
    }

    public void close()
    {
        close();
    }

    public long insert(DownloadTask task)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskEntry.URL, task.getUrl());
        contentValues.put(TaskEntry.PATH, task.getPath());
        contentValues.put(TaskEntry.BYTES, task.getBytes());
        contentValues.put(TaskEntry.SIZE, task.getSize());
        contentValues.put(TaskEntry.STATE, task.getState().toString());

        return db.insert(TaskEntry.TABLE_NAME, null, contentValues);
    }
}
