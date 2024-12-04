package com.example.androidexample.FileView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.androidexample.Editor.TextActivity;
import com.example.androidexample.Gallery.PhotoGalleryActivity;
import com.example.androidexample.LoginActivity;
import com.example.androidexample.R;
import com.example.androidexample.Settings.SettingsActivity;
import com.example.androidexample.SignupActivity;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.VolleySingleton;
import com.example.androidexample.WebSockets.WebSocketListener;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements WebSocketListener {

    private TextView messageText;
    private Button loginButt;
    private Button signupButt;
    private Button FileView;
    private Button OCRButt;
    private Button galleryButt;
    private TextView emailDisplay;
    private TextView passwordDisplay;
    private Button makeFile;
    private Button settingsButt;
    // the whole ass system full of paths
    private String fileSystem = "{\"root\": [] }";
    private final String URL_FILESYSTEM_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/system";
    private String username = "takulibruh";
    private String email = "takuli@iastate.edu";
    private String password = "admin123!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */

        emailDisplay = findViewById(R.id.emailDisplay);
        passwordDisplay = findViewById(R.id.passwordDisplay);
        FileView = findViewById(R.id.FileView);

        galleryButt = findViewById(R.id.galleryButt);

//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();

//        if(extras != null){
//            email = extras.getString("EMAIL");
//            username = extras.getString("USERNAME");
//            password = extras.getString("PASSWORD");
//            emailDisplay.setText(email);
//            passwordDisplay.setText(password);
//        }
        email = UserPreferences.getEmail(this);
        username = UserPreferences.getUsername(this);
        password = UserPreferences.getPassword(this);
        emailDisplay.setText(email);
        passwordDisplay.setText(password);

        FileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getFileSystem(email, password);

            }
        });

        loginButt = findViewById(R.id.loginButt);
        loginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        makeFile = findViewById(R.id.makeFile);
        makeFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, TextActivity.class);
                i.putExtra("FILESYSTEM", fileSystem);
                i.putExtra("PATH", "{\"path\": []}");
                i.putExtra("CONTENT", "");
                i.putExtra("EMAIL", email);
                i.putExtra("PASSWORD", password);
                startActivity(i);
            }
        });

        signupButt = findViewById(R.id.signupButt);
        signupButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        settingsButt = findViewById(R.id.settingsButt);
        settingsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        OCRButt = findViewById(R.id.OCRButt);
        OCRButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, OCRActivity.class);
                i.putExtra("FILESYSTEM", fileSystem);
                i.putExtra("PATH", "{\"path\": []}");
                i.putExtra("EMAIL", email);
                i.putExtra("PASSWORD", password);
                startActivity(i);
            }
        });

        galleryButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(MainActivity.this, PhotoGalleryActivity.class);
                i.putExtra("EMAIL", email);
                i.putExtra("USERNAME", username);
                i.putExtra("PASSWORD", password);
                startActivity(i);
            }
        });
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
                        fileSystem = response;
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

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onWebSocketMessage(String message) {

    }

    @Override
    public void onWebSocketJsonMessage(JSONObject Jsonmessage)
    {

    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onWebSocketError(Exception ex) {

    }
}