package com.jeremy.mycompagny.popularmoviesapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * this class extends {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "entered onCreateView method");

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")){
            Movie movie = (intent.getParcelableExtra("movie"));

            ((TextView)(rootView.findViewById(R.id.movie_title))).setText(movie.getMovieTitle());

            ImageView imageView = (ImageView)(rootView.findViewById(R.id.movie_thumbnail));
            String posterURLStr = movie.getPosterURL();
            Picasso.with(getActivity()).load(posterURLStr).into(imageView);

            ((TextView)(rootView.findViewById(R.id.movie_release_date))).setText(movie.getMovieReleaseDate());

            ((TextView)(rootView.findViewById(R.id.movie_rating)))
                    .setText
                            (String.valueOf(movie.getMovieUserRating()) + "/10");

            TextView synopsis = (TextView)(rootView.findViewById(R.id.movie_synopsis));
            synopsis.setText(movie.getMovieSynopsis());
            synopsis.setMovementMethod(new ScrollingMovementMethod());


        }

        return rootView;

    }

}
