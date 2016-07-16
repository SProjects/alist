package com.stratedgy.dsebuuma.alist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stratedgy.dsebuuma.alist.model.Movie;
import com.stratedgy.dsebuuma.alist.network.Api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailFragment extends Fragment {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String POSTER_URI = "https://image.tmdb.org/t/p/w185";

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mReleaseView;
    private TextView mLengthView;
    private TextView mRatingView;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mPosterView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
        mTitleView = (TextView) rootView.findViewById(R.id.movie_detail_title);
        mOverviewView = (TextView) rootView.findViewById(R.id.movie_synopsis);
        mReleaseView = (TextView) rootView.findViewById(R.id.detail_release_date);
        mLengthView = (TextView) rootView.findViewById(R.id.detail_movie_length);
        mRatingView = (TextView) rootView.findViewById(R.id.detail_movie_rating);

        Bundle arguments = getArguments();
        if (arguments != null) {
            int id = arguments.getInt("id", 550);
            this.updateView(id);
        }

        return rootView;
    }

    private void updateView(int id) {
        final String BASE_URL = "https://api.themoviedb.org/3/movie/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api apiService = retrofit.create(Api.class);
        Call<Movie> call = apiService.getMovie(id);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                String imageUri = POSTER_URI + movie.getPosterPath();

                mTitleView.setText(movie.getOriginalTitle());
                mOverviewView.setText(movie.getOverview());
                mReleaseView.setText(movie.getReleaseDate().split("-")[0]);
                mLengthView.setText(movie.getRuntime() + " min");
                mRatingView.setText(movie.getVoteAverage() + "/10" );

                Picasso.with(getContext())
                        .load(imageUri)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(mPosterView);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(
                        getContext(), "Failed to fetch movie " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

}
