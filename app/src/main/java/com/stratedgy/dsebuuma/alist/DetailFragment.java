package com.stratedgy.dsebuuma.alist;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarTransactionHelper;
import com.squareup.picasso.Picasso;
import com.stratedgy.dsebuuma.alist.model.Movie;
import com.stratedgy.dsebuuma.alist.model.Youtube;
import com.stratedgy.dsebuuma.alist.network.Api;
import com.stratedgy.dsebuuma.alist.network.RestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String POSTER_URI = "https://image.tmdb.org/t/p/w185";
    private String YOUTUBE_URI = "http://www.youtube.com/watch?v=";

    private ShareActionProvider mShareActionProvider;
    private String mShareableTrailer;
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
        setHasOptionsMenu(true);
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
        if (Utility.isOnline(getContext())) {
            RestClient restClient = new RestClient();

            Api apiService = restClient.getApiService();
            Call<Movie> call = apiService.getMovie(id);
            call.enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(Call<Movie> call, Response<Movie> response) {
                    final Movie movie = response.body();
                    mShareableTrailer = YOUTUBE_URI + movie.getTrailers().getYoutube().get(0).getSource();

                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareTrailerIntent());
                    }

                    String imageUri = POSTER_URI + movie.getPosterPath();

                    mTitleView.setText(movie.getOriginalTitle());
                    mOverviewView.setText(movie.getOverview());
                    mReleaseView.setText(Utility.getYearFromDateString(movie.getReleaseDate()));
                    mLengthView.setText(Utility.formatRuntime(getContext(), movie.getRuntime()));
                    mRatingView.setText(Utility.formatRating(getContext(), (float) movie.getVoteAverage()));

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
                                        Uri.parse(YOUTUBE_URI + trailerId)
                                );
                                startActivity(intent);
                            }
                        }
                    });


                    if (isMovieFavorited(movie)) {
                        mFavoriteButton.setVisibility(View.INVISIBLE);
                    } else {
                        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SugarTransactionHelper.doInTransaction(new SugarTransactionHelper.Callback() {
                                    @Override
                                    public void manipulateInTransaction() {
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
                                    }
                                });

                                if (isMovieFavorited(movie)) {
                                    mFavoriteButton.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(), "Movie successfully favorited", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to favorite movie", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Movie> call, Throwable t) {
                    Toast.makeText(
                            getContext(), "Failed to fetch movie " + t.getMessage(),
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

    private void updateViewFromDb(Long id) {
        final com.stratedgy.dsebuuma.alist.orm.model.Movie movie =
                com.stratedgy.dsebuuma.alist.orm.model.Movie.findById(
                        com.stratedgy.dsebuuma.alist.orm.model.Movie.class, id
                );
        mShareableTrailer = YOUTUBE_URI + movie.getYoutubeTrailers().get(0).getSource();

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }

        String imageUri = POSTER_URI + movie.getPosterPath();

        mTitleView.setText(movie.getOriginalTitle());
        mOverviewView.setText(movie.getOverview());
        mReleaseView.setText(Utility.getYearFromDateString(movie.getReleaseDate()));
        mLengthView.setText(Utility.formatRuntime(getContext(), movie.getRuntime()));
        mRatingView.setText(Utility.formatRating(getContext(), (float) movie.getVoteAverage()));

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (mShareableTrailer != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareableTrailer);
        return shareIntent;
    }

    private boolean isMovieFavorited(Movie movie) {
        return com.stratedgy.dsebuuma.alist.orm.model.Movie.find(
                com.stratedgy.dsebuuma.alist.orm.model.Movie.class,
                "movie_id = ?", String.valueOf(movie.getId())
        ).size() > 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(getContext())
                    .edit();
            editor.putString(
                    getContext().getString(R.string.pref_sort_term_key),
                    getContext().getString(R.string.pref_default_sort_term)
            );
            editor.apply();
        }
        return true;
    }
}
