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

import com.stratedgy.dsebuuma.alist.model.Category;
import com.stratedgy.dsebuuma.alist.model.Movie;
import com.stratedgy.dsebuuma.alist.network.Api;
import com.stratedgy.dsebuuma.alist.network.RestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieFragment extends Fragment {
    private static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private GridView movieGridView;
    private GridViewAdapter movieGridViewAdapter;
    SharedPreferences sortingPreference;
    private Movie apiMovie;
    private com.stratedgy.dsebuuma.alist.orm.model.Movie dbMovie;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        sortingPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        movieGridView = (GridView) rootView.findViewById(R.id.movie_grid_view);
        this.getData(getContext());

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utility.isInFavoriteMode(getContext())) {
                    dbMovie =
                            (com.stratedgy.dsebuuma.alist.orm.model.Movie) parent
                                    .getItemAtPosition(position);
                } else {
                    apiMovie = (Movie) parent.getItemAtPosition(position);
                }

                if (getActivity().findViewById(R.id.movie_detail_container) != null) {
                    Bundle arguments = new Bundle();

                    if (Utility.isInFavoriteMode(getContext())) {
                        arguments.putLong("dbId", dbMovie.getId());
                    } else {
                        arguments.putInt("apiId", apiMovie.getId());
                    }

                    DetailFragment fragment = new DetailFragment();
                    fragment.setArguments(arguments);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(getContext(), DetailActivity.class);

                    if (Utility.isInFavoriteMode(getContext())) {
                        intent.putExtra("dbId", dbMovie.getId());
                    } else {
                        intent.putExtra("apiId", apiMovie.getId());
                    }

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
                SharedPreferences.Editor editor = sortingPreference.edit();

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
                    case 2:
                        editor.putString(
                                getContext().getString(R.string.pref_sort_term_key),
                                getContext().getString(R.string.pref_favorite_sort_term)
                        );
                        break;
                }
                editor.apply();
                getData(getContext());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getData(Context context) {
        if (Utility.isInFavoriteMode(context)) {
            List<com.stratedgy.dsebuuma.alist.orm.model.Movie> data =
                    com.stratedgy.dsebuuma.alist.orm.model.Movie.listAll(
                            com.stratedgy.dsebuuma.alist.orm.model.Movie.class
                    );

            movieGridViewAdapter = new GridViewAdapter(
                    getContext(), R.layout.grid_item_movie, (ArrayList) data
            );
            movieGridView.setAdapter(movieGridViewAdapter);
        } else {
            if (Utility.isOnline(getContext())) {
                RestClient restClient = new RestClient();

                Api apiService = restClient.getApiService();
                Call<Category> call = apiService.getMovies(Utility.getPreferredSortTerm(getContext()));
                call.enqueue(new Callback<Category>() {
                    @Override
                    public void onResponse(Call<Category> call, Response<Category> response) {
                        Category category = response.body();
                        ArrayList<Movie> data = (ArrayList<Movie>) category.getMovies();

                        movieGridViewAdapter = new GridViewAdapter(
                                getContext(), R.layout.grid_item_movie, data
                        );
                        movieGridView.setAdapter(movieGridViewAdapter);
                    }

                    @Override
                    public void onFailure(Call<Category> call, Throwable t) {
                        Toast.makeText(
                                getContext(), "Failed to fetch movies " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            } else {
                Toast.makeText(
                        getContext(), "No network connectivity at this time.", Toast.LENGTH_LONG
                ).show();
            }

        }
    }
}
