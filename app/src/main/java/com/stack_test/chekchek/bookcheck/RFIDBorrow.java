package com.stack_test.chekchek.bookcheck;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class RFIDBorrow extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter[] filters;
    String hex;
    String UUID="";
    String Json;
    String TAG = "RFID";
    TextView textView;
    ImageView imageView;
    String library;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfidborrow);

        textView = (TextView) findViewById(R.id.RFIDTEXT);
        imageView = (ImageView) findViewById(R.id.RFIDICON);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        library = getIntent().getStringExtra("Libraries");
        if(nfcAdapter == null){
            Toast.makeText(this, "NFC가 지원되지 않는 기기입니다. 사용에 불편을 드려 죄송합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "이 서비스는 NFC기능이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(i);
        }

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(nfcAdapter != null){
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause(){
        if(nfcAdapter != null){
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


        Thread thread = new Thread(){
            @Override
            public void run() {
                String urlString = "https://www.xxx.xxx/API/user/borrowBook";
                try {
                    URL url = new URL(urlString);
                    if (tag != null) {
                        byte[] tagID = tag.getId();
                        for (int i = 0; i < 4; i++) {
                            if ((tagID[i] & 0xFF) < (byte) 16) {
                                hex = "0" + Integer.toHexString(tagID[i] & 0xFF) + "_";
                            } else {
                                hex = Integer.toHexString(tagID[i] & 0xFF) + "_";
                            }

                            UUID += hex;
                        }
                    }

                    UUID = UUID.substring(0, UUID.length() - 1);
                    UUID = UUID.toUpperCase();

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
                    jsonObject.put("libraryID", library);
                    jsonObject.put("bookCode", UUID);
                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    bufferedWriter.write(jsonObject.toString());

                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    connection.connect();
                    UUID = "";
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
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        JsonParser parser = null;

        thread.start();


        try{
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try{
            parser = new JsonParser(Json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String success = parser.Get("success");

        if(Boolean.parseBoolean(success) == true) {
            textView.setText("책 대출이 완료되었습니다.\n이용해주셔서 감사합니다.");
            imageView.setImageResource(R.drawable.success);
        }
        else{
            String reason = parser.Get("reason");
            textView.setText(reason);
            imageView.setImageResource(R.drawable.warning);
        }
    }
}
