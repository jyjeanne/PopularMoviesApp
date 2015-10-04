package com.jeremy.mycompagny.popularmoviesapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jeremy.mycompagny.popularmoviesapp.R;
import com.jeremy.mycompagny.popularmoviesapp.model.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A custom adapter for Movie Object.
 */
public class MoviePosterAdapter extends ArrayAdapter<MovieItem> {

    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();
    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<MovieItem> mMoviesInfo;

    /**
     * Custom constructor
     *
     * @param context
     * @param movies
     */
    public MoviePosterAdapter(Context context,int layoutResourceId, ArrayList movies) {

        super(context, layoutResourceId, movies);

        this.mContext = context;
        this.mMoviesInfo = movies;
        this.mLayoutResourceId = layoutResourceId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the Movie object from the ArrayAdapter
        MovieItem movie = getItem(position);

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
