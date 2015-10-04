package com.jeremy.mycompagny.popularmoviesapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.jeremy.mycompagny.popularmoviesapp.Callback;
import com.jeremy.mycompagny.popularmoviesapp.R;
import com.jeremy.mycompagny.popularmoviesapp.Utility;
import com.jeremy.mycompagny.popularmoviesapp.adapters.MoviePosterAdapter;
import com.jeremy.mycompagny.popularmoviesapp.asynctasks.FetchMoviesTask;
import com.jeremy.mycompagny.popularmoviesapp.model.MovieItem;

import java.util.ArrayList;

/**
 * Main movies fragment.
 */
public class MoviesFragment extends Fragment {

    private boolean mUseTwoPanesLayout;
    private GridView mGridViewMovies;
    private String mSortMethod;

    private MoviePosterAdapter mMovieArrayAdapter;

    private ArrayList<MovieItem> _movies;

    private MovieItem _selectedMovie;

    public MovieItem get_selectedMovie() {
        return _selectedMovie;
    }

    private int mPosition = GridView.INVALID_POSITION;
    private final String MOVIE_LIST_KEY = "movie_list_key";
    private final String SELECTED_KEY="selected_position";

    public MoviesFragment(){ }



    public void setUseTwoPanesLayout(boolean useTwoPanesLayout){
        mUseTwoPanesLayout = useTwoPanesLayout;
        if(mUseTwoPanesLayout){

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSortMethod = Utility.getPreferredSortingOrder(getActivity());

        if(savedInstanceState == null)
        {
            updateMoviesList();

            if(_movies != null && _movies.size() >0){
                _selectedMovie = _movies.get(0);
            }

        } else {
            if(savedInstanceState.containsKey(MOVIE_LIST_KEY)){
                _movies = (ArrayList<MovieItem>) savedInstanceState.getSerializable(MOVIE_LIST_KEY);
            }
            if(savedInstanceState.containsKey(SELECTED_KEY)){

                mPosition = savedInstanceState.getInt(SELECTED_KEY);

                if(_movies != null && _movies.size() >0){
                    _selectedMovie = _movies.get(mPosition);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridViewMovies = (GridView) rootView.findViewById(R.id.grid_movie_images);

        if(_movies == null){
            _movies = new ArrayList<>();
        }

        mMovieArrayAdapter = new MoviePosterAdapter(getActivity(), R.layout.list_item_movie_image, _movies);

        mGridViewMovies.setAdapter(mMovieArrayAdapter);

        mGridViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                MovieItem movieInfo = (MovieItem) mMovieArrayAdapter.getItem(position);

                ((Callback) getActivity()).onItemSelected(movieInfo);

            }
        });

        mGridViewMovies.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mPosition = firstVisibleItem;
            }
        });

        return  rootView;
    }

    public void updateMoviesList() {

        FetchMoviesTask movieTask = new FetchMoviesTask(new FragmentCallback(){

            @Override
            public void onTaskDone(ArrayList<MovieItem> result) {
                taskIsDone(result);
            }
        }, getActivity());


        String sortMethod =  Utility.getPreferredSortingOrder(getActivity());

        movieTask.execute(sortMethod);

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(MOVIE_LIST_KEY, _movies);
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        String sortMethod = Utility.getPreferredSortingOrder(getActivity());

        if((sortMethod != null && !sortMethod.equals(mSortMethod))){

            updateMoviesList();

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(SELECTED_KEY)){
                mPosition = savedInstanceState.getInt(SELECTED_KEY, 0);
            }
            if(savedInstanceState.containsKey(MOVIE_LIST_KEY)){
                _movies = (ArrayList<MovieItem>)savedInstanceState.getSerializable(MOVIE_LIST_KEY);
            }
        }
    }

    private void taskIsDone(ArrayList<MovieItem> result){

        mSortMethod =  Utility.getPreferredSortingOrder(getActivity());

        _movies = result;

        if(_movies.size()==0)
        {
            if(mSortMethod.equals(getActivity().getString(R.string.pref_sort_by_value_favorites))){
                Toast.makeText(getActivity(), R.string.info_no_favorite_movies,
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(), R.string.info_no_connection_available,
                        Toast.LENGTH_LONG).show();
            }
        } else {

            mMovieArrayAdapter.clear();
            mMovieArrayAdapter.addAll(_movies);
            mMovieArrayAdapter.notifyDataSetChanged();
        }

    }

    public interface FragmentCallback {
        public void onTaskDone(ArrayList<MovieItem> result);
    }
}
