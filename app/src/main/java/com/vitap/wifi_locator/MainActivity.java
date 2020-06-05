package com.vitap.wifi_locator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    //Button  b1 , b2 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //b1 = findViewById(R.id.update_loc);
        //b2 = findViewById(R.id.find_friend);
    }

    public void gotoUpdate_loc(View view){
        Intent jump = new Intent(this,UpdateLocation.class);
        startActivity(jump);
    }

    public void goToFindFriend(View view){
        startActivity(new Intent(this,FindMyFriend.class));
    }
}