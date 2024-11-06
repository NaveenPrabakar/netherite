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

import java.io.File;

public class MainActivity extends AppCompatActivity implements WebSocketListener {

    private TextView messageText;
    private Button loginButt;
    private Button signupButt;
    private Button FileView;
    private Button OCRButt;
    private TextView emailDisplay;
    private TextView passwordDisplay;
    private Button makeFile;
    private Button settingsButt;
    // the whole ass system full of paths
    private String fileSystem = "{\"root\": [] }";;
    private final String URL_STRING_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/system";
    private String username = "takulibruh";
    private String email = "takuli@iastate.edu";
    private String password = "admin123!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg);      // link to message textview in the Main activity XML
        messageText.setText("Hello World");

        emailDisplay = findViewById(R.id.usernameDisplay);
        passwordDisplay = findViewById(R.id.passwordDisplay);
        FileView = findViewById(R.id.FileView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String serverUrl = "ws://10.0.2.2:8080/chat/1/" + username;
        String aiURL = "ws://10.0.2.2:8080/chat/" + username;
        WebSocketManager.getInstance().connectWebSocket(serverUrl);

        if(extras != null){
            email = extras.getString("EMAIL");
            //username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
            emailDisplay.setText(email);
            passwordDisplay.setText(password);

        }

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
                        Intent i = new Intent(MainActivity.this, filesActivity.class);
                        i.putExtra("FILESYSTEM", fileSystem);
                        i.putExtra("EMAIL", email);
                        i.putExtra("PASSWORD", password);
                        i.putExtra("USERNAME", username);
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
    public void onWebSocketClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onWebSocketError(Exception ex) {

    }
}