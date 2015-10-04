package com.jeremy.mycompagny.popularmoviesapp.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jeremy .
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.jeremy.mycompagny.popularmoviesapp";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    /* Inner class that defines the table contents of the location table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

        // Column names
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "backdrop_path";

        public  static final String COLUMN_MOVIE_RELASE_DATE = "release_date";

        public  static final  String COLUMN_MOVIE_POSTER_PATH = "poster_path";

        public static final String COLUMN_MOVIE_POPULARITY = "popularity";

        public static final String COLUMN_MOVIE_VOTE_AVG = "vote_avg";

        public static final String COLUMN_MOVIE_VOTE_COUNT = "vote_count";

        public static final String COLUMN_MOVIE_TITLE = "title";

        public static final String COLUMN_MOVIE_ORIGINAL_TITLE = "original_title";

        public static final String COLUMN_MOVIE_OVERVIEW = "overview";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieURL(String image) {
            return CONTENT_URI.buildUpon().appendPath(image).build();
        }

        public static Uri buildMoviesURL() {
            return CONTENT_URI.buildUpon().build();
        }

        public static String getImageFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static  final class MovieTrailerEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUM_TRAILER_KEY = "key";

        public static final String COLUMN_TRAILER_NAME = "name";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class MovieReviewEntry implements  BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_REVIEW_AUTHOR = "author";

        public static final String COLUMN_REVIEW_CONTENT = "content";

        public static Uri buildReviewUri(String reviewId, long movieId) {
            return CONTENT_URI.buildUpon().appendPath(reviewId).appendQueryParameter(COLUMN_MOVIE_ID, Long.toString(movieId)).build();
        }

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

}
