package com.example.androidexample.FileView;

import androidx.appcompat.app.AppCompatActivity;

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

        addNavigationBar(this, R.layout.activity_main);

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

    /**
     * Adds a navigation bar at the bottom of the activity dynamically.
     * Call this in the onCreate() method of any activity to include a navigation bar.
     *
     * @param activity    The current activity where the navigation bar is to be added.
     * @param layoutResId The layout resource ID of the main content layout. The root layout of the activity (should be a FrameLayout or similar).
     */
    public static void addNavigationBar(Activity activity, int layoutResId) {
        // Inflate the provided layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        View mainContent = inflater.inflate(layoutResId, null);

        // Create a FrameLayout as the root container
        FrameLayout rootLayout = new FrameLayout(activity);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Add the main content to the root layout
        rootLayout.addView(mainContent);

        // Create the navigation bar
        LinearLayout navBarLayout = new LinearLayout(activity);
        navBarLayout.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams navBarParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        navBarParams.gravity = Gravity.BOTTOM; // Align to bottom
        navBarLayout.setLayoutParams(navBarParams);
        navBarLayout.setPadding(8, 8, 8, 8);
        navBarLayout.setBackgroundColor(activity.getResources().getColor(android.R.color.white));
        navBarLayout.setElevation(4); // Shadow/elevation for the nav bar
        navBarLayout.setGravity(Gravity.CENTER);

        // Add navigation buttons
        ImageButton micButton = createNavButton(activity, R.drawable.mic, "Mic");
        ImageButton homeButton = createNavButton(activity, R.drawable.home, "Home");
        ImageButton editButton = createNavButton(activity, R.drawable.navbar_create_note, "Edit");

        navBarLayout.addView(micButton);
        navBarLayout.addView(homeButton);
        navBarLayout.addView(editButton);

        // Add the nav bar to the root layout
        rootLayout.addView(navBarLayout);

        // Set the root layout as the content view
        activity.setContentView(rootLayout);
    }


    /**
     * Helper function to create individual navigation buttons.
     *
     * @param activity           The current activity context.
     * @param iconResId          The drawable resource ID for the icon.
     * @param contentDescription A description for accessibility.
     * @return The created ImageButton.
     */
    private static ImageButton createNavButton(Activity activity, int iconResId, String contentDescription) {
        ImageButton navButton = new ImageButton(activity);
        navButton.setLayoutParams(new LinearLayout.LayoutParams(
                0, // Equal spacing
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1 // Weight for equal distribution
        ));
        navButton.setImageResource(iconResId);
        //navButton.setBackgroundResource(android.R.attr.selectableItemBackgroundBorderless); // Touch feedback
        navButton.setContentDescription(contentDescription);
        navButton.setPadding(8, 8, 8, 8); // Add padding for spacing
        navButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Adjust scaling
        return navButton;
    }

}