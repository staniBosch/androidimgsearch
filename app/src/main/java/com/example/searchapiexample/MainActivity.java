package com.example.searchapiexample;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView searchPic;
    TextInputEditText searchText;
    String searchString;
    JSONArray itemList;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private LocationManager locationManager;
    public List<List<String>> queries;
    private static final String TAG = "MainActivity";
    public List<Bitmap> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchPic = findViewById(R.id.picSearch);
        searchText = findViewById(R.id.SearchText);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        queries = new LinkedList<>();
        Log.d(TAG, "onCreate: 1");

        handleWeather();

        Log.d(TAG, "onCreate: 2");
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);


        Log.d(TAG, "onCreate: 3");
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
        public void handleQuery() {
            String joinedQuery = TextUtils.join(" ", queries.get(0));
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round);
            new ConnectionRest(
                    (json) -> {
                        if (json != null)
                            try {

                                //new DownloadImageTask(findViewById(R.id.picSearch))
                                //        .execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");
                                ((JSONObject) json).getJSONArray("items");
                                String urlimg = ((JSONObject) json).getJSONArray("items").getJSONObject(0).getJSONObject("pagemap").getJSONArray("cse_image").getJSONObject(0).getString("src");

                                Glide.with(this).load(urlimg).apply(options).into(searchPic);

                            } catch (Exception e) {
                                Log.d("REST ERROR", e.getMessage());
                            }
                    }
            ).execute(joinedQuery);

        }

        public void handleWeather() {
            Log.d(TAG, "handleWeather: 1");
            if (checkLocationPermission()) {
                Log.d(TAG, "handleWeather: 2");
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.d(TAG, "handleWeather: 3");
                    Awareness.getSnapshotClient(this).getWeather()
                            .addOnSuccessListener(new OnSuccessListener<WeatherResponse>() {

                                @Override
                                public void onSuccess(WeatherResponse weatherResponse) {

                                    Log.d(TAG, "Humidity: " + weatherResponse.getWeather().getHumidity());
                                    Log.d(TAG, "Dew Point: " + weatherResponse.getWeather().getDewPoint(Weather.CELSIUS));
                                    Log.d(TAG, "FeelsLike Temperature: " + weatherResponse.getWeather().getFeelsLikeTemperature(Weather.CELSIUS));
                                    Log.d(TAG, "Temperature: " + weatherResponse.getWeather().getTemperature(Weather.CELSIUS));
                                    int i = 1;
                                    LinkedList<String> query = null;
                                    for (Integer condition : weatherResponse.getWeather().getConditions()) {
                                        Log.d("log4", "i: " + i + " Condition: " + condition);
                                        switch (condition) {
                                            case 1:
                                                query = new LinkedList<>();
                                                query.add("Sonne");
                                                queries.add(query);
                                                break;
                                            case 2:
                                                query = new LinkedList<>();
                                                query.add("Wolken");
                                                queries.add(query);
                                                Log.d("log4", "queries.size: " + queries.size());
                                                break;
                                            case 3:
                                                query = new LinkedList<>();
                                                query.add("Nebel");
                                                queries.add(query);
                                                break;
                                            case 4:  // Wäre Weather.HAZY -> schwierig, ein passendes Bild zu finden
                                                break;
                                            case 5:
                                                query = new LinkedList<>();
                                                query.add("Glatteis");
                                                queries.add(query);
                                                break;
                                            case 6:
                                                query = new LinkedList<>();
                                                query.add("Regen");
                                                queries.add(query);
                                                break;
                                            case 7:
                                                query = new LinkedList<>();
                                                query.add("Schnee");
                                                queries.add(query);
                                                break;
                                            case 8:
                                                query = new LinkedList<>();
                                                query.add("Gewitter");
                                                queries.add(query);
                                                break;
                                            case 9:
                                                query = new LinkedList<>();
                                                query.add("Sturm");
                                                queries.add(query);
                                                break;
                                        }
                                    }
                                    handleLocation();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG, "Failed: " + e);
                                }
                            });
                }
                else {
                    Toast.makeText(this, "GPS ist nicht aktiviert", Toast.LENGTH_LONG).show();
                }
            }
        }

        public void handleActivity() {
            if (checkLocationPermission()) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Awareness.getSnapshotClient(this).getDetectedActivity()
                            .addOnSuccessListener(new OnSuccessListener<DetectedActivityResponse>() {

                                @Override
                                public void onSuccess(DetectedActivityResponse detectedActivityResponse) {

                                    ActivityRecognitionResult activityRecognitionResult = detectedActivityResponse.getActivityRecognitionResult();
                                    Log.d(TAG, "Most probable activity: " + activityRecognitionResult.getMostProbableActivity());

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG, "Failed: " + e);
                                }
                            });
                }
            }
        }

        public void handleLocation() {
            if (checkLocationPermission()) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Awareness.getSnapshotClient(this).getLocation()
                            .addOnSuccessListener(new OnSuccessListener<LocationResponse>() {

                                @Override
                                public void onSuccess(LocationResponse locationResponse) {
                                    Location location = locationResponse.getLocation();
                                    Log.d(TAG, location.getLatitude() + " , " + location.getLongitude() + " , " + location.getAltitude());
                                    Geocoder geocoder = new Geocoder(getApplicationContext());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        if (addresses.isEmpty()) {
                                            Log.d(TAG, "Waiting for location");
                                        } else {
                                            if (addresses.size() > 0) {
                                                Log.d(TAG, addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality()
                                                        + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                                                for (List<String> query : queries) {
                                                    query.add(addresses.get(0).getLocality());
                                                }
                                                // handleActivity(); // Könnte man auch evt. mit einbeziehen
                                                handleQuery();
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG, "Failed: " + e);
                                }
                            });

                } else {
                    Toast.makeText(this, "GPS ist nicht aktiviert", Toast.LENGTH_LONG).show();
                }

            }
        }

        public boolean checkLocationPermission() {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.title_location_permission_request)
                            .setMessage(R.string.text_location_permission)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            })
                            .create()
                            .show();


                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // location-related task you need to do.
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                            //Request location updates:
                            //locationManager.requestLocationUpdates(provider, 400, 1, this);

                        }

                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.

                    }
                    return;
                }

            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            if(checkLocationPermission()){
                handleWeather();
            }
        }

        @Override
        protected void onPause() {
            super.onPause();
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//
//        }
        }

        // Eine weitere Idee:
//    public void setWallpaper(){
//        ImageView imagePreview = (ImageView)findViewById(R.id.preview);
//        imagePreview.setImageResource(R.drawable.five);
//
//        WallpaperManager myWallpaperManager
//                = WallpaperManager.getInstance(getApplicationContext());
//        try {
//            myWallpaperManager.setResource(R.drawable.five);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

}
