package com.stack_test.chekchek.bookcheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class BookinfoActivity extends AppCompatActivity {
    private ListViewItem item;
    String Json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookinfo);
        final String libraries = getIntent().getStringExtra("Libraries");
        item = (ListViewItem) getIntent().getSerializableExtra("item");

        ImageView imageView = (ImageView) findViewById(R.id.infopicture);
        TextView textTitle = (TextView) findViewById(R.id.infoTitle);
        TextView textAuthor = (TextView) findViewById(R.id.infoAuthor);
        TextView description = (TextView) findViewById(R.id.infoBook);
        Button bLight = (Button) findViewById(R.id.bRequest);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setClickable(false);

        Glide.with(this).load(item.BookimageURL).into(imageView);
        textTitle.setText(item.Title);
        textAuthor.setText(item.Author);
        description.setText(item.Description);

        bLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(){
                    @Override
                    public void run(){
                        String urlString = "https://www.xxx.xxx/API/user/light";

                        try{
                            URL url = new URL(urlString);
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
                            jsonObject.put("libraryID", libraries);
                            jsonObject.put("ISBN", item.ISBN);

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
                                    responseStringBuilder.append(stringline).append('\n');
                                }
                                bufferedReader.close();
                            }

                            connection.disconnect();
                            Json = responseStringBuilder.toString();

                        } catch (IOException | JSONException e){
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

                if(Boolean.parseBoolean(Data)){
                    String where = parser.Get("lightedBookcaseNumber");
                    if(where.length()!=4){
                        Bundle extras = new Bundle();
                        extras.putString("lightColor", parser.Get("lightColor"));
                        extras.putString("BookcaseNumber", parser.Get("lightedBookcaseNumber"));
                        extras.putString("Title", item.Title);
                        extras.putString("Author", item.Author);
                        extras.putString("ISBN", item.ISBN);
                        Intent intent = new Intent(BookinfoActivity.this, LightActivity.class);
                        intent.putExtras(extras);
                        BookinfoActivity.this.startActivity(intent);
                    }
                    else {
                        Toast.makeText(BookinfoActivity.this, "책이 책장에 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    String reason = parser.Get("reason");
                    Toast.makeText(BookinfoActivity.this, reason, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
