package com.example.livia.mytastelist;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
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

public class UserMoviesList extends AppCompatActivity {
    String username ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("username") ;
        Log.d("username Movie list", username) ;
        new AsyncLoading().execute() ;
    }



    private class AsyncLoading extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(UserMoviesList.this) ;
        HttpURLConnection connexion = null ;
        URL url = null ;
        ListView listView ;
        ArrayList<String> names ;
        ArrayList<String> id_data ;
        ArrayList<String> posters ;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            names = new ArrayList<>() ;
            id_data = new ArrayList<>() ;
            posters = new ArrayList<>() ;
            listView = (ListView) findViewById(R.id.listView) ;
            pdLoading.setMessage("Loading...") ;
            pdLoading.setCancelable(false) ;
            pdLoading.show() ;
        }

        @Override
        protected String doInBackground(String[] params) {
            try {
                url = new URL(RoadsURL.URL_GET_FILMS) ;

                connexion = (HttpURLConnection)url.openConnection() ;
                connexion.setRequestMethod("POST") ;
                connexion.setDoInput(true) ;
                connexion.setDoOutput(true) ;

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", username) ;
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
                        id_data.add(movie.getString("id_data")) ;
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


                RecyclerViewAdapter adapter = new RecyclerViewAdapter(UserMoviesList.this, names, posters, id_data, username);
                GridLayoutManager lLayout = new GridLayoutManager(UserMoviesList.this, 3);
                RecyclerView rView = (RecyclerView)findViewById(R.id.recycler_view);
                rView.setHasFixedSize(true);
                rView.setLayoutManager(lLayout);
                RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(UserMoviesList.this, names, posters, id_data, username);
                rView.setAdapter(rcAdapter);


            } else if (result.equals("Unsuccessful")) {
                Toast toast = Toast.makeText(getApplicationContext(), "nope", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

}
