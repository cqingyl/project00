package com.cqing.project00.bean;

/**
 * Created by Cqing on 2016/9/13.
 */

public class PopularMovies {
    private String imgUrl;
    private double popularity;

    public double getPopularity() {
        return popularity;
    }

    public double getVote_average() {
        return vote_average;
    }

    private double vote_average;

    public PopularMovies(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
