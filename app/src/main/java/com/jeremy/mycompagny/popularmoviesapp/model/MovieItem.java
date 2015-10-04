package com.jeremy.mycompagny.popularmoviesapp.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * MovieItem Model Object.
 */
public class MovieItem implements Parcelable {

    public static final String ID = "id";
    public static final String POSTER_PATH = "poster_path";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String OVERVIEW = "overview";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String RELEASE_DATE = "release_date";
    public static final String POPULARITY = "popularity";
    public static final String VOTE_COUNT= "vote_count";
    public static final String BACKDROP_PATH = "backdrop_path";
    public static final String TITLE = "title";

    private String id;
    private String originalTitle;
    private String posterPath;
    private String overview;
    private String userRating;
    private String releaseDate;
    private String popularity;
    private String backdropPath;
    private String title;
    private String voteCount;
    private boolean isFavorite;
    private ArrayList<MovieReview> movieReviews;
    private ArrayList<MovieTrailer> movieTrailers;

    public MovieItem() {

        this.id = "0";
        this.title = "unavailable";
        this.posterPath = "unavailable";
        this.backdropPath = "unavailable";
        this.overview = "unavailable";
        this.userRating = "0";
        this.releaseDate = "unavailable";

    }

    public MovieItem(
            String movieId,
            String originalTitle,
            String moviePoster,
            String overview,
            String vote_average,
            String releaseDate,
            String popularity,
            String voteCount,
            String backdropPath,
            String title,
            boolean isFavorite){

        this.setId(movieId);
        this.setMovieOriginalTitle(originalTitle);
        this.setMoviePosterPath(moviePoster);
        this.setMovieOverview(overview);
        this.setMovieUserRating(vote_average);
        this.setMovieReleaseDate(releaseDate);
        this.setMoviePopularity(popularity);
        this.setMovieVoteCount(voteCount);
        this.setBackdropPath(backdropPath);
        this.setMovieTitle(title);
        setIsFavorite(isFavorite);
    }

    public void setId(String movieID) {
        this.id = movieID;
    }

    public String getId() {
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

    public String getMovieOverview() {
        return overview;
    }

    public void setMovieOverview(String overview) {
        this.overview = overview;
    }

    public String getMoviePopularity() {
        return popularity;
    }

    public void setMoviePopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getMovieOriginalTitle() {
        return originalTitle;
    }

    public void setMovieOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setMovieUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getMovieUserRating() {
        return userRating;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.releaseDate = movieReleaseDate;
    }

    public String getMovieReleaseDate() {
        return releaseDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getMovieVoteCount() {
        return voteCount;
    }

    public void setMovieVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public ArrayList<MovieReview> getMovieReviews() {
        return movieReviews;
    }

    public void setMovieReviews(ArrayList<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
    }

    public ArrayList<MovieTrailer> getMovieTrailers() {
        return movieTrailers;
    }

    public void setMovieTrailers(ArrayList<MovieTrailer> movieTrailers) {
        this.movieTrailers = movieTrailers;
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
        parcel.writeString(id);
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
        parcel.writeString(popularity);
        parcel.writeString(backdropPath);
        parcel.writeString(title);
        parcel.writeString(voteCount);
        parcel.writeByte((byte) (isFavorite ? 1 : 0));
        parcel.writeList(movieReviews);
        parcel.writeList(movieTrailers);
    }



    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    private MovieItem(Parcel in) {
        id = in.readString();
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
        popularity = in.readString();
        backdropPath = in.readString();
        title = in.readString();
        voteCount = in.readString();
        isFavorite=in.readByte() != 0;
        movieReviews=in.readArrayList(null);
        movieTrailers=in.readArrayList(null);
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}
