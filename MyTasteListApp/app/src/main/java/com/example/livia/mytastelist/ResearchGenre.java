package com.example.livia.mytastelist;

import android.content.Intent;
import android.os.Bundle ;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ResearchGenre extends AppCompatActivity {

    ListView mListView ;
    String[] genres = new String[]{
            "Horror", "Comedy", "Drama", "Romance", "Action", "Animation",
            "Short", "Mystery", "Documentary", "Thriller", "Fantasy", "Music",
            "Biography", "Crime", "Adventure", "Sport", "Sci-Fi", "Family",
            "History", "Western", "Adult"
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_genre);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ResearchGenre.this,
                android.R.layout.simple_list_item_1, genres);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String itemValue = (String) mListView.getItemAtPosition(position);
                Intent myIntent = new Intent(ResearchGenre.this, MoviesListGenre.class);
                myIntent.putExtra("Genre", itemValue) ;
                myIntent.putExtra("username", getIntent().getStringExtra("username")) ;
                startActivity(myIntent);
            }

        });

    }

}
