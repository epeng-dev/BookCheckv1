package com.stack_test.chekchek.bookcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stack_test.chekchek.component.OnSingleClickListener;
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



public class LoginActivity extends AppCompatActivity {
    private String strUrl;
    private String Json;
    private String ID;
    private String Passward;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText etID = (EditText) findViewById(R.id.etID);
        final EditText etPW = (EditText) findViewById(R.id.etPW);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextInputLayout IDWrapper = (TextInputLayout) findViewById(R.id.layout_LoginID);
        final TextInputLayout PWWrapper = (TextInputLayout) findViewById(R.id.layout_LoginPW);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegister);
        final String libraries = "test_library";
        final SharedPreferences IDpref = getSharedPreferences("ID", MODE_PRIVATE);
        final SharedPreferences PWpref = getSharedPreferences("Passward", MODE_PRIVATE);

        IDWrapper.setHint("ID");
        PWWrapper.setHint("PassWord");

        bLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                new AsyncTask<Void, Void, Void>(){

                    @Override
                    protected Void doInBackground(Void... params) {
                        CookieManager cookieManager = new CookieManager();
                        CookieHandler.setDefault(cookieManager);
                        try {
                            URL url = new URL(strUrl);
                            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
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

                            StringBuilder responsestringBuilder = new StringBuilder();
                            if(httpsURLConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                                for (; ; ) {
                                    String string = bufferedReader.readLine();
                                    if (string == null) break;
                                    responsestringBuilder.append(string + "\n");
                                }
                                bufferedReader.close();
                            }
                            httpsURLConnection.disconnect();
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
                        ID = etID.getText().toString();
                        Passward = etPW.getText().toString();
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

                        if(Boolean.parseBoolean(Data)){
                            SharedPreferences.Editor editor = IDpref.edit();
                            editor.putString("ID", ID);
                            editor.apply();
                            editor = PWpref.edit();
                            editor.putString("Passward", Passward);
                            editor.apply();
                            Bundle extras = new Bundle();
                            extras.putString("Libraries", libraries);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtras(extras);
                            LoginActivity.this.startActivity(intent);
                            finish();
                        }
                        else{
                            String reason = parser.Get("reason");
                            Toast.makeText(LoginActivity.this, reason, Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });
    }

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}

