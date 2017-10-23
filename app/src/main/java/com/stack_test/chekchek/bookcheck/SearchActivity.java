package com.stack_test.chekchek.bookcheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stack_test.chekchek.http.HttpCookies;
import com.stack_test.chekchek.http.JsonParser;
import com.stack_test.chekchek.login.R;

import org.json.JSONArray;
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
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SearchActivity extends AppCompatActivity {
    String Json;
    String TAG = "search";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText Search = (EditText) findViewById(R.id.etSearch);
        final Button Searchbutton = (Button) findViewById(R.id.bSearch);
        final TextInputLayout SearchWrapper = (TextInputLayout) findViewById(R.id.layout_SearchBook);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rgSearchfor);
        final RadioButton rTitleButton = (RadioButton) findViewById(R.id.rSearchforTitle);
        final TextView tSearch = (TextView)findViewById(R.id.tSearch);
        final String library = getIntent().getStringExtra("Libraries");
        rTitleButton.setChecked(true);
        SearchWrapper.setHint("입력");
        tSearch.setText("제목으로 검색하려면 title" + "\n" + "ISBN으로 검색하려면 ISBN" + "\n" +"버튼을 선택해주세요" +"\n" + "아무 것도 치지 않으시면 모든 책이 검색됩니다.");
        Searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(id);
                final String searchby = radioButton.getText().toString();

                Thread thread = new Thread(){
                    @Override
                    public void run(){
                        String urlString = "https://www.xxx.xxx/API/user/searchBooks";
                        String search = Search.getText().toString();
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

                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(library);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("libraries", jsonArray);
                            jsonObject.put("searchBy", searchby);
                            jsonObject.put("searchingFor", search);

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

                        }catch (JSONException | IOException e){
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

                if(Boolean.parseBoolean(Data) == true){
                    String bookinformation = parser.Get("bookInformation");
                    if(bookinformation.equals("{}")){
                        Toast.makeText(SearchActivity.this, "검색하려는 책이 책장에 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else
                        try{
                        ArrayList<String> ISBN = parser.GetISBN("books");
                            ArrayList<String> Available = parser.GetAvailable("books");
                        ArrayList<String> Author = new ArrayList<String>();
                            ArrayList<String> Title = new ArrayList<String>();
                            ArrayList<String> Description = new ArrayList<String>();
                            ArrayList<String> Url = new ArrayList<String>();
                            for(int i=0; i<ISBN.size(); i++){
                                Author.add(i, parser.Get("bookInformation", "author", ISBN.get(i).toString()));
                                Title.add(i,  parser.Get("bookInformation", "title", ISBN.get(i).toString()));
                                Description.add(i, parser.Get("bookInformation", "description", ISBN.get(i).toString()));
                                Url.add(i, "https://ll.0o0.moe/API/bookImage/"+ISBN.get(i).toString());
                            }

                        Bundle extras = new Bundle();
                        extras.putStringArrayList("ISBN", ISBN);
                        extras.putStringArrayList("Title", Title);
                        extras.putStringArrayList("Author", Author);
                        extras.putStringArrayList("Description", Description);
                        extras.putStringArrayList("Url", Url);
                        extras.putString("Libraries", library);
                            extras.putStringArrayList("Available", Available);
                        Intent intent = new Intent(SearchActivity.this, BooklistActivity.class);
                        intent.putExtras(extras);
                        SearchActivity.this.startActivity(intent);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
                else{
                    String reason = parser.Get("reason");
                    Toast.makeText(SearchActivity.this, reason, Toast.LENGTH_LONG).show();
                }
                }


        });
    }
}


