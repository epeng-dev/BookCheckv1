package com.stack_test.chekchek.http;

import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class HttpCookies {
    public static java.net.CookieManager msCookieManager = new java.net.CookieManager();
    public HttpURLConnection connection;
    public HttpCookies(HttpsURLConnection httpsURLConnection){
        connection = httpsURLConnection;
    }
    public void setMsCookieManager(){
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
    }

}
