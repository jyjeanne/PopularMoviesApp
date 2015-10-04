package com.jeremy.mycompagny.popularmoviesapp.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.jeremy.mycompagny.popularmoviesapp.CheckConnectivity;
import com.jeremy.mycompagny.popularmoviesapp.R;
import com.jeremy.mycompagny.popularmoviesapp.database.MovieContract;
import com.jeremy.mycompagny.popularmoviesapp.fragments.MoviesFragment;
import com.jeremy.mycompagny.popularmoviesapp.model.MovieItem;
import com.jeremy.mycompagny.popularmoviesapp.model.MovieReview;
import com.jeremy.mycompagny.popularmoviesapp.model.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * An asyncTask to fetch movie datas.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_BACKDROP_PATH = 2;
    public static final int COL_ORIGINAL_TITLE = 3;
    public static final int COL_OVERVIEW =4;
    public static final int COL_POPULARITY = 5;
    public static final int COL_POSTER_PATH = 6;
    public static final int COL_RELEASE_DATE = 7;
    public static final int COL_VOTE_AVG = 8;
    public static final int COL_VOTE_COUNT = 9;

    private static final int COL_REVIEW_ID = 0;
    private static final int COL_REVIEW_AUTHOR = 1;
    private static final int COL_REVIEW_CONTENT = 2;
    private static final int COL_REVIEW_MOVIE_ID = 3;

    private static final int COL_TRAILER_ID = 0;
    private static final int COL_TRAILER_MOVIE_ID = 1;
    private static final int COL_TRAILER_KEY = 2;
    private static final int COL_TRAILER_NAME = 3;

    private static final String JSON_RESULTS = "results";

    private static final String QUERY_API_KEY="api_key";
    private static final String PARAM_API_KEY ="b1cdfe22af5862dd6a7754eff0f0f776";

    Context mContext;
    private MoviesFragment.FragmentCallback mFragmentCallback;

    private ProgressDialog dialog;

    public FetchMoviesTask(MoviesFragment.FragmentCallback fragmentCallback, FragmentActivity context) {
        mContext = context;
        mFragmentCallback = fragmentCallback;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected ArrayList<MovieItem> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        try{

            String retrieveMethod = params[0];

            ArrayList<MovieItem> result = new ArrayList<MovieItem>();

            if(!retrieveMethod.equals(mContext.getString(R.string.pref_sort_by_value_favorites))){

                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";

                String QUERY_SORT_BY = "sort_by";


                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_SORT_BY, params[0])
                        .appendQueryParameter(QUERY_API_KEY, PARAM_API_KEY).build();

                String jsonResult = getJSONData(builtUri);

                result = getMovieDataFromJson(jsonResult);

            }
            else {

                Cursor cursor = mContext.getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,   // projection
                        null,   //where
                        null,   // Values for the "where" clause
                        null    // sort order
                );

                MovieItem[] movieItems = new MovieItem[cursor.getCount()];

                int i = 0;

                while (cursor.moveToNext()) {
                    movieItems[i] = new MovieItem(
                            cursor.getString(COL_MOVIE_ID),
                            cursor.getString(COL_ORIGINAL_TITLE),
                            cursor.getString(COL_POSTER_PATH),
                            cursor.getString(COL_OVERVIEW),
                            cursor.getString(COL_VOTE_AVG),
                            cursor.getString(COL_RELEASE_DATE),
                            cursor.getString(COL_POPULARITY),
                            cursor.getString(COL_VOTE_COUNT),
                            cursor.getString(COL_BACKDROP_PATH),
                            cursor.getString(COL_TITLE),
                            true
                    );

                    ArrayList<MovieReview> reviews = getMovieReviewsDataFromDb(cursor.getString(COL_MOVIE_ID));
                    ArrayList<MovieTrailer> trailers = getMovieTrailersDataFromDb(cursor.getString(COL_MOVIE_ID));

                    movieItems[i].setMovieReviews(reviews);
                    movieItems[i].setMovieTrailers(trailers);

                    i++;
                }

                cursor.close();

                result = new ArrayList<>(Arrays.asList(movieItems));
            }

            return result;

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return new ArrayList<MovieItem>();
        }
    }

    /** progress dialog to show user that the backup is processing. */
    /** application context. */
    @Override
    protected void onPreExecute() {

        this.dialog.setMessage("Please wait ...");
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<MovieItem> result){
        mFragmentCallback.onTaskDone(result);
        this.dialog.hide();
    }

    private ArrayList<MovieTrailer> getMovieTrailersDataFromDb(String movieId) {

        ArrayList<MovieTrailer> movieTrailers;

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieTrailerEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID + " = " + movieId,   //where
                null,   // Values for the "where" clause
                null    // sort order
        );

        MovieTrailer[] items = new MovieTrailer[cursor.getCount()];

        int i = 0;

        while (cursor.moveToNext()) {

            items[i] = new MovieTrailer(cursor.getString(COL_TRAILER_MOVIE_ID), cursor.getString(COL_TRAILER_ID),cursor.getString(COL_TRAILER_NAME), cursor.getString(COL_TRAILER_KEY));

            i++;
        }

        movieTrailers = new ArrayList<>(Arrays.asList(items));

        return movieTrailers;
    }

    private ArrayList<MovieReview> getMovieReviewsDataFromDb(String movieId) {

        ArrayList<MovieReview> movieReviews;

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieReviewEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieReviewEntry.COLUMN_MOVIE_ID + " = " + movieId,   //where
                null,   // Values for the "where" clause
                null    // sort order
        );

        MovieReview[] items = new MovieReview[cursor.getCount()];

        int i = 0;

        while (cursor.moveToNext()) {

            items[i] = new MovieReview(cursor.getString(COL_REVIEW_AUTHOR), cursor.getString(COL_REVIEW_ID), cursor.getString(COL_REVIEW_CONTENT), cursor.getString(COL_REVIEW_MOVIE_ID) );

            i++;
        }

        movieReviews = new ArrayList<>(Arrays.asList(items));

        return movieReviews;
    }

    private String getJSONData(Uri builtUri)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String result = null;

        try{

            URL url = new URL(builtUri.toString());

            CheckConnectivity checkConnectivity = new CheckConnectivity();

            if(!checkConnectivity.isNetworkConnected(mContext) || checkConnectivity.isInternetAvailable(builtUri.toString())) {
                return null;
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            result = buffer.toString();

            return result;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SecurityException e){
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    private ArrayList<MovieItem> getMovieDataFromJson(String moviesJsonStr) throws JSONException {

        try {

            if(moviesJsonStr == null)
                return new ArrayList<>();

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = moviesJson.getJSONArray(JSON_RESULTS);

            if(resultsArray == null)
                return new ArrayList<>();

            int count = resultsArray.length();

            MovieItem[] movieItems = new MovieItem[count];


            for(int i = 0; i <= count - 1; i++) {

                JSONObject jsonObjectMovie = resultsArray.getJSONObject(i);

                String id = jsonObjectMovie.getString(MovieItem.ID);
                String poster_path = jsonObjectMovie.getString(MovieItem.POSTER_PATH);
                String originalTitle = jsonObjectMovie.getString(MovieItem.ORIGINAL_TITLE);
                String overview = jsonObjectMovie.getString(MovieItem.OVERVIEW);
                String vote_average = jsonObjectMovie.getString(MovieItem.VOTE_AVERAGE)+"/10";
                String release_date = jsonObjectMovie.getString(MovieItem.RELEASE_DATE);
                String popularity = jsonObjectMovie.getString(MovieItem.POPULARITY);
                String voteCount = jsonObjectMovie.getString(MovieItem.VOTE_COUNT);
                String backdropPath = jsonObjectMovie.getString(MovieItem.BACKDROP_PATH);
                String title = jsonObjectMovie.getString(MovieItem.TITLE);

                movieItems[i] = new MovieItem(id, originalTitle, poster_path,overview, vote_average, release_date, popularity, voteCount, backdropPath, title, false);

                movieItems[i].setMovieReviews(getMovieReviews(id));
                movieItems[i].setMovieTrailers(getMovieTrailers(id));

            }

            return new ArrayList<>(Arrays.asList(movieItems));

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private ArrayList<MovieTrailer> getMovieTrailers(String movieId) throws JSONException {

        ArrayList<MovieTrailer> result = new ArrayList<>();

        final String MOVIES_BASE_URL =
                "https://api.themoviedb.org/3/movie/";

        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon().appendPath(movieId).appendPath("videos")
                .appendQueryParameter(QUERY_API_KEY, PARAM_API_KEY).build();

        String jsonResult = getJSONData(builtUri);

        try {
            result = getMovieTrailersDataFromJson(jsonResult, movieId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

    private ArrayList<MovieTrailer> getMovieTrailersDataFromJson(String jsonResult, String movieId) throws JSONException {

        if(jsonResult == null)
            return new ArrayList<>();

        JSONObject moviesJson = new JSONObject(jsonResult);
        JSONArray resultsArray = moviesJson.getJSONArray(JSON_RESULTS);

        if(resultsArray == null)
            return new ArrayList<>();

        int count = resultsArray.length();

        if(count == 0)
            return new ArrayList<>();

        MovieTrailer[] movieTrailers = new MovieTrailer[count];

        for(int i = 0; i <= count - 1; i++) {

            JSONObject jsonObjectMovie = resultsArray.getJSONObject(i);

            String id = jsonObjectMovie.getString(MovieTrailer.ID);
            String name = jsonObjectMovie.getString(MovieTrailer.NAME);
            String key = jsonObjectMovie.getString(MovieTrailer.KEY);

            movieTrailers[i] = new MovieTrailer(movieId, id, name,key);

        }

        return new ArrayList<>(Arrays.asList(movieTrailers));
    }

    private ArrayList<MovieReview> getMovieReviews(String movieId) throws JSONException {

        ArrayList<MovieReview> result = new ArrayList<>();

        final String MOVIES_BASE_URL =
                "https://api.themoviedb.org/3/movie/";

        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon().appendPath(movieId).appendPath("reviews")
                .appendQueryParameter(QUERY_API_KEY, PARAM_API_KEY).build();

        String jsonResult = getJSONData(builtUri);

        try {
            result = getMovieReviewsDataFromJson(jsonResult, movieId);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

    private ArrayList<MovieReview> getMovieReviewsDataFromJson(String jsonResult, String movieId) throws JSONException {

        JSONObject moviesJson = new JSONObject(jsonResult);
        JSONArray resultsArray = moviesJson.getJSONArray(JSON_RESULTS);

        if(resultsArray == null)
            return new ArrayList<>();

        int count = resultsArray.length();

        if(count == 0)
            return new ArrayList<>();

        MovieReview[] movieReviews = new MovieReview[count];

        for(int i = 0; i <= count - 1; i++) {

            JSONObject jsonObjectMovie = resultsArray.getJSONObject(i);

            String id = jsonObjectMovie.getString(MovieReview.ID);
            String author = jsonObjectMovie.getString(MovieReview.AUTHOR);
            String content = jsonObjectMovie.getString(MovieReview.CONTENT);

            movieReviews[i] = new MovieReview(author, id, content,movieId);

        }

        return new ArrayList<>(Arrays.asList(movieReviews));

    }
}
