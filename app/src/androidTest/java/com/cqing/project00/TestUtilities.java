package com.cqing.project00;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.cqing.project00.data.PopMoviesContract;

import java.util.Map;
import java.util.Set;

import static com.cqing.project00.data.PopMoviesContract.PopMoviesEntry.COLUMN_MOVIE_ID;
import static com.cqing.project00.data.PopMoviesContract.PopMoviesEntry.COLUMN_ORIGINAL_TITLE;
import static com.cqing.project00.data.PopMoviesContract.PopMoviesEntry.COLUMN_OVERVIEW;
import static com.cqing.project00.data.PopMoviesContract.PopMoviesEntry.COLUMN_POSTER_PATH;
import static com.cqing.project00.data.PopMoviesContract.PopMoviesEntry.COLUMN_RELEASE_DATE;
import static com.cqing.project00.data.PopMoviesContract.PopMoviesEntry.COLUMN_VOTE_AVERAGE;

/**
 * Created by Cqing on 2016/10/27.
 */

public class TestUtilities extends AndroidTestCase{

    public static void validateCursor (String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor return" + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();

    }
    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
    public static ContentValues createValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_POSTER_PATH, "123.img");
        contentValues.put(COLUMN_OVERVIEW, "this is ...");
        contentValues.put(COLUMN_RELEASE_DATE, "2013-12-1");
        contentValues.put(COLUMN_MOVIE_ID, 1000001);
        contentValues.put(COLUMN_ORIGINAL_TITLE, "WTF");
        contentValues.put(COLUMN_VOTE_AVERAGE, 2.3);
        return contentValues;
    }

    public static ContentValues createReviewValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PopMoviesContract.ReviewEntry.COLUMN_REVIEW_URL, "http://1111111");
        contentValues.put(PopMoviesContract.ReviewEntry.COLUMN_REVIEW_ID, 101);
        contentValues.put(PopMoviesContract.ReviewEntry.COLUMN_AUTHOR, "Bob");
        contentValues.put(PopMoviesContract.ReviewEntry.COLUMN_MOVIE_ID, 1000001);
        contentValues.put(PopMoviesContract.ReviewEntry.COLUMN_CONTENT, "this is bla bla");

        return contentValues;
    }

    public static ContentValues createVideoValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PopMoviesContract.VideoEntry.COLUMN_VIDEO_ID, 123);
        contentValues.put(PopMoviesContract.VideoEntry.COLUMN_MOVIE_ID, 1000001);

        return contentValues;
    }
}
