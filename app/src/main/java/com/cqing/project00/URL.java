package com.cqing.project00;

/**
 * Created by Cqing on 2016/10/9.
 */

/**
 * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
 */
public class URL {
    private URL(){}
    public static final String Host = "http://api.themoviedb.org";
    public static final String API_KEY = "api_key=";
    public static final String POPULAR = Host + "/3/movie/popular?";
    public static final String TOP_RATED = Host + "/3/movie/top_rated?";
    public static final String SERVICE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String SERVICE_IMAGE_URL_PICSIZE = "w185";
    public static final String KEY_MOVIE_POSITION = "position";


}
