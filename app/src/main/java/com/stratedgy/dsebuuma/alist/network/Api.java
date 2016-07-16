package com.stratedgy.dsebuuma.alist.network;

import com.stratedgy.dsebuuma.alist.BuildConfig;
import com.stratedgy.dsebuuma.alist.model.Movie;
import com.stratedgy.dsebuuma.alist.model.Movies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {
    String API_KEY = "?api_key=" + BuildConfig.MOVIE_DB_API_KEY;

    @GET("popular" + API_KEY)
    Call<Movies> getMovies();

    @GET("{id}" + API_KEY + "&append_to_response=trailers")
    Call<Movie> getMovie(@Path("id") int id);

}
