package com.cqing.project00.url;

/**
 * Created by Cqing on 2016/10/9.
 */

/**
 * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
 * http://api.themoviedb.org/3/movie/top_rated?api_key=[YOUR_API_KEY]
 * https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg
 * https://api.themoviedb.org/3/movie/157336/reviews?api_key=[YOUR_API_KEY]
 * https://api.themoviedb.org/3/movie/157336/videos?api_key=[YOUR_API_KEY]
 */
public class URL {
    private URL(){}
    public static final String HOST = "http://api.themoviedb.org/3/movie";
    public static final String API_KEY = "/?api_key=";

    public static final String POPULAR = HOST + "/popular";
    public static final String TOP_RATED = HOST + "/top_rated";
    public static final String SERVICE_IMAGE_URL = "http://image.tmdb.org/t/p";
    public static final String SERVICE_IMAGE_URL_PIC_SIZE = "/w500";

    public static final String API_KEY_VIDEO = "/videos?api_key=";
    public static final String API_KEY_REVIEW = "/reviews?api_key=";
}
