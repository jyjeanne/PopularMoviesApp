package com.jeremy.mycompagny.popularmoviesapp.model;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Trailer model object.
 */
public class MovieTrailer implements Serializable {

    public static final String ID = "id";
    public  static final String NAME = "name";
    public  static final String KEY="key";



    private String movieId;
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;

    public MovieTrailer() {

    }

    public MovieTrailer(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.key = trailer.getString("key");
        this.name = trailer.getString("name");
        this.site = trailer.getString("site");
        this.type = trailer.getString("type");
    }

    protected MovieTrailer(Parcel in) {
    }

    public MovieTrailer(String movieId, String id, String name, String key) {
        this.movieId = movieId;
        this.id = id;
        this.name = name;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public String getKey() { return key; }

    public String getName() { return name; }

    public String getSite() { return site; }

    public String getType() { return type; }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
