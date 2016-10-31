package com.cqing.project00.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Cqing on 2016/10/21.
 */

public class PopMoviesContract {
    public static final String CONTENT_AUTHORITY = "com.cqing.project00";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_POPMOVIE = "movie";


    public static final class PopMoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPMOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPMOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_POPMOVIE;

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_GENRE_IDS = "genre_ids";

        public static Uri buildPopMoviesUri(long id) {
            int i = 0;
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildPopMoviesWithVideos(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id).buildUpon().appendPath("videos").build();
        }
        public static Uri buildPopMoviesWithReviews(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id).buildUpon().appendPath("reviews").build();
        }
    }
}
