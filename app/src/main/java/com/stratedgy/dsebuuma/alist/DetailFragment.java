package com.stratedgy.dsebuuma.alist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailFragment extends Fragment {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private String title;
    private int posterResourceId;

    private ImageView mPosterView;
    private TextView mTitleView;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mPosterView = (ImageView) rootView.findViewById(R.id.movie_detail_poster);
        mTitleView = (TextView) rootView.findViewById(R.id.movie_detail_title);

        Bundle arguments = getArguments();
        if (arguments != null) {
            title = arguments.getString("title");
            posterResourceId = arguments.getInt("poster");

            mTitleView.setText(title);
            Picasso.with(getContext())
                    .load(posterResourceId)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(mPosterView);
        }

        return rootView;
    }

}
