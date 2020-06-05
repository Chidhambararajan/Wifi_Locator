package com.vitap.wifi_locator;

import androidx.appcompat.app.AppCompatActivity;

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

public class FindMyFriend extends AppCompatActivity {

    EditText regNoInp;
    TextView status ;
    RequestQueue queue ;

    public void sendCommand(View view){
        status.setText("Please Wait Querying Server");
        String regNo = regNoInp.getText().toString();
        String Command = "http://3.136.13.45:8000/?query="+
                URLEncoder.encode(String.format(
                        "SELECT location FROM bssidlocation WHERE bssid in (SELECT bssid FROM studentbssid where regno = \'%s\')"
                        ,regNo)
                );
        status.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(
                Request.Method.GET,
                Command,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        status.setText(response.substring(3,response.length()-3));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_friend);
        queue = Volley.newRequestQueue(this);
        regNoInp = findViewById(R.id.editText);
        status = findViewById(R.id.status);
    }
}
