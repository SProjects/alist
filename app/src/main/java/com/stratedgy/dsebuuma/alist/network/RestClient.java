package com.stratedgy.dsebuuma.alist.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {
    private final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private Api apiService;

    public RestClient() {
        this.apiService = retrofit.create(Api.class);
    }

    public Api getApiService() {
        return apiService;
    }

}
