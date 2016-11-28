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
        mContext.getContentResolver().delete(
                PopMoviesContract.ReviewEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                PopMoviesContract.VideoEntry.CONTENT_URI,
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

        long testMovieId = 789456;
        // content://com.cqing.project00.data/movie
        type = mContext.getContentResolver().getType(
                PopMoviesContract.PopMoviesEntry.buildPopMoviesWithMovieId(testMovieId));
        // vnd.android.cursor.dir/com.cqing.project00.data/movie/789456
        assertEquals("Error: the PopMoviesEntry CONTENT_URI with movie id should return PopMoviesEntry.CONTENT_ITEM_TYPE",
                PopMoviesContract.PopMoviesEntry.CONTENT_ITEM_TYPE, type);
        // content://com.cqing.project00.data/movie/789456/reviews
        type = mContext.getContentResolver().getType(PopMoviesContract.PopMoviesEntry.buildPopMoviesWithReviews(testMovieId));
        assertEquals("Error: the PopMoviesEntry CONTENT_URI with movie id and review should return PopMoviesEntry.CONTENT_TYPE",
                PopMoviesContract.PopMoviesEntry.CONTENT_TYPE, type);
        // content://com.cqing.project00.data/movie/789456/reviews
        type = mContext.getContentResolver().getType(PopMoviesContract.PopMoviesEntry.buildPopMoviesWithVideos(testMovieId));
        assertEquals("Error: the PopMoviesEntry CONTENT_URI with movie id and video should return PopMoviesEntry.CONTENT_TYPE",
                PopMoviesContract.PopMoviesEntry.CONTENT_TYPE, type);
        // content://com.cqing.project00.data/movie/789456/merge
        type = mContext.getContentResolver().getType(PopMoviesContract.PopMoviesEntry.buildPopMoviesWithRT(testMovieId));
        assertEquals("Error: the PopMoviesEntry CONTENT_URI with movie id and merge should return PopMoviesEntry.CONTENT_TYPE",
                PopMoviesContract.PopMoviesEntry.CONTENT_TYPE, type);
    }
    public void testBasicWeatherQuery() {
        // insert our test records into the database
        PopMoviesDbHelper dbHelper = new PopMoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues1 = TestUtilities.createValues();

        long popMovieRowId = db.insert(PopMoviesContract.PopMoviesEntry.TABLE_NAME, null, testValues1);
        assertTrue("Unable to Insert PopMovieEntry into the Database", popMovieRowId != -1);


        //movie
        Cursor cursor1 = mContext.getContentResolver().query(
                PopMoviesContract.PopMoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieQueries, movie query", cursor1, testValues1);
        cursor1.close();
        //review

        ContentValues testValues2 = TestUtilities.createReviewValues();
        popMovieRowId = db.insert(PopMoviesContract.ReviewEntry.TABLE_NAME, null, testValues2);
        assertTrue("Unable to Insert PopMovieEntry into the Database", popMovieRowId != -1);

        Cursor cursor2 = mContext.getContentResolver().query(
                PopMoviesContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieQueries, movie query", cursor2, testValues2);
        //video
        ContentValues testValues3 = TestUtilities.createVideoValues();
        popMovieRowId = db.insert(PopMoviesContract.VideoEntry.TABLE_NAME, null, testValues3);
        assertTrue("Unable to Insert PopMovieEntry into the Database", popMovieRowId != -1);

        Cursor cursor3 = mContext.getContentResolver().query(
                PopMoviesContract.VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieQueries, movie query", cursor3, testValues3);
        db.close();
    }

}
