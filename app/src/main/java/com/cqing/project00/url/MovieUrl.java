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
public class MovieUrl {
    private MovieUrl(){}
    public static final String HOST = "http://api.themoviedb.org/3/movie";
    public static final String SERVICE_IMAGE_URL = "http://image.tmdb.org/t/p";
    public static final String SERVICE_IMAGE_URL_PIC_SIZE = "/w500";
}
