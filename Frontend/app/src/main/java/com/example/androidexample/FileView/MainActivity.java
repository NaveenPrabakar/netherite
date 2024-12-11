package com.example.androidexample.FileView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.androidexample.Editor.TextActivity;
import com.example.androidexample.Gallery.PhotoGalleryActivity;
import com.example.androidexample.LoginActivity;
import com.example.androidexample.NavigationBar;
import com.example.androidexample.R;
import com.example.androidexample.Settings.SettingsActivity;
import com.example.androidexample.SignupActivity;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.VolleySingleton;
import com.example.androidexample.WebSockets.WebSocketListener;
import com.example.androidexample.WebSockets.WebSocketManager;
import com.example.androidexample.WebSockets.WebSocketManager3;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{
    // the whole ass system full of paths
    private final String URL_FILESYSTEM_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/system";
    private final String URL_FSWS = "ws://coms-3090-068.class.las.iastate.edu:8080/Mail";

    private String username;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* initialize UI elements */
        username = UserPreferences.getUsername(this);
        email = UserPreferences.getEmail(this);
        password = UserPreferences.getPassword(this);

        WebSocketManager3.getInstance().connectWebSocket(URL_FSWS);
        getFileSystem(email, password);
    }

    public void getFileSystem(String email, String password){
        Uri.Builder builder = Uri.parse(URL_FILESYSTEM_REQ).buildUpon();
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
                        UserPreferences.saveUserDetails(MainActivity.this, username, email, password, response, "{\"path\": [\"root\"]}");
                        Intent i = new Intent(MainActivity.this, filesActivity.class);
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