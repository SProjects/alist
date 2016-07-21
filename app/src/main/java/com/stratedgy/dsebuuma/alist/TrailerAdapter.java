package com.stratedgy.dsebuuma.alist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TrailerAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();

    public TrailerAdapter(Context context, int resource, ArrayList data) {
        super(context, resource, data);

        this.context = context;
        this.layoutResourceId = resource;
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
            holder.trailerPosition = (TextView) row.findViewById(R.id.trailer_position_text_view);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.trailerPosition.setText("Trailer " + (position + 1));
        return row;
    }

    static class ViewHolder {
        TextView trailerPosition;
    }
}
