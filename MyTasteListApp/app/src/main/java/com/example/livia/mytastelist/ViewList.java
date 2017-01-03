package com.example.livia.mytastelist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ViewList extends AppCompatActivity {
    ArrayList<String> name_films ;
    String username ;
    ListView listView ;
    TextView textView ;
    Button save ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView) ;
        textView = (TextView) findViewById(R.id.goBack) ;
        save = (Button) findViewById(R.id.save) ;

        name_films = getIntent().getStringArrayListExtra("choices") ;
        username = getIntent().getStringExtra("username") ;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewList.this,
                android.R.layout.simple_list_item_1, name_films);
        listView.setAdapter(adapter);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(ViewList.this, UserColdStart.class);
                ViewList.this.startActivity(registerIntent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                for(int i = 0 ; i < name_films.size() ; i++)
                    new AsyncLoading().execute(username, name_films.get(i)) ;
            }
        });
    }


    private class AsyncLoading extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(ViewList.this);
        HttpURLConnection connexion = null;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(RoadsURL.URL_SAVE);

                connexion = (HttpURLConnection) url.openConnection();
                connexion.setRequestMethod("POST");
                connexion.setDoInput(true);
                connexion.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", params[0]) ;
                builder.appendQueryParameter("name_film", params[1]);

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
                Toast toast = Toast.makeText(getApplicationContext(), "Datas entered !", Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(getBaseContext(), MainActivity.class) ;
                startActivity(intent) ;
                finish () ;
            } else if (result.equals("Error") || result.equals("Unsuccessful")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't Reach the Server", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

}
