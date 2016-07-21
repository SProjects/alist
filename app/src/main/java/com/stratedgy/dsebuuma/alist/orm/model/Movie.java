package com.stratedgy.dsebuuma.alist.orm.model;

import com.orm.SugarRecord;

import java.util.List;

public class Movie extends SugarRecord{
    int runtime;
    String posterPath;
    String overview;
    String releaseDate;
    int movieId;
    String originalTitle;
    double voteAverage;

    public Movie() {

    }

    public Movie(int movieId, int runtime, String posterPath, String overview, String releaseDate,
                 String originalTitle, double voteAverage) {
        this.movieId = movieId;
        this.runtime = runtime;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.originalTitle = originalTitle;
        this.voteAverage = voteAverage;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public int getRuntime() {
        return runtime;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public List<Youtube> getYoutubeTrailers() {
        return Youtube.find(Youtube.class, "movie = ?", String.valueOf(this.getId()));
    }

    public static Movie generateFromApiMovie(com.stratedgy.dsebuuma.alist.model.Movie movie) {
        return new Movie(
                movie.getId(), movie.getRuntime(), movie.getPosterPath(), movie.getOverview(),
                movie.getReleaseDate(), movie.getOriginalTitle(), movie.getVoteAverage()
        );
    }
}
