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

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;
    private Button loginButt;
    private Button signupButt;
    private Button FileView;
    private TextView emailDisplay;
    private TextView passwordDisplay;
    private Button makeFile;
    private Button settingsButt;
    // the whole ass system full of paths
    private String fileSystem = "{\"root\": [] }";;
    private final String URL_STRING_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/system";
    private String username = "-1";
    private String email = "-1";
    private String password = "-1";

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