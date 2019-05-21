package com.example.admin.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.Manifest.permission.WRITE_CALENDAR;
import static java.lang.String.format;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.PeriodFormat;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, DatePickerDialog.OnDateSetListener {

    ArrayList<String> Coordinates = new ArrayList<String>();
    Calendar c;

    String Address;
    String EventTitle;

    int ChosenYear, ChosenMonth, ChosenDay;

    private NotificationManagerCompat notificationManager;

    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mDatabaseHelper = new DatabaseHelper(this);

        notificationManager = NotificationManagerCompat.from(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Clear any previous markers
        googleMap.clear();

        GoogleMap mMap = googleMap;

        //Set a listener for when a marker is clicked
        mMap.setOnInfoWindowClickListener(this);


        // Get the coordinates generated from the JSON request in the friends class
        Coordinates = getIntent().getStringArrayListExtra("Coordinates");
        //Get the current coordinates for the user
        double MyLongitude = Double.parseDouble(getIntent().getStringExtra("Longitude"));
        double MyLatitude = Double.parseDouble(getIntent().getStringExtra("Latitude"));
        //Get the name of the activity
        EventTitle = getIntent().getStringExtra("EventType");

        //Set the coordinates for the users location
        LatLng MyLocation = new LatLng(MyLatitude,MyLongitude);

        //Add the marker for the users location onto the map and set its colour to blue
        mMap.addMarker(new MarkerOptions().position(MyLocation).title("My Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MyLocation));

        //Loop through all of the coordinates supplied for each place and place a marker down for the location
        for(int i =0; i < Coordinates.size(); i+=4) {

                //Set a variable for the places latitude, longitude name of the place and its address
                double Lat = Double.parseDouble(Coordinates.get(i));
                double Lng = Double.parseDouble(Coordinates.get(i+1));
                final String name = Coordinates.get(i+2);
                String Address = Coordinates.get(i+3);

                LatLng Place = new LatLng(Lat,Lng);

                mMap.addMarker((new MarkerOptions().position(Place).title(name).snippet(Address)));

                mMap.moveCamera(CameraUpdateFactory.newLatLng(Place));
        }

        //Set the zoom for the camera
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 13.0f));
    }

    //Function where the user will choose a date from a calendar
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, year); // Stores the year
        c.set(Calendar.MONTH, month); // Stores the month - 1 (EG May shows up as 4 not five)
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth); // Stores the day

        //Declare the global variables to allow use in other parts of the code
        ChosenYear = year;
        ChosenMonth = month;
        ChosenDay = dayOfMonth;

        //Work out the time until the chosen event day in milliseconds
        long timeUntilDate = calcTime(ChosenDay, ChosenMonth, ChosenYear);

        //Create a notification to appear for midnight of the day of the event
        createNotification(timeUntilDate);

        //Add that this event has been created to a database
        AddData();

        //Creates the event once the date has been set and calls the users calendar
        createEvent();


    }

    //Calls the Database Helper class and adds data
    public void AddData() {
        //Get the current date and time to add tot he database
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String DateNow = dateFormat.format(date);

        //Call the addData function in the DatabaseHelper class to add the activity and time of creation
        boolean insertData = mDatabaseHelper.addData(EventTitle, DateNow);

        //If the data was added successfully, then display the event has been created
        if (insertData) {
            Toast.makeText(this, "Event Succesfully Created!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to make event.", Toast.LENGTH_SHORT).show();
        }
    }


    //Listener for if the markers info box is clicked
    @Override
    public void onInfoWindowClick(final Marker marker) {

        //Create a message asking if the user wants to make this an event, with a "yes" and "no" option accompanying
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Make this an event?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    //If the user presses yes...
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Sets the address for the marker clicked
                        Address = marker.getSnippet();

                        //Check to see if the permission for the calendar has been granted or not. If not then ask again
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                            //Request Access to the calendar as unable to gain access prior
                            ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                                    new String[]{WRITE_CALENDAR},
                                    1);

                        }
                        else {
                            //Call the function for the user to choose a date for the event
                            callDatePicker();
                        }
                    }
                })
                //If the user chooses no, then close the pop up
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        //Display the message
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //Create the event and call the calendar app
    private void createEvent() {

        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, EventTitle); //Specify the title as the type of event
        calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, Address); // Add in the address of the event

        GregorianCalendar calDate = new GregorianCalendar(ChosenYear, ChosenMonth, ChosenDay); //Add in the day chosen by the date picker
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.getTimeInMillis());
        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDate.getTimeInMillis());

        startActivity(calIntent);
    }

    //Calls and displays the date picker
    private void callDatePicker() {

        //Call the Calendar Date picker fragment
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");

    }

    //This function will calculate the time in seconds until the time specified by the datepicker
    public long calcTime(int EventDay, int EventMonth, int EventYear) {

        long MilliSecondsBetween = 0;

        LocalDate dateBefore = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            //Get the current date
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String DateNow = dateFormat.format(date);

            //Extract the current date
            String split[] = DateNow.split(" ");
            String CurrentDate = split[0];
            String CurrentTime = split[1];

            //Extract the individual year month and day
            String SplitCurrentDate[] = CurrentDate.split("/");
            int CurrentYear = Integer.parseInt(SplitCurrentDate[0]);
            int CurrentMonth = Integer.parseInt(SplitCurrentDate[1]);
            int CurrentDay = Integer.parseInt(SplitCurrentDate[2]);


            //Work out how many days are between the event and now
            dateBefore = LocalDate.of(CurrentYear, CurrentMonth, CurrentDay);
            LocalDate dateAfter = LocalDate.of(EventYear, EventMonth, EventDay);
            long noOfDaysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);

            //Convert the days into milliseconds
            MilliSecondsBetween = noOfDaysBetween * 24 * 60 *60 * 1000;

            //Extract the individual hour minute and second
            String SplitCurrentTime[] = CurrentTime.split(":");
            int CurrentHour = Integer.parseInt(SplitCurrentTime[0]);
            int CurrentMinute = Integer.parseInt(SplitCurrentTime[1]);
            int CurrentSecond = Integer.parseInt(SplitCurrentTime[2]);

            //Work out how many hours minutes and seconds are left until the clock strikes 12
            int HoursBetween = 24 - CurrentHour;
            int MinutesBetween = 60 - CurrentMinute;
            int SecondsBetween = 60 - CurrentSecond;

            //Work out the final amount of Milliseconds left
            MilliSecondsBetween = MilliSecondsBetween + (HoursBetween *60 *60 *1000) + (MinutesBetween * 60 * 1000) + (SecondsBetween * 1000);

        }

        return MilliSecondsBetween;
    }

    //Create the notification for midnight of the day of the event
    public void createNotification(long time) {
        //Create an alarm
        AlarmManager alarms = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        //Create the receiver instances from the Receiver class
        Receiver receiver = new Receiver();
        IntentFilter filter = new IntentFilter("ALARM_ACTION");
        registerReceiver(receiver, filter);

        //Create the intent to be called come time of the event
        Intent intent = new Intent("ALARM_ACTION");
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Call the notification once the event has arrived
        alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+time, operation) ;


    }

    //Once a notification occurs this class will be called to create the reminder
    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // TODO Auto-generated method stub

            Notification notification = new NotificationCompat.Builder(getApplicationContext(), "channel1")
                    .setSmallIcon(R.drawable.ic_alert)
                    .setContentTitle(EventTitle)
                    .setContentText("You have an event coming up!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .build();

            notificationManager.notify(1, notification);
        }

    }

}
