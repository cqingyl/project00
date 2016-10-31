package com.cqing.project00.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cqing.project00.data.PopMoviesContract.PopMoviesEntry;
/**
 * Created by Cqing on 2016/10/21.
 */

public class PopMoviesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "popmovies.db";

    public PopMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + PopMoviesEntry.TABLE_NAME + " (" +
                PopMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                PopMoviesEntry.COLUMN_ADULT + " TEXT," +
                PopMoviesEntry.COLUMN_OVERVIEW + " INTEGER NOT NULL," +
                PopMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                PopMoviesEntry.COLUMN_ID + " TEXT NOT NULL," +
                PopMoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL," +
                PopMoviesEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT," +
                PopMoviesEntry.COLUMN_TITLE + " TEXT," +
                PopMoviesEntry.COLUMN_BACKDROP_PATH + " TEXT," +
                PopMoviesEntry.COLUMN_POPULARITY + " REAL," +
                PopMoviesEntry.COLUMN_VOTE_COUNT + " INTEGER," +
                PopMoviesEntry.COLUMN_VIDEO + " TEXT," +
                PopMoviesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                PopMoviesEntry.COLUMN_GENRE_IDS + " TEXT," +
                " UNIQUE (" + PopMoviesEntry.COLUMN_ID + ", "+ PopMoviesEntry.COLUMN_VOTE_AVERAGE + ", " + PopMoviesEntry.COLUMN_POPULARITY +
                ") ON CONFLICT REPLACE" +
                ");";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  PopMoviesContract.PopMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
