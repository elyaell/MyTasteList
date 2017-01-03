package com.example.livia.mytastelist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class UserColdStart extends AppCompatActivity {
    EditText film_name ;
    Button search_film ;
    ArrayList<String> names;
    ArrayList<String> year;
    ArrayList<String> choices ;
    Boolean finish = false ;
    String username ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cold_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        film_name = (EditText) findViewById(R.id.film_name) ;
        search_film = (Button) findViewById(R.id.search_film) ;
        username = getIntent().getStringExtra("username") ;

        displayInfosDialog() ;


        names = new ArrayList<>();
        year = new ArrayList<>();
        choices = new ArrayList<>();

        search_film.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(film_name.getText().toString() == "") {
                    Toast toast = Toast.makeText(getApplicationContext(), "Empty Field", Toast.LENGTH_LONG);
                    toast.show();
                } else new UserColdStart.AsyncLoading().execute(film_name.getText().toString()) ;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choices.size() >= 5) {
                    Intent intent = new Intent(UserColdStart.this, ViewList.class);
                    intent.putExtra("choices", choices);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private class AsyncLoading extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(UserColdStart.this);
        HttpURLConnection connexion = null;
        URL url = null;
        ListView listView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listView = (ListView) findViewById(R.id.listView);
            names.clear();
            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(RoadsURL.URL_SEARCH_FILM);

                connexion = (HttpURLConnection) url.openConnection();
                connexion.setRequestMethod("POST");
                connexion.setDoInput(true);
                connexion.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("movie_name", params[0]);
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
                    String line = reader.readLine();
                    String tst ;
                    while((tst = reader.readLine()) != null)
                        line += tst ;

                    Log.d("line", line) ;

                    JSONObject jsonResult = new JSONObject(line);
                    if(jsonResult.getString("success").equals("false")) return "Unsuccessful" ;

                    JSONObject jsonMovies = new JSONObject(jsonResult.getString("movies")) ;
                    Log.d("jsonMovies", jsonMovies.toString()) ;

                    for (int i = 0; i < jsonMovies.length(); i++)
                        names.add(jsonMovies.getString(Integer.toString(i)));

                    return "Successful";
                } else return "Unsuccessful";
            } catch (MalformedURLException e) {
                return "Error";
            } catch (IOException e) {
                return "Error";
            } catch (JSONException e) {
                e.printStackTrace();
                return "Unsuccessful";
            } finally {
                connexion.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            if (result.equals("Successful")) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UserColdStart.this,
                        android.R.layout.simple_list_item_1, names);
                listView.setAdapter(adapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String itemValue = (String) listView.getItemAtPosition(position);
                        choices.add(itemValue);

                        Toast toast = Toast.makeText(getApplicationContext(), itemValue + " add", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            } else if (result.equals("Unsuccessful")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Meeeh, nothing in the database for this !", Toast.LENGTH_LONG);
                toast.show();
            } else if (result.equals("Error")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't Reach the Server", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void displayInfosDialog() {
        Context context = UserColdStart.this;
        String title = "Informations";
        String message = "You gonna have to select at least 5 Films or TV Shows that you like !" ;

        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.show();
    }
}
