package com.example.admin.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Spinners are drop down menus with different options
    Spinner MateOrDate;
    Spinner HowManyPeople;

    Button PartnerDate;
    Button FriendsDate;
    Button HistoryBtn;

    TextView HowManyFriendstxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ad listener for the history button
        HistoryBtn = findViewById(R.id.HistoryBtn);
        HistoryBtn.setOnClickListener(this);

        //Add listener for partner button
        PartnerDate = (Button) (findViewById(R.id.FindDateBtn));
        PartnerDate.setOnClickListener(this);

        //Add listener for friends button
        FriendsDate = (Button) (findViewById(R.id.FriendEventBtn));
        FriendsDate.setOnClickListener(this);

        HowManyFriendstxt = (TextView) (findViewById(R.id.FriendsText));

        //declare the first spinner and add dropdown options
        MateOrDate = findViewById(R.id.WhoWithOption);
        String[] items = new String[]{"Partner", "Friends",};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        MateOrDate.setAdapter(adapter);

        //declare the first spinner and add dropdown options
        HowManyPeople = findViewById(R.id.HowMany);
        String[] words = new String[]{"2", "3", "4", "5", "6+"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, words);
        HowManyPeople.setAdapter(adapter1);

        //Add a listener that will get the value of the Friends or date spinner, and update the UI
        MateOrDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String WhoWith = MateOrDate.getSelectedItem().toString();

                //If the option for the date or friends spinner is date, then add a button to go to the dates page. Else ask how many people will be there
                if(WhoWith.equals("Partner")) {

                    PartnerDate.setVisibility(View.VISIBLE);
                    HowManyFriendstxt.setVisibility(View.INVISIBLE);
                    HowManyPeople.setVisibility(View.INVISIBLE);
                    FriendsDate.setVisibility((View.INVISIBLE));
                    HistoryBtn.setVisibility(View.VISIBLE);
                }

                else if(WhoWith.equals("Friends")){
                    //Toast.makeText(getApplicationContext(),"FTheSystem", Toast.LENGTH_SHORT).show();
                    PartnerDate.setVisibility(View.INVISIBLE);
                    HowManyFriendstxt.setVisibility(View.VISIBLE);
                    HowManyPeople.setVisibility(View.VISIBLE);
                    FriendsDate.setVisibility((View.VISIBLE));
                    HistoryBtn.setVisibility(View.GONE);

                }
            }

            //Necessary to make listener work - however, a value will always be selected
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });



    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.FindDateBtn:
                Intent DateIntent = new Intent(getApplicationContext(), friends.class);
                DateIntent.putExtra("Date", "Date");
                startActivity(DateIntent);
                break;

            case R.id.FriendEventBtn:
                //Call the friends activity and pass in how many people will be attending
                Intent FriendIntent = new Intent(getApplicationContext(), friends.class);
                FriendIntent.putExtra("NumberOfPeopleSend", HowManyPeople.getSelectedItem().toString());
                startActivity(FriendIntent);
                break;
            case R.id.HistoryBtn:
                Intent intent = new Intent(this, History.class);
                startActivity(intent);
                break;
        }
    }
}
