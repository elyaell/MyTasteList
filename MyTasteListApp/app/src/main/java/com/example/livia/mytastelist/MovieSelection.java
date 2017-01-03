package com.example.livia.mytastelist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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

public class MovieSelection extends AppCompatActivity {
    ImageView imageView ;
    TextView tvTitle ;
    TextView tvInfos ;
    TextView tvCast ;
    TextView tvPlot ;
    TextView tvGenre ;
    String username ;
    String id_data ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        id_data = getIntent().getStringExtra("id_film") ;
        username = getIntent().getStringExtra("username") ;

        Log.d("id_film", id_data) ;
        Log.d("username", username) ;

        imageView = (ImageView) findViewById(R.id.img);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvInfos = (TextView) findViewById(R.id.informations);
        tvGenre = (TextView) findViewById(R.id.tvGenre);
        tvCast = (TextView) findViewById(R.id.tvCast);
        tvPlot = (TextView) findViewById(R.id.tvPlot);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncAdd().execute(tvTitle.getText().toString(), username) ;
            }
        });

        new AsyncLoading().execute(id_data) ;
    }

    private class AsyncAdd extends AsyncTask<String, String, String>{
        ProgressDialog pdLoading = new ProgressDialog(MovieSelection.this) ;
        HttpURLConnection connexion = null ;
        URL url = null ;

        @Override
        protected void onPreExecute(){
            super.onPreExecute() ;
            pdLoading.setMessage("Loading...") ;
            pdLoading.setCancelable(false) ;
            pdLoading.show() ;
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(RoadsURL.URL_SAVE);

                connexion = (HttpURLConnection) url.openConnection();
                connexion.setRequestMethod("POST");
                connexion.setDoInput(true);
                connexion.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", params[1]) ;
                builder.appendQueryParameter("name_film", params[0]);

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
                    JSONObject object = new JSONObject(line);
                    if((object.getString("success")).equals("false")) return "Unsuccessful" ;
                }
                return "Successful";
            } catch (MalformedURLException e) {
                return "Error";
            } catch (IOException e) {
                return "Error";
            } catch (JSONException e) {
                return "Error";
            } finally {
                connexion.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            if (result.equals("Successful")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Movie add !", Toast.LENGTH_LONG);
                toast.show();
            } else if (result.equals("Error")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't Reach the Server", Toast.LENGTH_LONG);
                toast.show();
            } else if (result.equals("Unsuccessful")){
                Toast toast = Toast.makeText(getApplicationContext(), "Already in your profile", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private class AsyncLoading extends AsyncTask<String, String, String>{
        ProgressDialog pdLoading = new ProgressDialog(MovieSelection.this) ;
        HttpURLConnection connexion = null ;
        URL url = null ;

        String name ;
        String back_poster ;
        String cast ;
        String year ;
        String nb_seasons ;
        String nb_episodes ;
        String plot ;
        String status ;
        String genre ;
        Bitmap image = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute() ;
            pdLoading.setMessage("Loading...") ;
            pdLoading.setCancelable(false) ;
            pdLoading.show() ;
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL(RoadsURL.URL_GET_INFORMATIONS) ;

                connexion = (HttpURLConnection)url.openConnection() ;
                connexion.setRequestMethod("POST") ;
                connexion.setDoInput(true) ;
                connexion.setDoOutput(true) ;

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("id_data", strings[0]) ;
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

                    JSONObject jsonObject = new JSONObject(line);
                    JSONObject actor = new JSONObject(jsonObject.get("movies").toString()) ;

                        name = actor.getString("name");
                        year = actor.getString("year") ;
                        genre = actor.getString("genre") ;
                        plot = actor.getString("plot") ;
                        cast = actor.getString("actors") ;
                        nb_episodes = actor.getString("number_of_episodes") ;
                        nb_seasons = actor.getString("number_of_seasons") ;
                        status = actor.getString("status") ;
                        back_poster = actor.getString("back") ;


                    InputStream in = new java.net.URL(back_poster).openStream();
                    image = BitmapFactory.decodeStream(in);

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
                String informations ;
                if(nb_seasons.equals("0"))  // Movie
                    informations = year ;
                else  // TV Show
                    informations = year + " | " + nb_episodes + "E | " + nb_seasons + "S | " + status ;
                tvTitle.setText(name);
                tvInfos.setText(informations) ;
                tvGenre.setText(genre);
                tvCast.setText(cast);
                tvPlot.setText(plot);
                imageView.setImageBitmap(image);

            } else if (result.equals("Unsuccessful")) {
                Toast toast = Toast.makeText(getApplicationContext(), "nope", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
