package com.stratedgy.dsebuuma.alist;

public class GridItem {
    private int moviePosterResource;
    private String movieTitle;

    public GridItem(int resourceId, String title) {
        super();
        this.moviePosterResource = resourceId;
        this.movieTitle = title;
    }

    public int getMoviePosterResource() {
        return this.moviePosterResource;
    }

    public String getMovieTitle() {
       return this.movieTitle;
    }

}
