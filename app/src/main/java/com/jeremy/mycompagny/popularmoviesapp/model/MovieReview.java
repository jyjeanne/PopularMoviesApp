package com.jeremy.mycompagny.popularmoviesapp.model;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Review model object.
 */
public class MovieReview implements Serializable {
    public static final String ID = "id";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";

    private String id;
    private String author;
    private String content;
    private String movieId;


    public MovieReview() {

    }

    public MovieReview(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.author = trailer.getString("author");
        this.content = trailer.getString("content");
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public MovieReview(String author, String id, String content, String movieId) {
        this.author = author;
        this.id = id;
        this.content = content;
        this.movieId = movieId;
    }

    protected MovieReview(Parcel in) {
    }
}
