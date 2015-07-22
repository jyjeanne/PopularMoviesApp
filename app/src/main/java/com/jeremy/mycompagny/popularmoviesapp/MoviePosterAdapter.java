package com.jeremy.mycompagny.popularmoviesapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A custom adapter for Movie Object.
 */
public class MoviePosterAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();

    /**
     * Constructor
     *
     * @param context
     * @param movies
     */
    public MoviePosterAdapter(Activity context, List<Movie> movies) {

        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the Movie object from the ArrayAdapter
        Movie movie = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        ImageView moviePosterView = (ImageView) convertView.findViewById(R.id.grid_item_movie_image);

        String posterURLStr = movie.getPosterURL();

        Picasso.with(getContext()).load(posterURLStr).into(moviePosterView);

        return convertView;
    }
}
