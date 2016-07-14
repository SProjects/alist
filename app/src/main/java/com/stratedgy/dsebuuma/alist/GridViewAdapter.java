package com.stratedgy.dsebuuma.alist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();

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

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.movieTitle = (TextView) row.findViewById(R.id.movie_title);
            holder.moviePoster = (ImageView) row.findViewById(R.id.movie_poster);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        GridItem item = (GridItem) data.get(position);
        holder.moviePoster.setImageResource(item.getMoviePosterResource());
        holder.movieTitle.setText(item.getMovieTitle());

        return row;
    }

    static class ViewHolder {
        ImageView moviePoster;
        TextView movieTitle;
    }
}
