package com.example.admin.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class friends extends AppCompatActivity implements View.OnClickListener {

    //Declare all the buttons and images.


    Button PicnicBtn;
    ImageView PicnicImg;

    Button RestaurantBtn;
    ImageView RestaurantImg;

    Button ThemeParkBtn;
    ImageView ThemeParkImg;

    Button BowlingBtn;
    ImageView BowlingImg;

    Button PubBtn;
    ImageView PubImg;

    Button MuseumBtn;
    ImageView MuseumImg;

    Button CinemaBtn;
    ImageView CinemaImg;

    double UserLongitude;
    double UserLatitude;
    Location location;

    private Context mContext;
    String mJSONURLString;

    String place;
    String EventType;


    TextView Header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //Assign all the buttons and images to variables, as well as any button onclick listeners
        PicnicBtn = (Button) findViewById(R.id.PicnicBtn);
        PicnicBtn.setOnClickListener(this);
        PicnicImg = (ImageView) findViewById(R.id.PicnicPic);

        RestaurantBtn = (Button) findViewById(R.id.RestaurantsBtn);
        RestaurantBtn.setOnClickListener(this);
        RestaurantImg = (ImageView) findViewById(R.id.restaurantsPic);

        ThemeParkBtn = (Button) findViewById(R.id.themeParkBtn);
        ThemeParkBtn.setOnClickListener(this);
        ThemeParkImg = (ImageView) findViewById(R.id.themeParkPic);

        BowlingBtn = (Button) findViewById(R.id.BowlingBtn);
        BowlingBtn.setOnClickListener(this);
        BowlingImg = (ImageView) findViewById(R.id.bowlingPic);

        PubBtn = (Button) findViewById(R.id.PubBtn);
        PubBtn.setOnClickListener(this);
        PubImg = (ImageView) findViewById(R.id.pubPic);

        MuseumBtn = (Button) findViewById(R.id.MuseumBtn);
        MuseumBtn.setOnClickListener(this);
        MuseumImg = (ImageView) findViewById(R.id.MuseumPic);


        CinemaBtn = (Button) findViewById(R.id.CinemaBtn);
        CinemaBtn.setOnClickListener(this);
        CinemaImg = (ImageView) findViewById(R.id.CinemaPic);

        //Declare the textView widget
        Header = (TextView) findViewById(R.id.FriendHeader);

        //Get the number of people attending the activity
        Intent PeopleInfo = getIntent();
        String NumberOfPeople = PeopleInfo.getStringExtra("NumberOfPeopleSend");

        //Get if it is a date or a mate
        String DateOrMate = PeopleInfo.getStringExtra("Date");

        //Change the UI based on how many and who is attending
        display(NumberOfPeople, DateOrMate);

        //Request the Calendar permission and location permission.
        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION, Manifest.permission.WRITE_CALENDAR},
                1);

        //Get the current location service
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Set up the listener for the current location
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20, 10, locationListener);

    }



    //function that manipulates the UI depending on how many people are attending the event
    public void display(String People, String DateOrMate) {

        if(!"Date".equals(DateOrMate)) {
            switch (People) {
                case "2":

                    ThemeParkBtn.setVisibility(View.GONE);
                    ThemeParkImg.setVisibility(View.GONE);
                    break;
                case "3":
                    RestaurantImg.setVisibility((View.GONE));
                    RestaurantBtn.setVisibility(View.GONE);
                    break;
                case "4":
                    CinemaImg.setVisibility(View.GONE);
                    CinemaBtn.setVisibility(View.GONE);
                    break;
                case "5":
                    PicnicImg.setVisibility(View.GONE);
                    PicnicBtn.setVisibility(View.GONE);
                    break;
                default:
                    PicnicImg.setVisibility(View.GONE);
                    PicnicBtn.setVisibility(View.GONE);
                    MuseumImg.setVisibility(View.GONE);
                    MuseumBtn.setVisibility(View.GONE);

                    break;
            }
        }
        else {
            //If the Date option is selected, don't display the museum details
            MuseumImg.setVisibility(View.GONE);
            MuseumBtn.setVisibility(View.GONE);

            Header.setText("Choose an activity for you and your date!");

        }
    }

    private final LocationListener locationListener = new LocationListener() {
        //If the location changes then change the users longitude and latitude
        public void onLocationChanged(Location location) {
            UserLongitude = location.getLongitude();
            UserLatitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onClick(View v) {
        //If the user does not have access to the location permission then request it again
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request Access to the phones Location as unable to gain access prior
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    1);
        }
        //If the user does have access then create a JSONRequest
        else {

            //Set the variables to send to the maps activity depending on the users choice
            //And call a JSON request to find locations available
            switch (v.getId()) {
                case R.id.CinemaBtn:
                    place = "movie_theater";
                    EventType = "Cinema";
                    new JSONRequest().execute("");
                    break;

                case R.id.PicnicBtn:
                    place = "park";
                    EventType = "Picnic";
                    new JSONRequest().execute("");

                    break;
                case R.id.RestaurantsBtn:
                    place = "restaurant";
                    EventType = "Restaurant";
                    new JSONRequest().execute("");

                    break;
                case R.id.themeParkBtn:
                    place = "amusement_park";
                    EventType = "Theme Park";
                    new JSONRequest().execute("");

                    break;
                case R.id.BowlingBtn:
                    place = "bowling_alley";
                    EventType = "Bowling";
                    new JSONRequest().execute("");

                    break;
                case R.id.PubBtn:
                    place = "bar";
                    EventType = "Pub";
                    new JSONRequest().execute("");

                    break;
                case R.id.MuseumBtn:
                    place = "museum";
                    EventType = "Museum";
                    new JSONRequest().execute("");

                    break;
            }
        }


    }


    private class JSONRequest extends AsyncTask<String, Void, String> {

        //An array of co-ordinates to send to the maps activity.
        final ArrayList<String> Coordinates = new ArrayList<String>();

        @Override
        protected String doInBackground(String... params) {

            // Get the application context
            mContext = getApplicationContext();

            mJSONURLString = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + Double.toString(UserLatitude) + "," + Double.toString(UserLongitude) + "&radius=2000&type=" + place + "&key=AIzaSyA8E8REAhUehZej0fdi9T-eB1HqC0G8YbA";

           // Toast.makeText(getApplicationContext(), "Start of Sycnh task", Toast.LENGTH_LONG).show();
            Log.d("Info","Start of Async Task");

            // Initialize a new RequestQueue instance
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);

            // Initialize a new JsonObjectRequest instance
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, mJSONURLString, null,

                    //Waits for the response of the JSON request
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // Process the JSON
                            try{
                                // Get the JSON results array
                                JSONArray array = response.getJSONArray("results");

                                // Loop through the array elements
                                for(int i=0;i<array.length();i++){
                                    // Get current json object
                                    JSONObject place = array.getJSONObject(i);

                                    JSONObject geometry = place.getJSONObject("geometry");

                                    JSONObject location = geometry.getJSONObject("location");


                                    //Add the longitude, latitude and place name to an Array List
                                    Coordinates.add(location.getString("lat"));
                                    Coordinates.add(location.getString("lng"));
                                    Coordinates.add(place.getString("name"));
                                    Coordinates.add(place.getString("vicinity"));

                                    Log.d("Current Latitude:", location.getString("lat"));

                                }

                                Intent maps = new Intent(getApplicationContext(), MapsActivity.class);
                                maps.putStringArrayListExtra("Coordinates", Coordinates);
                                maps.putExtra("Longitude", Double.toString(UserLongitude));
                                maps.putExtra("Latitude", Double.toString(UserLatitude));
                                maps.putExtra("EventType", EventType);

                                startActivity(maps);


                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Display the error in a toast message if it occurs
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            Log.d("Error: ", error.toString());

                        }
                    }
            );

            // Add JsonObjectRequest to the RequestQueue
            requestQueue.add(jsonObjectRequest);

            return "Executed";
        }

    }

}