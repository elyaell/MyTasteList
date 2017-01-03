package com.example.livia.mytastelist;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MoviesListGenre extends AppCompatActivity {
    String username ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_genre);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String genre = getIntent().getStringExtra("Genre") ;
        username = getIntent().getStringExtra("username") ;

        new AsyncLoading().execute(genre) ;
    }

    private class AsyncLoading extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(MoviesListGenre.this) ;
        HttpURLConnection connexion = null ;
        URL url = null ;

        ArrayList<String> id_list ;
        ArrayList<String> names ;
        ArrayList<String> posters ;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            names = new ArrayList<>() ;
            posters = new ArrayList<>() ;
            id_list = new ArrayList<>() ;
            pdLoading.setMessage("Loading...") ;
            pdLoading.setCancelable(false) ;
            pdLoading.show() ;
        }

        @Override
        protected String doInBackground(String[] params) {
            try {
                url = new URL(RoadsURL.URL_SHOW_BY_GENRE) ;

                connexion = (HttpURLConnection)url.openConnection() ;
                connexion.setRequestMethod("POST") ;
                connexion.setDoInput(true) ;
                connexion.setDoOutput(true) ;

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("genre", params[0]) ;
                String query = builder.build().getEncodedQuery();

                OutputStream os = connexion.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                connexion.connect();

                int response_code = connexion.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = connexion.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    String line = reader.readLine() ;
                    String tst ;
                    while((tst = reader.readLine()) != null)
                        line += tst ;
                    Log.d("line", line) ;

                    JSONObject jsonResult = new JSONObject(line);
                    if(jsonResult.getString("success").equals("false")) return "Unsuccessful" ;

                    JSONObject jsonMovies = new JSONObject(jsonResult.getString("movies")) ;
                    for(int i = 0 ; i < jsonMovies.length() ; i++){
                        JSONObject movie = new JSONObject(jsonMovies.get(Integer.toString(i)).toString()) ;
                        names.add(movie.getString("name")) ;
                        id_list.add(movie.getString("id_data")) ;
                        posters.add(movie.getString("poster")) ;
                    }


                    return "Successful" ;
                } else return "Unsuccessful" ;

            } catch (MalformedURLException e) {
                return "URL Exception" ;
            } catch (IOException e) {
                return "Connexion Exception" ;
            } catch (JSONException e) {
                e.printStackTrace();
                return "JSON Exception" ;
            } finally {
                connexion.disconnect() ;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            if(result.equals("Successful")) {

                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(MoviesListGenre.this, names, posters, id_list, username);
                    GridLayoutManager lLayout = new GridLayoutManager(MoviesListGenre.this, 3);
                    RecyclerView rView = (RecyclerView)findViewById(R.id.recycler_view);
                    rView.setHasFixedSize(true);
                    rView.setLayoutManager(lLayout);
                    RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(MoviesListGenre.this, names, posters, id_list, username);
                    rView.setAdapter(rcAdapter);

            } else if (result.equals("Unsuccessful")) {
                Toast toast = Toast.makeText(getApplicationContext(), "nope", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
