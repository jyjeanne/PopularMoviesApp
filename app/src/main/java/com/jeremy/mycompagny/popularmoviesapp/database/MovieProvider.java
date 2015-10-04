package com.jeremy.mycompagny.popularmoviesapp.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Movie db provider to make CRUD actions.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_REVIEW_AND_TRAILER = 101;
    static final int TRAILER = 200;
    static final int REVIEW = 300;

    private static final SQLiteQueryBuilder sMovieInnerJoinTrailerAndReview;

    static {
        sMovieInnerJoinTrailerAndReview = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        // movie INNER JOIN review ON movie._id = review.movie_id
        // INNER JOIN trailer ON movie._id = trailer.movie_id
        sMovieInnerJoinTrailerAndReview.setTables(
                MovieContract.MovieEntry.TABLE_NAME
                        + " INNER JOIN "
                        + MovieContract.MovieReviewEntry.TABLE_NAME + " ON "
                        + MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = "
                        + MovieContract.MovieReviewEntry.TABLE_NAME + "." + MovieContract.MovieReviewEntry.COLUMN_MOVIE_ID
                        + " INNER JOIN "
                        + MovieContract.MovieTrailerEntry.TABLE_NAME + " ON "
                        + MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = "
                        + MovieContract.MovieTrailerEntry.TABLE_NAME + "." + MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID
        );

    }

    //movie._id = ?
    private static final String sMovieByIdSelection =
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = ? ";

    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority,
                MovieContract.PATH_MOVIE + "/*",
                MOVIE_WITH_REVIEW_AND_TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);

        return matcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            break;
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieTrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            break;
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            break;
            case MOVIE_WITH_REVIEW_AND_TRAILER:{

            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case TRAILER:
                return MovieContract.MovieTrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.MovieReviewEntry.CONTENT_TYPE;
            case MOVIE_WITH_REVIEW_AND_TRAILER:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match){
            case MOVIE:
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case TRAILER:
                _id = db.insert(MovieContract.MovieTrailerEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.MovieTrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case REVIEW:
                _id = db.insert(MovieContract.MovieReviewEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.MovieReviewEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(
                        MovieContract.MovieTrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(
                        MovieContract.MovieReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(MovieContract.MovieTrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(MovieContract.MovieReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
