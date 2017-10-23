package com.stack_test.chekchek.bookcheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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

public class BarcodeReadActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;
    private String Json;
    private String library;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_read);

        imageView = (ImageView) findViewById(R.id.barcode);
        textView = (TextView) findViewById(R.id.barcodeTEXT);
        library = getIntent().getStringExtra("Libraries");
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("책의 바코드가 보이도록 찍어주세요");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "스캔을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
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
                            jsonObject.put("libraryID", library);
                            jsonObject.put("bookCode", result.getContents());

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
                try {
                    parser = new JsonParser(Json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String success = parser.Get("success");
                if(Boolean.getBoolean(success)){
                    imageView.setImageResource(R.drawable.success);
                    textView.setText("대출이 완료되었습니다.");
                }
                else{
                    imageView.setImageResource(R.drawable.warning);
                    textView.setText(parser.Get("reason"));
                }

            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
