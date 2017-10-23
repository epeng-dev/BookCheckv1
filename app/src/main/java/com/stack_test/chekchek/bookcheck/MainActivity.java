package com.stack_test.chekchek.bookcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.stack_test.chekchek.http.HttpCookies;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by 10201Kangminsub on 2016-09-25.
 */
public class MainActivity extends AppCompatActivity{
    String Json;
    String TAG = "Main";
    Boolean login = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button Recommend = (Button) findViewById(R.id.bRecommend);
        final Button Borrow = (Button) findViewById(R.id.bIntroduce);
        final Button Search = (Button) findViewById(R.id.bSearch);
        final Button Logout = (Button) findViewById(R.id.bLogOut);
        final String library = getIntent().getStringExtra("Libraries");
        ImageView imageView = (ImageView) findViewById(R.id.mainImage);


        Recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putString("Libraries", library);
                Intent intent = new Intent(MainActivity.this, RecommendActivity.class);
                intent.putExtras(extras);
                MainActivity.this.startActivity(intent);
            }
        });
        Borrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putString("Libraries", library);
                Intent intent = new Intent(MainActivity.this, BorrowActivity.class);
                intent.putExtras(extras);
                MainActivity.this.startActivity(intent);
            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();
                extras.putString("Libraries", library);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtras(extras);
                MainActivity.this.startActivity(intent);
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(){
                    @Override
                    public void run(){
                        String urlString = "https://www.xxx.xxx/API/logout";
                        String search = Search.getText().toString();
                        try{
                            URL url = new URL(urlString);
                            SharedPreferences pref = getSharedPreferences("ID", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.commit();
                            pref = getSharedPreferences("Password", MODE_PRIVATE);
                            editor = pref.edit();
                            editor.clear();
                            editor.commit();
                            trustAllHosts();
                            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                            httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                                @Override
                                public boolean verify(String hostname, SSLSession session) {
                                    return true;
                                }
                            });

                            HttpCookies httpCookies = new HttpCookies(httpsURLConnection);
                            HttpURLConnection connection = httpCookies.connection;
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type", "application/json");
                            connection.setRequestProperty("Accept", "application/json");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);


                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("noGET",login);

                            OutputStream outputStream = connection.getOutputStream();
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            bufferedWriter.write(jsonObject.toString());

                            bufferedWriter.flush();
                            bufferedWriter.close();
                            outputStream.close();
                            connection.connect();

                            StringBuilder responseStringBuilder = new StringBuilder();
                            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                for(;;){
                                    String stringline = bufferedReader.readLine();
                                    if(stringline == null) break;
                                    responseStringBuilder.append(stringline + '\n');
                                }
                                bufferedReader.close();
                            }

                            connection.disconnect();
                            Json = responseStringBuilder.toString();
                            Log.d(TAG, responseStringBuilder.toString());

                        }catch (MalformedURLException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                };
                JsonParser parser = null;

                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    parser = new JsonParser(Json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String Data = parser.Get("success");
                if(Data == "true"){
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(intent);
                    finish();
                }

            }
        });
    }

    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
