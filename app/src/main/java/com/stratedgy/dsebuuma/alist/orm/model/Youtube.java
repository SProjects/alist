package com.stratedgy.dsebuuma.alist.orm.model;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class Youtube extends SugarRecord{
    String source;
    Movie movie;

    public Youtube () {

    }

    public Youtube (String source, Movie movie) {
        this.source = source;
        this.movie = movie;
    }

    public String getSource() {
        return source;
    }

    public static List<Youtube> generateFromApiMovie(com.stratedgy.dsebuuma.alist.model.Movie apiMovie, Movie dbMovie) {
        List<Youtube> youtubeTrailers = new ArrayList<Youtube>();
        List<com.stratedgy.dsebuuma.alist.model.Youtube> trailers = apiMovie.getTrailers().getYoutube();
        for (com.stratedgy.dsebuuma.alist.model.Youtube trailer:trailers) {
            youtubeTrailers.add(new Youtube(trailer.getSource(), dbMovie));
        }
        return youtubeTrailers;
    }
}
