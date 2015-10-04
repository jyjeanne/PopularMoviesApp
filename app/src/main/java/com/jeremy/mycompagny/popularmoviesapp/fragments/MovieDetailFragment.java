package com.jeremy.mycompagny.popularmoviesapp.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeremy.mycompagny.popularmoviesapp.R;
import com.jeremy.mycompagny.popularmoviesapp.database.MovieContract;
import com.jeremy.mycompagny.popularmoviesapp.model.MovieItem;
import com.jeremy.mycompagny.popularmoviesapp.model.MovieReview;
import com.jeremy.mycompagny.popularmoviesapp.model.MovieTrailer;
import com.squareup.picasso.Picasso;


/**
 * this class extends {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {

    public static final String TAG = MovieDetailFragment.class.getSimpleName();
    public static final String SELECTED_MOVIE_KEY = "selected_movie";

    private Button btnMarkAsFavorite;
    private MovieItem mSelectedMovie;
    private MovieTrailer mMovieTrailer;
    private ShareActionProvider mShareActionProvider;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "entered onCreateView method");

        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Bundle args = getArguments();

        MovieItem movieItem;

        if(args != null){
            movieItem = args.getParcelable(SELECTED_MOVIE_KEY);
        }
        else {
            Intent intent = getActivity().getIntent();
            movieItem = intent.getParcelableExtra(SELECTED_MOVIE_KEY);
        }

        mSelectedMovie = movieItem;

        if(mSelectedMovie != null){

            bindControls(rootView);

            showTrailers(rootView);

            showReviews(rootView);

            btnMarkAsFavorite.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (isAlreadyFavorite() || mSelectedMovie.isFavorite()) {
                        deleteFavoriteMovie();
                        btnMarkAsFavorite.setText(R.string.btn_favorite);
                    } else {
                        insertNewFavoriteMovie();
                        btnMarkAsFavorite.setText(R.string.btn_delete_favorite);
                    }
                }

            });
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        mShareActionProvider.setShareIntent(createShareMovieTrailerIntent());
    }

    private Intent createShareMovieTrailerIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");

        if(mSelectedMovie != null && mSelectedMovie.getMovieTrailers() != null){

            if(mSelectedMovie.getMovieTrailers().size() > 0) {
                MovieTrailer trailer = mSelectedMovie.getMovieTrailers().get(0);

                Uri builtUri = getMovieTrailerUri(trailer);

                shareIntent.putExtra(Intent.EXTRA_TEXT, builtUri.toString());
            } else {
                shareIntent.putExtra(Intent.EXTRA_TEXT, mSelectedMovie.getMovieTitle() + "(" + mSelectedMovie.getMovieOriginalTitle() + ")");
            }

        }

        return shareIntent;

    }

    private void bindControls(View rootView) {
        String movieTitle = mSelectedMovie.getMovieOriginalTitle();
        String moviePoster = mSelectedMovie.getMoviePosterPath();
        String releaseDate = mSelectedMovie.getMovieReleaseDate();
        String vote_average = mSelectedMovie.getMovieUserRating();
        String overview = mSelectedMovie.getMovieOverview();

        ((TextView) rootView.findViewById(R.id.movie_title)).setText(movieTitle);
        ImageView imageView = ((ImageView) rootView.findViewById(R.id.movie_poster));
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w92//" + moviePoster).into(imageView);
        ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(releaseDate);
        ((TextView) rootView.findViewById(R.id.movie_rating)).setText(vote_average);
        ((TextView) rootView.findViewById(R.id.movie_overview)).setText(overview);

        btnMarkAsFavorite = (Button) rootView.findViewById(R.id.favorites_btn);

        if(mSelectedMovie.isFavorite() || isAlreadyFavorite()){
            btnMarkAsFavorite.setText(R.string.btn_delete_favorite);
        } else {
            btnMarkAsFavorite.setText(R.string.btn_favorite);
        }
    }

    private void showTrailers(View rootView) {
        int i = 0;

        for (MovieTrailer trailer : mSelectedMovie.getMovieTrailers()) {

            LinearLayout list_of_trailers = ((LinearLayout) rootView.findViewById(R.id.list_of_trailers));

            list_of_trailers.setGravity(Gravity.CENTER);

            Button btnTrailer = new Button(getActivity());

            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            btnTrailer.setLayoutParams(parms);

            btnTrailer.setText(trailer.getName());

            btnTrailer.setId(i);

            mMovieTrailer = trailer;

            btnTrailer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Uri builtUri = getMovieTrailerUri(mMovieTrailer);

                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, builtUri));
                }
            });

            list_of_trailers.addView(btnTrailer);

            i++;

        }
    }

    private Uri getMovieTrailerUri(MovieTrailer movieTrailer) {
        final String YOUTUBE_BASE_URL =
                "https://www.youtube.com/";

        final String WATCH = "watch";

        return Uri.parse(YOUTUBE_BASE_URL).buildUpon().appendPath(WATCH)
                .appendQueryParameter("v", movieTrailer.getKey()).build();
    }

    private void showReviews(View rootView) {

        int i;
        i = 0;

        for (MovieReview review : mSelectedMovie.getMovieReviews()) {

            LinearLayout list_of_reviews = ((LinearLayout) rootView.findViewById(R.id.list_of_reviews));

            TextView textView = new TextView(getActivity());

            textView.setId(i);

            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            textView.setText("\"" + review.getContent() + "\"" + "\n -" + review.getAuthor() + "\n\n" );

            list_of_reviews.addView(textView);

            i++;

        }
    }

    private void deleteFavoriteMovie() {

        getActivity().getContentResolver().delete(
                MovieContract.MovieTrailerEntry.CONTENT_URI,
                MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID + " = " + mSelectedMovie.getId(),
                null
        );

        getActivity().getContentResolver().delete(
                MovieContract.MovieReviewEntry.CONTENT_URI,
                MovieContract.MovieReviewEntry.COLUMN_MOVIE_ID + " = " + mSelectedMovie.getId(),
                null
        );

        getActivity().getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry._ID + " = " + mSelectedMovie.getId(),
                null
        );

    }

    private boolean isAlreadyFavorite() {
        // A cursor is your primary interface to the query results.
        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry._ID + " = " + mSelectedMovie.getId(),
                null,   // Values for the "where" clause
                null    // sort order
        );

        if(cursor.getCount() == 0){
            return false;
        }

        return true;
    }

    private void insertNewFavoriteMovie(){

        ContentValues movieContentValues = getMovieContentValues();

        Uri insertedUri = getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieContentValues);

        getActivity().getContentResolver().notifyChange(insertedUri, null);

        for(MovieReview movieReview : mSelectedMovie.getMovieReviews()){

            ContentValues reviewsContentValues = getMovieReviewsContentValues(movieReview);

            insertedUri = getActivity().getContentResolver().insert(MovieContract.MovieReviewEntry.CONTENT_URI, reviewsContentValues);

            getActivity().getContentResolver().notifyChange(insertedUri, null);
        }

        for(MovieTrailer movieTrailer : mSelectedMovie.getMovieTrailers()){

            ContentValues trailersContentValues = getMovieTrailersContentValues(movieTrailer);

            insertedUri = getActivity().getContentResolver().insert(MovieContract.MovieTrailerEntry.CONTENT_URI, trailersContentValues);

            getActivity().getContentResolver().notifyChange(insertedUri, null);
        }

    }

    private ContentValues getMovieTrailersContentValues(MovieTrailer movieTrailer) {
        ContentValues data = new ContentValues();

        data.put(MovieContract.MovieTrailerEntry._ID, movieTrailer.getId());
        data.put(MovieContract.MovieTrailerEntry.COLUM_TRAILER_KEY, movieTrailer.getKey());
        data.put(MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID, mSelectedMovie.getId());
        data.put(MovieContract.MovieTrailerEntry.COLUMN_TRAILER_NAME, movieTrailer.getName());

        return data;
    }

    private ContentValues getMovieContentValues() {
        ContentValues data = new ContentValues();

        data.put(MovieContract.MovieEntry._ID,mSelectedMovie.getId());
        data.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,mSelectedMovie.getMoviePosterPath());
        data.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,mSelectedMovie.getMovieTitle());
        data.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY,mSelectedMovie.getMoviePopularity());
        data.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_COUNT, mSelectedMovie.getMovieVoteCount());
        data.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, mSelectedMovie.getBackdropPath());
        data.put(MovieContract.MovieEntry.COLUMN_MOVIE_ORIGINAL_TITLE, mSelectedMovie.getMovieOriginalTitle());
        data.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, mSelectedMovie.getMovieOverview());
        data.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELASE_DATE, mSelectedMovie.getMovieReleaseDate());
        data.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, mSelectedMovie.getMovieUserRating());

        return data;
    }

    private ContentValues getMovieReviewsContentValues(MovieReview movieReview) {
        ContentValues data = new ContentValues();

        data.put(MovieContract.MovieReviewEntry._ID, movieReview.getId());
        data.put(MovieContract.MovieReviewEntry.COLUMN_MOVIE_ID, mSelectedMovie.getId());
        data.put(MovieContract.MovieReviewEntry.COLUMN_REVIEW_CONTENT, movieReview.getContent());
        data.put(MovieContract.MovieReviewEntry.COLUMN_REVIEW_AUTHOR, movieReview.getAuthor());

        return data;
    }
}
