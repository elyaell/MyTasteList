package com.example.livia.mytastelist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class UserLogin extends AppCompatActivity {
    EditText etUser;
    EditText etPass;
    Button bLogin;
    TextView registerLink;
    CheckBox checkBox ;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = (EditText) findViewById(R.id.etUsername);
        etPass = (EditText) findViewById(R.id.etPass);
        bLogin = (Button) findViewById(R.id.bLogin);
        registerLink = (TextView) findViewById(R.id.tvRegister);
        checkBox = (CheckBox) findViewById(R.id.checkBox) ;

        registerLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(UserLogin.this, UserRegister.class);
                UserLogin.this.startActivity(registerIntent);
            }
        });

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit() ;
        etUser.setText(loginPreferences.getString("username", ""));
        etPass.setText(loginPreferences.getString("password", ""));
        checkBox.setChecked(true);

        bLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (etUser.getText().toString() == ""
                        || etPass.getText().toString() == "") {
                    Toast toast = Toast.makeText(getApplicationContext(), "Empty Field(s)", Toast.LENGTH_LONG);
                    toast.show();
                } else
                    new AsyncLoading().execute(etUser.getText().toString(), etPass.getText().toString());
            }
        });
    }

    private class AsyncLoading extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(UserLogin.this);
        HttpURLConnection connexion = null;
        URL url = null;
        String name ;

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
                url = new URL(RoadsURL.URL_LOGIN);

                connexion = (HttpURLConnection) url.openConnection();
                connexion.setRequestMethod("POST");
                connexion.setDoInput(true);
                connexion.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("username", params[0]);
                builder.appendQueryParameter("password", params[1]);
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

                    JSONObject jsonObject = new JSONObject(line);
                    if(jsonObject.getString("success").equals("false")) return "Unsuccessful" ;
                    name = jsonObject.getString("username") ;

                    return "Successful" ;
                } else return "Unsuccessful";
            } catch (MalformedURLException e) {
                return "Error";
            } catch (IOException e) {
                return "Error";
            } catch (JSONException e) {
                e.printStackTrace();
                return "Error";
            } finally {
                connexion.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            if (result.equals("Successful")) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                if(checkBox.isChecked()){
                    loginPrefsEditor.clear() ;
                    loginPrefsEditor.putString("username", etUser.getText().toString());
                    loginPrefsEditor.putString("password", etPass.getText().toString());
                    loginPrefsEditor.commit();
                }
                intent.putExtra("username", name) ;
                startActivity(intent);
                finish();
            } else if (result.equals("Unsuccessful")) {
                Toast toast = Toast.makeText(getApplicationContext(), "User Doesn't Exist", Toast.LENGTH_LONG);
                toast.show();
            } else if (result.equals("Error")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Couldn't Reach the Server", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}