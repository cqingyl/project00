package com.cqing.project00.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Cqing on 2016/10/24.
 */

public class TestMovieContract extends AndroidTestCase {

    private static final long TEST_ID = 789456123L;

    public void testBuildPopMoviesUri() {
       Uri uri = PopMoviesContract.PopMoviesEntry.buildPopMoviesUri(TEST_ID);
       assertNotNull("Error: Null Uri returned.  You must fill-in buildPopMoviesUri in " +
                       "PopMoviesContract.",
               uri);

       assertEquals("Error: Movie Uri doesn't match our expected result",
               uri.toString(),
               "content://com.cqing.project00/movie/789456123");
        assertEquals("Error: Movie Uri doesn't match our expected result",
                PopMoviesContract.VideoEntry.buildPopMoviesVideoUri(TEST_ID).toString(),
                "content://com.cqing.project00/videos/789456123");
        assertEquals("Error: Movie Uri doesn't match our expected result",
                PopMoviesContract.ReviewEntry.buildPopMoviesReviewUri(TEST_ID).toString(),
                "content://com.cqing.project00/reviews/789456123");

   }
}
