package com.vitap.wifi_locator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.VoiceInteractor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.URLEncoder;

public class AddLocation extends AppCompatActivity {
    String bssid ;
    EditText location_inp;
    TextView status ;
    RequestQueue queue ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        location_inp = findViewById(R.id.editText);
        status = findViewById(R.id.status);
        queue = Volley.newRequestQueue(this);
        bssid = getIntent().getExtras().getString("bssid","bruh");
    }

    public void onPress(View view){
        String inp = location_inp.getText().toString();
        if(inp.trim()!=""){
            sendDBEntry(inp);
        }
    }

    public void sendDBEntry(String location){
        String Command = "http://3.136.13.45:8000/?query="+ URLEncoder.encode(String.format("INSERT INTO bssidlocation values(\'%s\',\'%s\')",bssid,location));
        status.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Command,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        status.setText("Successfulyl updated");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        status.setText("Unable to communicate to Server");
                    }
                }
        );
        queue.add(request);
    }
}
