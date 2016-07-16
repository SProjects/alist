package com.stratedgy.dsebuuma.alist.model;

import java.util.ArrayList;
import java.util.List;

public class Movies {

    private int page;
    private List<Movie> results = new ArrayList<Movie>();
    private int total_movies;
    private int total_pages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getMovies() {
        return results;
    }

    public void setMovies(List<Movie> movies) {
        this.results = movies;
    }

    public int getTotalMovies() {
        return total_movies;
    }

    public void setTotalMovies(int totalMovies) {
        this.total_movies = totalMovies;
    }

    public int getTotalPages() {
        return total_pages;
    }

    public void setTotalPages(int totalPages) {
        this.total_pages = totalPages;
    }

}
