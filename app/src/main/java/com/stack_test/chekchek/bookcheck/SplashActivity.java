package com.stack_test.chekchek.bookcheck;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.stack_test.chekchek.http.JsonParser;
import com.stack_test.chekchek.login.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class SplashActivity extends Activity {
    private String Json;
    private String strUrl;
    private String ID;
    private String Passward;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView imageView = (ImageView) findViewById(R.id.Splashimage);
        final SharedPreferences IDpref = getSharedPreferences("ID", MODE_PRIVATE);
        final SharedPreferences PWpref = getSharedPreferences("Passward", MODE_PRIVATE);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.raw.splash).into(imageViewTarget);
        if(IDpref.getString("ID", "")!=""){
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    ID = IDpref.getString("ID", "");
                    Passward = PWpref.getString("Passward", "");
                    CookieManager cookieManager = new CookieManager();
                    CookieHandler.setDefault(cookieManager);
                    try {
                        URL url = new URL(strUrl);
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        httpsURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
                        httpsURLConnection.setRequestMethod("POST");
                        httpsURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpsURLConnection.setDoOutput(true);
                        httpsURLConnection.setDoInput(true);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("ID", ID);
                        jsonObject.put("password", Passward);
                        OutputStream outputStream = httpsURLConnection.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        bufferedWriter.write(jsonObject.toString());
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        outputStream.close();
                        httpsURLConnection.connect();

                        StringBuilder responsestringBuilder = new StringBuilder();
                        if (httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                            for (; ; ) {
                                String string = bufferedReader.readLine();
                                if (string == null) break;
                                responsestringBuilder.append(string + "\n");
                            }
                            bufferedReader.close();
                        }
                        httpsURLConnection.connect();
                        Json = responsestringBuilder.toString();

                    } catch (IOException | JSONException e1) {
                        e1.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    strUrl = "https://www.xxx.xxx/API/login";
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    JsonParser parser = null;

                    try {
                        parser = new JsonParser(Json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String Data = parser.Get("success");

                    if(Boolean.parseBoolean(Data) == true){
                        Bundle extras = new Bundle();
                        extras.putString("Libraries", "test_library");
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtras(extras);
                        SplashActivity.this.startActivity(intent);
                        finish();
                    }
                    else{
                        String reason = parser.Get("reason");
                        Toast.makeText(SplashActivity.this, reason, Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
        else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }, 7850);
        }


        }
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}

