package com.vitap.wifi_locator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.URLEncoder;
import java.util.List;

public class UpdateLocation extends AppCompatActivity {
    EditText regnoField ;
    TextView status ;
    WifiManager wifiManager ;
    RequestQueue queue;
    String bssid = "default";
    Button jump ,update;
    //studentbssid
    //INSERT INTO studentbssid values(

    void onSuccess(){
        //TODO:Add success actions here
        status.setVisibility(View.VISIBLE);
        status.setText("Succeffully Updated");
    }

    void onFail(){
        //TODO:Add fail actions here
        status.setVisibility(View.VISIBLE);
        status.setText("Unable to communicate to server");
    }

    void addEntry(String regno , final String bssid){

        String firstCommand = "http://3.136.13.45:8000/?query=" + URLEncoder.encode(String.format("INSERT INTO studentbssid values(\"%s\",\"%s\")",regno,bssid));
        final String secondCommand = "http://3.136.13.45:8000/?query=" +URLEncoder.encode(String.format("UPDATE studentbssid SET bssid = \"%s\" WHERE regno = \"regno\"",bssid));
        StringRequest request1 = new StringRequest(
                Request.Method.GET,
                firstCommand,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("[]")){ //primary key duplicate error, student id already exists in table
                            StringRequest request2 = new StringRequest(
                                    Request.Method.GET,
                                    secondCommand, // performing update operation since record is already present
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            onSuccess();
                                            checkBSSID(bssid);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            onFail();
                                        }
                                    }
                            );
                            queue.add(request2);
                        }
                        else{
                            onSuccess();
                            checkBSSID(bssid);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onFail();
                    }
                }

        );
        queue.add(request1);
    }

    void checkBSSID(String bssid){
        String command = "http://3.136.13.45:8000/?query="+URLEncoder.encode(String.format("SELECT location from bssidlocation where bssid=\"%s\" ",bssid));
        StringRequest request = new StringRequest(
                Request.Method.GET,
                command,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.equals("[]")) {
                            String location = response.substring(3,response.length()-3);
                            status.setText("Updation success , you are present at "+location);
                        }
                        else{
                            status.setText("Sorry your location is not available in db , please add it!!");
                            jump.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        queue.add(request);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location);
        queue = Volley.newRequestQueue(this);
        regnoField = findViewById(R.id.editText);
        status = findViewById(R.id.status);
        update = findViewById(R.id.button3);
        update.setEnabled(false);
        jump = findViewById(R.id.add_entry);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true); // Turns on Wifi

        status.setVisibility(View.VISIBLE);
        status.setText("Please wait scanning for networks");
        //Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(myIntent);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    List<ScanResult> results = wifiManager.getScanResults();
                    if(results.size()!=0){
                        bssid = results.get(0).BSSID;
                        status.setText("nearby bssid identified , now you can update");
                        update.setEnabled(true);
                    }
                    else{
                        status.setText("no nearby wifi network identified , you cant update now");
                        update.setEnabled(false);
                    }
                }
            },
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        );
        if(wifiManager.startScan())
            System.out.println("-----------------Scanning");
        else {
            switch(wifiManager.getWifiState()){
                case WifiManager.WIFI_STATE_DISABLING:
                    Toast.makeText(this, "-------Disabling", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Toast.makeText(this,"------------Disabled", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    Toast.makeText(this, "--------------Enabling", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Toast.makeText(this, "----------------Enabled", Toast.LENGTH_LONG).show();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    Toast.makeText(this,"-------------Unknown", Toast.LENGTH_LONG).show();
                    break;

            }
        }
        if(wifiManager.isScanAlwaysAvailable()){
            System.out.println("--------------------yes");
        }

        /*
        List<ScanResult> availableNets = wifiManager.getScanResults();
        if(availableNets.size()==0){
            status.setVisibility(View.VISIBLE);
            status.setTextColor(Color.RED);
            status.setText("Sorry No Networks Found");
        }
        else{
            bssid = availableNets.get(0).BSSID;
        }*/
    }

    public void updateData(View view){
        if(!regnoField.getText().toString().trim().equals("")){
            addEntry(regnoField.getText().toString().trim(),bssid);
        }
    }

    public void jumper(View view){
        Intent jump = new Intent(this,AddLocation.class);
        jump.putExtra("bssid",bssid);
        System.out.println("okkkkkkkkkkkkkkkkkkk"+bssid.length());
        startActivity(jump);
    }

}
