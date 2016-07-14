package com.stratedgy.dsebuuma.alist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;


public class MovieFragment extends Fragment {
    private GridView movieGridView;
    private GridViewAdapter movieGridViewAdapter;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        movieGridView = (GridView) rootView.findViewById(R.id.movie_grid_view);
        movieGridViewAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_movie, getData());
        movieGridView.setAdapter(movieGridViewAdapter);

        return rootView;
    }

    private ArrayList<GridItem> getData() {
        Integer[] movies = {
                R.drawable.inter, R.drawable.inter, R.drawable.inter,
                R.drawable.inter, R.drawable.inter, R.drawable.inter,
                R.drawable.inter, R.drawable.inter, R.drawable.inter,
                R.drawable.inter, R.drawable.inter, R.drawable.inter,
                R.drawable.inter, R.drawable.inter, R.drawable.inter
        };

        final ArrayList<GridItem> movieItems = new ArrayList<>();

        for (int i = 0; i < movies.length; i++) {
            movieItems.add(new GridItem(movies[i], "Interstaller #" + i));
        }

        return movieItems;
    }
}
