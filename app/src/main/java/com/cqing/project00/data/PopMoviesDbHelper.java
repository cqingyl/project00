package com.cqing.project00.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cqing.project00.data.PopMoviesContract.PopMoviesEntry;

import static com.cqing.project00.data.PopMoviesContract.PopMoviesEntry.TABLE_NAME;

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

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + PopMoviesContract.ReviewEntry.TABLE_NAME + " (" +
                PopMoviesContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopMoviesContract.ReviewEntry.COLUMN_REVIEW_ID + " INTEGER NOT NULL," +
                PopMoviesContract.ReviewEntry.COLUMN_AUTHOR+ " TEXT," +
                PopMoviesContract.ReviewEntry.COLUMN_CONTENT+ " TEXT," +
                PopMoviesContract.ReviewEntry.COLUMN_REVIEW_URL+ " TEXT" +
                ");";
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
            final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + PopMoviesContract.VideoEntry.TABLE_NAME + " (" +
                PopMoviesContract.VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopMoviesContract.VideoEntry.CONTENT_VIDEO_ID + " INTEGER NOT NULL," +
                PopMoviesContract.VideoEntry.CONTENT_KEY + " TEXT," +
                PopMoviesContract.VideoEntry.CONTENT_NAME + " INTEGER," +
                PopMoviesContract.VideoEntry.CONTENT_SITE + " TEXT," +
                PopMoviesContract.VideoEntry.CONTENT_SIZE + " INTEGER" +
                ");";
        db.execSQL(SQL_CREATE_VIDEO_TABLE);

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                PopMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                PopMoviesEntry.COLUMN_ADULT + " INTEGER," +
                PopMoviesEntry.COLUMN_OVERVIEW + " INTEGER NOT NULL," +
                PopMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                PopMoviesEntry.COLUMN_ID + " INTEGER NOT NULL," +
                PopMoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL," +
                PopMoviesEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT," +
                PopMoviesEntry.COLUMN_TITLE + " TEXT," +
                PopMoviesEntry.COLUMN_BACKDROP_PATH + " TEXT," +
                PopMoviesEntry.COLUMN_POPULARITY + " REAL," +
                PopMoviesEntry.COLUMN_VOTE_COUNT + " INTEGER," +
                PopMoviesEntry.COLUMN_VIDEO + " INTEGER," +
                PopMoviesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                PopMoviesEntry.COLUMN_GENRE_IDS + " TEXT," +
                PopMoviesEntry.COLUMN_COLLECTION + " INTEGER," +
                //把Reivew表当作外键
                " FOREIGN KEY (" + PopMoviesEntry.COLUMN_REVIEW_KEY + ") REFERENCES " +
                PopMoviesContract.ReviewEntry.TABLE_NAME + " (" + PopMoviesContract.ReviewEntry._ID + "), " +
                //把video表当作外键
                " FOREIGN KEY (" + PopMoviesEntry.COLUMN_VIDEO_KEY + ") REFERENCES " +
                PopMoviesContract.VideoEntry.TABLE_NAME + " (" + PopMoviesContract.VideoEntry._ID + "), " +
                //这一段sql保证了插入的时候，如果（date，loc_key）出现重复，就会替代原先的。
                " UNIQUE (" + PopMoviesEntry.COLUMN_ID + ", "+ PopMoviesEntry.COLUMN_VOTE_AVERAGE + ", " + PopMoviesEntry.COLUMN_POPULARITY +
                ") ON CONFLICT REPLACE" +
                ");";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
        onCreate(db);
    }
}
