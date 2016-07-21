package com.stratedgy.dsebuuma.alist;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.stratedgy.dsebuuma.alist.model.Movie;
import com.stratedgy.dsebuuma.alist.model.Youtube;
import com.stratedgy.dsebuuma.alist.network.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailFragment extends Fragment {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String POSTER_URI = "https://image.tmdb.org/t/p/w185";

    ArrayAdapter<Youtube> mTrailerViewAdapter;

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mOverviewView;
    private TextView mReleaseView;
    private TextView mLengthView;
    private TextView mRatingView;
    private ListView mTrailerView;
    private Button mFavoriteButton;

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
        mTrailerView = (ListView) rootView.findViewById(R.id.trailer_list_view);
        mFavoriteButton = (Button) rootView.findViewById(R.id.favorite_movie);

        Bundle arguments = getArguments();
        if (arguments != null) {
            if (Utility.isInFavoriteMode(getContext())) {
                Long id = arguments.getLong("dbId", 1);
                this.updateViewFromDb(id);
            } else {
                int id = arguments.getInt("apiId", 550);
                this.updateViewFromApi(id);
            }
        }

        return rootView;
    }

    private void updateViewFromApi(int id) {
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
                final Movie movie = response.body();
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

                ArrayList<Youtube> movieTrailers = (ArrayList<Youtube>) movie.getTrailers().getYoutube();
                mTrailerViewAdapter = new TrailerAdapter(
                        getContext(),
                        R.layout.trailer_movie_item,
                        movieTrailers
                );
                mTrailerView.setAdapter(mTrailerViewAdapter);

                mTrailerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Youtube trailer = (Youtube) parent.getItemAtPosition(position);

                        String youtubeUri = "http://www.youtube.com/watch?v=";
                        String trailerId = trailer.getSource();

                        try {
                            Intent intent = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("vnd.youtube:" + trailerId)
                            );
                            startActivity(intent);
                        } catch (ActivityNotFoundException ex) {
                            Intent intent = new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(youtubeUri + trailerId)
                            );
                            startActivity(intent);
                        }
                    }
                });


                mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        com.stratedgy.dsebuuma.alist.orm.model.Movie favoriteMovie =
                                com.stratedgy.dsebuuma.alist.orm.model.Movie
                                .generateFromApiMovie(movie);
                        Long favoriteMovieId = favoriteMovie.save();

                        com.stratedgy.dsebuuma.alist.orm.model.Movie savedMovie =
                                com.stratedgy.dsebuuma.alist.orm.model.Movie
                                        .findById(
                                                com.stratedgy.dsebuuma.alist.orm.model.Movie.class,
                                                favoriteMovieId
                                        );

                        List<com.stratedgy.dsebuuma.alist.orm.model.Youtube> movieTrailers =
                                com.stratedgy.dsebuuma.alist.orm.model.Youtube
                                        .generateFromApiMovie(movie, savedMovie);

                        for (com.stratedgy.dsebuuma.alist.orm.model.Youtube trailer:movieTrailers) {
                            trailer.save();
                        }

                        Toast.makeText(getContext(), "Movie successfully favorited", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void updateViewFromDb(Long id) {
        final com.stratedgy.dsebuuma.alist.orm.model.Movie movie =
                com.stratedgy.dsebuuma.alist.orm.model.Movie.findById(
                        com.stratedgy.dsebuuma.alist.orm.model.Movie.class, id
                );
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

        List<com.stratedgy.dsebuuma.alist.orm.model.Youtube> movieTrailers = movie.getYoutubeTrailers();
        mTrailerViewAdapter = new TrailerAdapter(
                getContext(),
                R.layout.trailer_movie_item,
                (ArrayList) movieTrailers
        );
        mTrailerView.setAdapter(mTrailerViewAdapter);

        mTrailerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                com.stratedgy.dsebuuma.alist.orm.model.Youtube trailer =
                        (com.stratedgy.dsebuuma.alist.orm.model.Youtube) parent.getItemAtPosition(position);

                String youtubeUri = "http://www.youtube.com/watch?v=";
                String trailerId = trailer.getSource();

                try {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("vnd.youtube:" + trailerId)
                    );
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(youtubeUri + trailerId)
                    );
                    startActivity(intent);
                }
            }
        });

        mFavoriteButton.setVisibility(View.INVISIBLE);
    }
}
