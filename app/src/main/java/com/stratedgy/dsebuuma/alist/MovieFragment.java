package com.stratedgy.dsebuuma.alist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
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
    SharedPreferences sortingPreference;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        sortingPreference = getActivity().getSharedPreferences(
                getActivity().getPackageName(),
                Context.MODE_PRIVATE
        );
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

                if (getActivity().findViewById(R.id.movie_detail_container) != null) {
                    Bundle arguments = new Bundle();

                    arguments.putInt("id", movie.getId());

                    DetailFragment fragment = new DetailFragment();
                    fragment.setArguments(arguments);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("id", movie.getId());

                    startActivity(intent);
                }
            }
        });


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.sort_menu_spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sort_menu_items,
                android.R.layout.simple_spinner_dropdown_item
        );
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(getContext())
                        .edit();

                switch (position) {
                    case 0:
                        editor.putString(
                                getContext().getString(R.string.pref_sort_term_key),
                                getContext().getString(R.string.pref_default_sort_term)
                        );
                        break;
                    case 1:
                        editor.putString(
                                getContext().getString(R.string.pref_sort_term_key),
                                getContext().getString(R.string.pref_top_rated_sort_term)
                        );
                        break;
                }
                editor.apply();
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getData() {
        final String BASE_URL = "https://api.themoviedb.org/3/movie/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api apiService = retrofit.create(Api.class);
        Call<Movies> call = apiService.getMovies(Utility.getPreferredSortTerm(getContext()));
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
