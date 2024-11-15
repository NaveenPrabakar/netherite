package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.File;

public class MegaMainClass extends AppCompatActivity{


    private final String URL_STRING_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/system";
    public String username = "takulibruh";
    public String email = "takuli@iastate.edu";
    public String password = "admin123!";
    public String fileSystem = "{\"root\": [] }";
    public String path = "{\"path\": [\"root\"]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFileSystem(email, password);

    }

    public void getFileSystem(String email, String password){
        Uri.Builder builder = Uri.parse(URL_STRING_REQ).buildUpon();
        builder.appendQueryParameter("email", email);
        builder.appendQueryParameter("password", password);
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("File System from Server", response);
                        fileSystem = response;
                        Log.d("Globals", fileSystem);
                        Log.d("Globals", path);
                        Log.d("Globals", email);
                        Log.d("Globals", password);
                        Log.d("Globals", username);
                        Intent i = new Intent(MegaMainClass.this, filesActivity.class);
                        startActivity(i);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}