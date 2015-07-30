package com.jeremy.mycompagny.popularmoviesapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The main activity
 */
public class MainActivity extends Activity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private MoviePosterAdapter moviePosterAdapter;

    private ArrayList<Movie> movieArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "entered onCreate method");

        super.onCreate(savedInstanceState);

        /* Set the activity layout */
        setContentView(R.layout.activity_main);

        /* Get the gridView */
        GridView gridView = (GridView) findViewById(R.id.gridView);

        /* Create the custom adapter */
        moviePosterAdapter = new MoviePosterAdapter(
                this, // The current context (this activity)
                new ArrayList<Movie>());

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Overide onClick action
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Parcelable movie = moviePosterAdapter.getItem(position);
                Intent intent = new Intent(getBaseContext(), MovieDetailActivity.class)
                        .putExtra(getString(R.string.movie_key), movie);
                startActivity(intent);

            }
        });

        gridView.setAdapter(moviePosterAdapter);
        Log.d(LOG_TAG, "gridView.setAdapter(mMoviePosterAdapter) passed");

        if (savedInstanceState != null && savedInstanceState.containsKey(getString(R.string.movie_array_key))) {

            Log.d(LOG_TAG, "savedInstanceState is not null, and it does contain a key called movieArray");

            Parcelable[] movieArray = savedInstanceState.getParcelableArray(getString(R.string.movie_array_key));
            movieArrayList = new ArrayList<Movie>();
            for (Parcelable movie : movieArray) {
                movieArrayList.add((Movie) movie);
            }

            moviePosterAdapter.clear();
            moviePosterAdapter.addAll(movieArrayList);
            Log.d(LOG_TAG, "movieArrayList has been added to mMoviePosterAdapter");
        } else {

            Log.d(LOG_TAG, "savedInstance state is either null or does not contain a \"movieArray\"");
            Log.d(LOG_TAG, "updating movies via API call");

            updateMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        Parcelable[] movieArray = new Parcelable[movieArrayList.size()];

        for (int i = 0; i < movieArrayList.size(); i++) {
            movieArray[i] = (movieArrayList.get(i));
        }
        outState.putParcelableArray(getString(R.string.movieArray), movieArray);

        super.onSaveInstanceState(outState);

        Log.d(LOG_TAG, "instanceState saved");
        Log.d(LOG_TAG, "outState.containsKey(\"movieArray\"): "
                + String.valueOf(outState.containsKey("movieArray")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // AsyncTask for :
    // get a list of movies and their image and detail URLs (doInBackground),
    // download the images (doInBackground),
    // and pass them to the adapterView (onPostExecute)
    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... sortOption) {

            Log.d(LOG_TAG, "Entered in doing in background");

            URL queryURL = getQueryURL(sortOption[0]);
            String moviesJsonStr = getMoviesData(queryURL);
            try {
                Movie[] movies = getMovieArrayFromJsonStr(moviesJsonStr);
                return movies;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Movie[] movies) {

            Log.d(LOG_TAG, "entered onPostExecute");

            if (movies != null) {
                movieArrayList = new ArrayList<Movie>(Arrays.asList(movies));
                moviePosterAdapter.addAll(movieArrayList);
            }
        }

        /**
         * Make the URL for the API
         *
         * @param sortOption
         * @return URL of the API
         */
        private URL getQueryURL(String sortOption) {

            final String LOG_TAG = "getQueryURL";

            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(getString(R.string.http_protocol))
                        .authority(getString(R.string.moviedb_api_url))
                        .appendPath(getString(R.string.number_three))
                        .appendPath(getString(R.string.discover))
                        .appendPath(getString(R.string.movie))
                        .appendQueryParameter(getString(R.string.API_query_sort_by), sortOption)
                        .appendQueryParameter(getString(R.string.API_query_key), getString(R.string.API_param_key));

                Log.d(LOG_TAG, builder.build().toString());

                return new URL(builder.build().toString());

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
        }

        //Make API Call and read input
        //A lot of code here copied from Sunshine app
        private String getMoviesData(URL queryURL) {

            final String LOG_TAG = "getMovies()";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                // Create the request to themovieDB, and open the connection
                urlConnection = (HttpURLConnection) queryURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
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
                moviesJsonStr = buffer.toString();

                //Log.v(LOG_TAG, moviesJsonStr);

                return moviesJsonStr;

            } catch (IOException e) {

                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movies data, there's no point in attempting
                // to parse it.
                return null;
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
        }

        /**
         * Convert JSon to movie Object
         *
         * @param moviesDataStr
         * @return Array of Movie
         * @throws JSONException
         */
        private Movie[] getMovieArrayFromJsonStr(String moviesDataStr)
                throws JSONException {

            final int resultCount = 20; //20 results shown per page: initial setting

            // These are the names of the JSON objects that need to be extracted.
            final String TMDB_MOVIES_LIST = "results";
            final String TMDB_MOVIE_ID = "id";
            final String TMDB_TITLE = "original_title";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_PLOT_SYNOPSIS = "overview";
            final String TMDB_USER_RATING = "vote_average";
            final String TMDB_RELEASE_DATE = "release_date";

            JSONObject moviesJsonResult = new JSONObject(moviesDataStr);
            JSONArray moviesJsonArray = moviesJsonResult.getJSONArray(TMDB_MOVIES_LIST);


            //Create an array of movie objects to store relevant details from the JSON results
            Movie[] moviesObjectArray = new Movie[resultCount];


            //for each movie in the JSON array, create a Movie object and store the relevant details
            for (int i = 0; i < moviesJsonArray.length(); i++) {

                Movie movieObject = new Movie();

                // Get the JSON object representing the movie
                JSONObject movieJson = moviesJsonArray.getJSONObject(i);

                //Set movie details to the movie object
                movieObject.setMovieId(movieJson.getInt(TMDB_MOVIE_ID));
                movieObject.setMovieTitle(movieJson.getString(TMDB_TITLE));
                movieObject.setMoviePosterPath(movieJson.getString(TMDB_POSTER_PATH));
                movieObject.setMovieSynopsis(movieJson.getString(TMDB_PLOT_SYNOPSIS));
                movieObject.setMovieUserRating(movieJson.getDouble(TMDB_USER_RATING));
                movieObject.setMovieReleaseDate(movieJson.getString(TMDB_RELEASE_DATE));

                moviesObjectArray[i] = movieObject;

            }

            return moviesObjectArray;
        }

    }

    /**
     * Update Movie Data with AsyncTask
     */
    public void updateMovies() {

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        // Read shared Preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sortingOrder = prefs.getString(getString(R.string.pref_sorting_order_key),
                getString(R.string.pref_sorting_order_default_value));
        // Execute the task
        moviesTask.execute(sortingOrder);
    }
}
