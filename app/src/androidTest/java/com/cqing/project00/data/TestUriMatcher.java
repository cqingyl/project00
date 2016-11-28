package com.cqing.project00.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Cqing on 2016/10/24.
 */

public class TestUriMatcher extends AndroidTestCase{
    private final static Uri TEST_POPMOVIE_DIR = PopMoviesContract.PopMoviesEntry.CONTENT_URI;
    private final static Uri TEST_POPMOVIE_WITH_ID_DIR = PopMoviesContract.PopMoviesEntry.buildPopMoviesWithMovieId(123456);

    private final static Uri TEST_POPMOVIE_WITH_REVIEW_DIR = PopMoviesContract.PopMoviesEntry.buildPopMoviesWithReviews(123456);
    private final static Uri TEST_POPMOVIE_WITH_VIDEO_DIR = PopMoviesContract.PopMoviesEntry.buildPopMoviesWithVideos(123456);
    private final static Uri TEST_POPMOVIE_WITH_MERGE_DIR = PopMoviesContract.PopMoviesEntry.buildPopMoviesWithRT(123456);

    public void testUriMatcher() {
        UriMatcher testMatcher = PopMoviesProvider.buildUriMatcher();
        assertEquals("Error: The WEATHER URI was matched incorrectly.", testMatcher.match(TEST_POPMOVIE_WITH_ID_DIR), PopMoviesProvider.MOVIE_WITH_MOVIE_ID);
        assertEquals("Error: The WEATHER URI was matched incorrectly.", testMatcher.match(TEST_POPMOVIE_WITH_REVIEW_DIR), PopMoviesProvider.MOVIE_WITH_MOVIE_ID_WITH_REVIEWS);
        assertEquals("Error: The WEATHER URI was matched incorrectly.", testMatcher.match(TEST_POPMOVIE_WITH_VIDEO_DIR), PopMoviesProvider.MOVIE_WITH_MOVIE_ID_WITH_VIDEOS);
        assertEquals("Error: The WEATHER URI was matched incorrectly.", testMatcher.match(TEST_POPMOVIE_WITH_MERGE_DIR), PopMoviesProvider.MOVIE_WITH_MOVIE_ID_WITH_MERGE);
    }
}

