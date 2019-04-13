package com.example.searchapiexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ImageView searchPic;
    TextInputEditText searchText;
    String searchString;
    JSONArray itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchPic = findViewById(R.id.picSearch);
        searchText = findViewById(R.id.SearchText);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);



        Glide.with(this).load("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png").apply(options).into(searchPic);
        searchString = "";
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((View view) -> {
            Log.d("REST",""+searchString.equals(searchText.getText().toString()));
             if(searchString.equals(searchText.getText().toString())){
                 try {
                     Log.d("TEST", itemList.toString());
                     int i = itemList.length();
                     i = (int) (Math.random() * i);
                     //String urlimg = itemList.getJSONObject(i).getJSONObject("pagemap").getJSONArray("cse_image").getJSONObject(0).getString("src");
                     String urlimg = itemList.getJSONObject(i).getString("contentUrl");
                     Glide.with(this).load(urlimg).apply(options).into(searchPic);
                 }
                 catch (Exception e){
                     Log.d("TEST", e.toString());
                 }
             }
             else{
                 searchString = searchText.getText().toString();
                 new ConnectionRest(
                         (json) -> {
                             if(json != null)
                                 try {

                                     //new DownloadImageTask(findViewById(R.id.picSearch))
                                     //        .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");
                                     itemList = new JSONArray(((JSONObject) json).getJSONArray("value").toString());

                                     int i = itemList.length();
                                     i = (int) (Math.random()*i);
                                     //String urlimg = itemList.getJSONObject(i).getJSONObject("pagemap").getJSONArray("cse_image").getJSONObject(0).getString("src");
                                     String urlimg = itemList.getJSONObject(i).getString("contentUrl");

                                     Glide.with(this).load(urlimg).apply(options).into(searchPic);

                                 } catch (Exception e) {
                                     Log.d("REST ERROR", e.getMessage());
                                 }
                         }
                 ).execute(searchString);
             }

                Log.d("TEST","clicked");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
