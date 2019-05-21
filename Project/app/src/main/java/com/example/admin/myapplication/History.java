package com.example.admin.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class History extends AppCompatActivity implements View.OnClickListener {

    Button HistoryBtn;
    Button ClearHistoryBtn;
    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        HistoryBtn = (Button) findViewById(R.id.HistoryBtn);
        HistoryBtn.setOnClickListener(this);

        ClearHistoryBtn = (Button) findViewById(R.id.DeleteBtn);
        ClearHistoryBtn.setOnClickListener(this);

        //Register an instance of the databseHelper class
        mDatabaseHelper = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.HistoryBtn:
                //This intent will call a class that will display a listview with the contents of the database
                Intent intent = new Intent(this, ListDataActivity.class);
                startActivity(intent);
                break;

            case R.id.DeleteBtn:
                //Calls the method to delete the database
                mDatabaseHelper.deleteTable();
                Toast.makeText(this,"Database deleted!", Toast.LENGTH_SHORT).show();

                break;
        }
    }


}
