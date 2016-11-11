package com.cqing.project00.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.cqing.project00.TestUtilities;

/**
 * Created by Cqing on 2016/10/27.
 */

public class TestProvider extends AndroidTestCase{

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                PopMoviesContract.PopMoviesEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                PopMoviesContract.PopMoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                PopMoviesProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + PopMoviesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, PopMoviesContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }
    public void testGetType() {
        // content:/com.cqing.project00.data/movie
        String type = mContext.getContentResolver().getType(PopMoviesContract.PopMoviesEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.cqing.project00.data/movie
        assertEquals("Error: the PopMoviesEntry CONTENT_URI should return PopMoviesEntry.CONTENT_TYPE",
                PopMoviesContract.PopMoviesEntry.CONTENT_TYPE, type);

        long testMoiveId = 789456;
        // content://com.cqing.project00.data/movie
        type = mContext.getContentResolver().getType(
                PopMoviesContract.PopMoviesEntry.buildPopMoviesWithMovieId(testMoiveId));
        // vnd.android.cursor.dir/com.cqing.project00.data/movie/789456
        assertEquals("Error: the PopMoviesEntry CONTENT_URI with movie id should return PopMoviesEntry.CONTENT_ITEM_TYPE",
                PopMoviesContract.PopMoviesEntry.CONTENT_ITEM_TYPE, type);
    }
    public void testBasicWeatherQuery() {
        // insert our test records into the database
        PopMoviesDbHelper dbHelper = new PopMoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createValues();

        long popMovieRowId = db.insert(PopMoviesContract.PopMoviesEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to Insert PopMovieEntry into the Database", popMovieRowId != -1);

        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                PopMoviesContract.PopMoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieQueries, movie query", cursor, testValues);
    }

}
