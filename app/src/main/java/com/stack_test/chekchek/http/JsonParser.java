package com.stack_test.chekchek.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JsonParser{
    private String JsonString;
    private JSONArray JSONARRAY;
    private JSONObject OBJECTROOT ;
    public JsonParser(String jsonString) throws JSONException {
        this.JsonString =jsonString;
        this.OBJECTROOT = new JSONObject(jsonString);

    }

    public JSONArray GetJsonArray(String name) throws JSONException{
        return new JSONArray(OBJECTROOT.getString(name));
    }
    public String Get(String ID) {
    try {
        return OBJECTROOT.getString(ID);
    }
    catch (JSONException e)
    {
        return null;
    }
    }

    public JSONObject getJSONObject(String ID) throws JSONException {
            return OBJECTROOT.getJSONObject(ID);
    }

    public ArrayList<String> GetISBN(String JSONArray) throws JSONException {
        JSONArray books = GetJsonArray(JSONArray);
        ArrayList<String> bookISBNs = new ArrayList<String>();
        for(int i = 0; i < books.length(); i++)
        {
           bookISBNs.add(books.getJSONObject(i).getString("ISBN"));
        }
        return bookISBNs;
    }

    public ArrayList<String> GetAvailable(String JSONArray) throws  JSONException{
        JSONArray books = GetJsonArray(JSONArray);
        Boolean[] available = null;
        ArrayList<String> Available = new ArrayList<String>();
        for(int i=0; i<books.length(); i++){
            Available.add(books.getJSONObject(i).getString("available"));
        }

        return Available;
    }

    public String Get(String parentID, String childrenID, String ISBN) throws JSONException {
        JSONObject Datas = new JSONObject(Get(parentID));
        JSONObject Data = new JSONObject(Datas.getString(ISBN));
        return Data.getString(childrenID);
    }
}
