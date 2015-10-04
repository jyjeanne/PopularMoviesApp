package com.jeremy.mycompagny.popularmoviesapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jeremy on 30/09/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "popularmovies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final  String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " ("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY,"
                + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY + " REAL NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_RELASE_DATE + " STRING NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG + " REAL NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT + " REAL NOT NULL "
                + ");" ;

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " +
                MovieContract.MovieTrailerEntry.TABLE_NAME + " ("
                + MovieContract.MovieTrailerEntry._ID + " TEXT PRIMARY KEY,"
                + MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.MovieTrailerEntry.COLUM_TRAILER_KEY + " TEXT NOT NULL, "
                + MovieContract.MovieTrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, "

                + " FOREIGN KEY (" + MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + ")); ";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
                MovieContract.MovieReviewEntry.TABLE_NAME + " ("
                + MovieContract.MovieReviewEntry._ID + " TEXT PRIMARY KEY,"
                + MovieContract.MovieReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, "
                + MovieContract.MovieReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, "
                + MovieContract.MovieReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "

                + " FOREIGN KEY (" + MovieContract.MovieReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID + ")); ";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieTrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieReviewEntry.TABLE_NAME);
        onCreate(db);
    }

}
