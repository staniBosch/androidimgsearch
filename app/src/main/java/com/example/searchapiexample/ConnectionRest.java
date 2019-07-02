package com.example.searchapiexample;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectionRest extends AsyncTask<String, Void, Object> {

    private Consumer<Object> fun;
    public ConnectionRest (Consumer<Object> c){
        this.fun = c;
    }
    public ConnectionRest (){
        super();
    }

    @Override
    protected Object doInBackground(String... params) {
        String urlstring;
        urlstring = "https://www.googleapis.com/customsearch/v1?key=AIzaSyB-3iDuPKlwhiWQPoPKEiKWosoLyTqhbWk&cx=001292501706504981640:vdkc5g3tuao&q="+params[0];
        //urlstring = "https://api.cognitive.microsoft.com/bing/v7.0/images/search?q="+params[0];
        try {
            URLEncoder.encode(urlstring, "UTF-8");
        }catch (UnsupportedEncodingException e){
            Log.d("REST",e.toString());
        }
        OkHttpClient client = new OkHttpClient();
        Request request;
        //POST
        if(params.length>1){
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, params[1]);
            request = new Request.Builder()
                    .url(urlstring)
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if(response.body()!=null){
                    JSONObject responseJSON = new JSONObject(response.body().string());
                    Log.d("XRESTPOST", urlstring + params[1]);
                    return responseJSON;
                }
                else return null;
            } catch (Exception e){
                Log.d("XRESTError", "couldnt send :"+params[1]+" errmsg: "+e.getMessage());
            }
        }//GET
        else{
            request = new Request.Builder()
                    .url(urlstring)
                    //.addHeader("Ocp-Apim-Subscription-Key","0438467ceea34c3f9231dfb35f114cce")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Log.d("XRESTGET", urlstring);
                if(response.body()!=null)
                    return new JSONObject(response.body().string());
                else return response;
            } catch (Exception e){
                Log.d("XRESTError", "couldnt get :"+urlstring);
                Log.d("XRESTError", e.toString());
                //if(this.fun!=null)
                //    this.fun.accept(null);
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(Object jsonarrdata) {
        super.onPostExecute(jsonarrdata);
        if(this.fun!=null)
            this.fun.accept(jsonarrdata);
    }
}