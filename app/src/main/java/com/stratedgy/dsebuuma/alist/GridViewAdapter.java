package com.stratedgy.dsebuuma.alist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.stratedgy.dsebuuma.alist.model.Movie;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();
    private String POSTER_URI = "https://image.tmdb.org/t/p/w185";
    private Movie apiMovie;
    private com.stratedgy.dsebuuma.alist.orm.model.Movie dbMovie;

    public GridViewAdapter(Context context, int resource, ArrayList data) {
        super(context, resource, data);

        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        String imageUri;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.moviePoster = (ImageView) row.findViewById(R.id.movie_poster);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        if (Utility.isInFavoriteMode(context)) {
            dbMovie = (com.stratedgy.dsebuuma.alist.orm.model.Movie) data.get(position);
            imageUri = POSTER_URI + dbMovie.getPosterPath();
        } else {
            apiMovie = (Movie) data.get(position);
            imageUri = POSTER_URI + apiMovie.getPosterPath();
        }

        Picasso.with(context)
                .load(imageUri)
                .fit()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.moviePoster);

        return row;
    }

    static class ViewHolder {
        ImageView moviePoster;
    }
}
