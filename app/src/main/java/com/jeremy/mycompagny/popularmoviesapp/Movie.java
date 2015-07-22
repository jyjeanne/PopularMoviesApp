package com.jeremy.mycompagny.popularmoviesapp;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie Model Object.
 *
 * @author Jeremy
 * @version 1.0.
 */
public class Movie implements Parcelable {

    private int id;
    private String title;
    private String posterPath;
    private String synopsis;
    private double userRating;
    private String releaseDate;

    public Movie() {

        this.id = 0;
        this.title = "unavailable";
        this.posterPath = "unavailable";
        this.synopsis = "unavailable";
        this.userRating = 0;
        this.releaseDate = "unavailable";

    }

    public void setMovieId(int movieID) {
        this.id = movieID;
    }

    public int getMovieID() {
        return id;
    }

    public void setMovieTitle(String movieTitle) {
        this.title = movieTitle;
    }

    public String getMovieTitle() {
        return title;
    }

    public void setMoviePosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getMoviePosterPath() {
        return posterPath;
    }

    public void setMovieSynopsis(String plotSynopsis) {
        this.synopsis = plotSynopsis;
    }

    public String getMovieSynopsis() {
        return synopsis;
    }

    public void setMovieUserRating(double userRating) {
        this.userRating = userRating;
    }

    public double getMovieUserRating() {
        return userRating;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.releaseDate = movieReleaseDate;
    }

    public String getMovieReleaseDate() {
        return releaseDate;
    }

    public String getPosterURL() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w185");

        return builder.build().toString() + posterPath;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(posterPath);
        parcel.writeString(synopsis);
        parcel.writeDouble(userRating);
        parcel.writeString(releaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        synopsis = in.readString();
        userRating = in.readDouble();
        releaseDate = in.readString();
    }
}