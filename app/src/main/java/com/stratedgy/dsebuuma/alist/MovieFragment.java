package com.stratedgy.dsebuuma.alist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.stratedgy.dsebuuma.alist.model.Movie;
import com.stratedgy.dsebuuma.alist.model.Movies;
import com.stratedgy.dsebuuma.alist.network.Api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieFragment extends Fragment {
    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
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
        this.getData();
        movieGridView = (GridView) rootView.findViewById(R.id.movie_grid_view);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("id", movie.getId());

                startActivity(intent);
            }
        });


        return rootView;
    }

    private void getData() {
        final String BASE_URL = "https://api.themoviedb.org/3/movie/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api apiService = retrofit.create(Api.class);
        Call<Movies> call = apiService.getMovies();
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                Movies moviesList = response.body();
                ArrayList<Movie> data = (ArrayList<Movie>) moviesList.getMovies();

                movieGridViewAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_movie, data);
                movieGridView.setAdapter(movieGridViewAdapter);
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                Toast.makeText(
                        getContext(), "Failed to fetch movies " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

    }
}
