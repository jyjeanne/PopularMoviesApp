package com.jeremy.mycompagny.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jeremy.mycompagny.popularmoviesapp.fragments.MovieDetailFragment;
import com.jeremy.mycompagny.popularmoviesapp.fragments.MoviesFragment;
import com.jeremy.mycompagny.popularmoviesapp.model.MovieItem;

/**
 * The main activity
 */
public class MainActivity extends AppCompatActivity implements Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private String sortingOrder;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "entered onCreate method");

        super.onCreate(savedInstanceState);

        /* Get sort preferences */
        sortingOrder = Utility.getPreferredSortingOrder(this);

        MoviesFragment moviesFragment = null;

        /* Set the activity layout */
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), MovieDetailFragment.TAG)
                        .commit();

                moviesFragment =
                        ((MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movie));

                this.onItemSelected(moviesFragment.get_selectedMovie());
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        moviesFragment =
                ((MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movie));

        moviesFragment.setUseTwoPanesLayout(mTwoPane);


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

    @Override
    protected void onResume() {
        super.onResume();

        String sortMethod = Utility.getPreferredSortingOrder(this);

        if(sortMethod != null && !sortMethod.equals(sortingOrder)){
            MoviesFragment mf = (MoviesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if(null != mf){

                if(mf.get_selectedMovie() != null){
                    onItemSelected(mf.get_selectedMovie());
                }

            }

            sortingOrder = sortMethod;
        }
    }

    @Override
    public void onItemSelected(MovieItem movieItem) {

        if(mTwoPane){

            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.SELECTED_MOVIE_KEY, movieItem);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MovieDetailFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .putExtra(MovieDetailFragment.SELECTED_MOVIE_KEY, movieItem);
            startActivity(intent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//        if (movieArrayList != null) {
//            Parcelable[] movieArray = new Parcelable[movieArrayList.size()];
//
//            for (int i = 0; i < movieArrayList.size(); i++) {
//                movieArray[i] = (movieArrayList.get(i));
//            }
//            outState.putParcelableArray(getString(R.string.movieArray), movieArray);
//
//            super.onSaveInstanceState(outState);
//
//            Log.d(LOG_TAG, "instanceState saved");
//            Log.d(LOG_TAG, "outState.containsKey(\"movieArray\"): "
//                    + String.valueOf(outState.containsKey("movieArray")));
//        }
//    }

}
