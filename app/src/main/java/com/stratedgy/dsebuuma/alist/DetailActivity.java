package com.stratedgy.dsebuuma.alist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final String FAVORITE_SORT = getApplicationContext().getString(R.string.pref_favorite_sort_term);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle arguments = new Bundle();

            if (FAVORITE_SORT.equals(Utility.getPreferredSortTerm(getApplicationContext()))) {
                arguments.putLong("dbId", intent.getLongExtra("dbId", 1));
            } else {
                arguments.putInt("apiId", intent.getIntExtra("apiId", 550));
            }

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
}
