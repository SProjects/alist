package com.stratedgy.dsebuuma.alist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle arguments = new Bundle();

            arguments.putInt("poster", intent.getIntExtra("poster", R.drawable.placeholder));
            arguments.putString("title", intent.getStringExtra("title"));

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }
}
