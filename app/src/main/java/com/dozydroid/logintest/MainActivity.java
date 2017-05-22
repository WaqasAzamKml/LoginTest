package com.dozydroid.logintest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etEmail.getText())){
                    etEmail.setError("Required");
                    etEmail.requestFocus();
                }else if(TextUtils.isEmpty(etPassword.getText())){
                    etPassword.setError("Required");
                    etPassword.requestFocus();
                }else{
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();
                    new BackgroundWorker(MainActivity.this).execute(email, password);
                }

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dialog!=null)
            dialog.cancel();
    }

    /*******
     * Inner BackgroundWorker Class
     *******/
    class BackgroundWorker extends AsyncTask<String,Void,String> {
        Context context;
//        ProgressDialog dialog;
        BackgroundWorker(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading, please wait");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String login_url = "http://crazemo.com/aff/login.php";
            String email = params[0];
            String password = params[1];
            try {
                URL url = new URL(login_url);
                //URL url = new URL(login_url+"login.php");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");

                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                String result = "";
                String line = "";
                while((line = bufferedReader.readLine())!=null){
                    result += line;
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Error";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            dialog.cancel();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
