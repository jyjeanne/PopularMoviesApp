package com.jeremy.mycompagny.popularmoviesapp;

import com.jeremy.mycompagny.popularmoviesapp.model.MovieItem;

/**
 * Created by Jeremy on 02/10/2015.
 */
public interface Callback {
    public void onItemSelected(MovieItem movieItem);
}
