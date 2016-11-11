package com.cqing.project00.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.cqing.project00.data.PopMoviesContract.PopMoviesEntry;

import java.util.HashSet;

/**
 * Created by Cqing on 2016/10/24.
 */

public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();
    public void deletedTheDatabase() {
        mContext.deleteDatabase(PopMoviesDbHelper.DATABASE_NAME);
    }
    public void setUp() {
        deletedTheDatabase();
    }
    public void testCreateDb() throws Throwable{
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(PopMoviesEntry.TABLE_NAME);

        mContext.deleteDatabase(PopMoviesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new PopMoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );
        assertTrue("Error: Your database was created without movie entry tables",
                tableNameHashSet.isEmpty());
        c = db.rawQuery("PRAGMA table_info(" + PopMoviesContract.PopMoviesEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(PopMoviesEntry._ID);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_POSTER_PATH);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_ADULT);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_OVERVIEW);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_RELEASE_DATE);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_ID);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_ORIGINAL_TITLE);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_ORIGINAL_LANGUAGE);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_TITLE);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_BACKDROP_PATH);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_POPULARITY);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_VOTE_COUNT);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_VIDEO);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_VOTE_AVERAGE);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_GENRE_IDS);
        locationColumnHashSet.add(PopMoviesEntry.COLUMN_COLLECTION);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

}
