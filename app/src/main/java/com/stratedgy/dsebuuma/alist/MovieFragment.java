package com.stratedgy.dsebuuma.alist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("poster", item.getMoviePosterResource());
                intent.putExtra("title", item.getMovieTitle());

                startActivity(intent);
            }
        });

        return rootView;
    }

    private ArrayList<GridItem> getData() {
        Integer[] movies = {
                R.drawable.inter, R.drawable.bat, R.drawable.whip,
                R.drawable.inter, R.drawable.bat, R.drawable.inter,
                R.drawable.whip, R.drawable.inter, R.drawable.bat,
                R.drawable.inter, R.drawable.whip, R.drawable.inter
        };

        final ArrayList<GridItem> movieItems = new ArrayList<>();

        for (int i = 0; i < movies.length; i++) {
            movieItems.add(new GridItem(movies[i], "Interstaller #" + i));
        }

        return movieItems;
    }
}
