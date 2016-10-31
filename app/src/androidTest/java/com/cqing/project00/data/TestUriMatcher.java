package com.cqing.project00.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Cqing on 2016/10/24.
 */

public class TestUriMatcher extends AndroidTestCase{
    private final static Uri TEST_POPMOVIE_DIR = PopMoviesContract.PopMoviesEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = PopMoviesProvider.buildUriMatcher();
        assertEquals("Error: The WEATHER URI was matched incorrectly.", testMatcher.match(TEST_POPMOVIE_DIR), PopMoviesProvider.POPMOVIE);
    }
}

