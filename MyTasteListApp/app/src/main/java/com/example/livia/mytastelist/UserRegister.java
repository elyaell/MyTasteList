package com.example.livia.mytastelist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import static com.example.livia.mytastelist.R.id.bRegister;

public class UserRegister extends AppCompatActivity {
    EditText birth;
    EditText password;
    EditText username;
    EditText email;
    Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        birth = (EditText) findViewById(R.id.etBirth);
        password = (EditText) findViewById(R.id.etPass);
        username = (EditText) findViewById(R.id.etUsername);
        email = (EditText) findViewById(R.id.etEmail);
        register = (Button) findViewById(bRegister);

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO => checke syntaxes
                if(birth.getText().toString() == ""
                        || password.getText().toString() == ""
                        || username.getText().toString() == ""
                        || email.getText().toString() == "") {
                    Toast toast = Toast.makeText(getApplicationContext(), "Empty Field(s)", Toast.LENGTH_LONG);
                    toast.show();
                } else new AsyncLoading().execute(username.getText().toString(), password.getText().toString(), email.getText().toString(), birth.getText().toString());
            }
        });
    }

    private class AsyncLoading extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(UserRegister.this) ;
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
                url = new URL(RoadsURL.URL_REGISTER) ;

                connexion = (HttpURLConnection)url.openConnection() ;
                connexion.setRequestMethod("POST") ;
                connexion.setDoInput(true) ;
                connexion.setDoOutput(true) ;

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", params[0]) ;
                builder.appendQueryParameter("password", params[1]) ;
                builder.appendQueryParameter("email", params[2]) ;
                builder.appendQueryParameter("birth", params[3]) ;
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
                    JSONObject object = new JSONObject(line);
                    if((object.getString("success")).equals("true")) return "Successful" ;
                    else return "Unsuccessful" ;
                } else return "Unsuccessful" ;
            } catch (MalformedURLException e) {
                return "Error" ;
            } catch (IOException e) {
                return "Error" ;
            } catch (JSONException e) {
                e.printStackTrace();
                return "Error" ;
            } finally {
                connexion.disconnect() ;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            if (result.equals("Successful")) {
                Intent intent = new Intent(getBaseContext(), UserColdStart.class) ;
                intent.putExtra("username", username.getText().toString()) ;
                startActivity(intent) ;
                finish () ;
            } else if (result.equals("Unsuccessful")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Already Registered", Toast.LENGTH_LONG);
                toast.show();
            } else if (result.equals("Error")){
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't Reach the Server", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}