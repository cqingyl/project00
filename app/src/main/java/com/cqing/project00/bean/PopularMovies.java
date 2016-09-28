package com.cqing.project00.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cqing on 2016/9/13.
 */

public class PopularMovies implements Parcelable{
    private String imgUrl;
    private double popularity;
    private double vote_average;
    private String overview;
    private String release_date;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public PopularMovies() {
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setVoteAverage(double vote_average) {
        this.vote_average = vote_average;
    }

    public double getVoteAverage() {
        return vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgUrl);
        dest.writeDouble(popularity);
        dest.writeDouble(vote_average);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(title);
    }

    protected PopularMovies(Parcel in) {
        imgUrl = in.readString();
        popularity = in.readDouble();
        vote_average = in.readDouble();
        overview = in.readString();
        release_date = in.readString();
        title = in.readString();
    }

    public static final Creator<PopularMovies> CREATOR = new Creator<PopularMovies>() {
        @Override
        public PopularMovies createFromParcel(Parcel in) {
            return new PopularMovies(in);
        }

        @Override
        public PopularMovies[] newArray(int size) {
            return new PopularMovies[size];
        }
    };

}
