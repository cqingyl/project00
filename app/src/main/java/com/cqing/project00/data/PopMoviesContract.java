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
    public static final String PATH_REVIEW = "reviews";
    public static final String PATH_VIDEO = "videos";

    /***
     * 预告片实体
     */
    public static final class VideoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String TABLE_NAME = "videos";
        public static final String CONTENT_VIDEO_ID = "video_id";
        public static final String CONTENT_KEY = "name";
        public static final String CONTENT_NAME = "KEY";
        public static final String CONTENT_SITE = "site";
        public static final String CONTENT_SIZE = "size";

        public static Uri buildPopMoviesVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    /***
     * 评论实体
     */
    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_REVIEW;

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_REVIEW_URL = "url";

        public static Uri buildPopMoviesReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    /**
     * 电影实体
     * */
    public static final class PopMoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPMOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPMOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_POPMOVIE;

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_GENRE_IDS = "genre_ids";
        public static final String COLUMN_COLLECTION = "collection";
        public static final String COLUMN_REVIEW_KEY = "review_id";
        public static final String COLUMN_VIDEO_KEY = "video_id";

        public static Uri buildPopMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildPopMoviesWithMovieId(long movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build();
        }
        public static Long getMovieId(Uri uri) {
            return Long.valueOf(uri.getPathSegments().get(1));
        }
        public static Uri buildPopMoviesWithReviews(long movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).appendPath(ReviewEntry.TABLE_NAME).build();
        }
        public static Uri buildPopMoviesWithVideos(long movieId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).appendPath(VideoEntry.TABLE_NAME).build();
        }
    }

}
