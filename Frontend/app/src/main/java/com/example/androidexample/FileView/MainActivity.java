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

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{
    // the whole ass system full of paths
    private final String URL_FILESYSTEM_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/system";
    private final String URL_FSWS = "ws://coms-3090-068.class.las.iastate.edu:8080/Mail";

    private String username = "takulibruh";
    private String email = "takuli@iastate.edu";
    private String password = "admin123!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* initialize UI elements */
        username = UserPreferences.getUsername(this);
        email = UserPreferences.getEmail(this);
        password = UserPreferences.getPassword(this);

        NavigationBar navigationBar = new NavigationBar(this);
        navigationBar.addNavigationBar();
        WebSocketManager.getInstance().connectWebSocket(URL_FSWS);

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

    /**
     * Adds a navigation bar at the bottom of the activity dynamically.
     * Call this in the onCreate() method of any activity to include a navigation bar.
     *
     * @param activity    The current activity where the navigation bar is to be added.
     * @param layoutResId The layout resource ID of the main content layout. The root layout of the activity (should be a FrameLayout or similar).
     */
    public void addNavigationBar(Activity activity, int layoutResId) {
        // Inflate the provided layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        View mainContent = inflater.inflate(layoutResId, null);

        // Create a FrameLayout as the root container
        CoordinatorLayout rootLayout = new CoordinatorLayout(activity);
        rootLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // Add the main content to the root layout
        CoordinatorLayout.LayoutParams contentParams = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
        );
        contentParams.bottomMargin = (int) activity.getResources().getDimension(R.dimen.nav_bar_height); // Reserve space for nav bar
        rootLayout.addView(mainContent, contentParams);

        // Create the navigation bar
        LinearLayout navBarLayout = new LinearLayout(activity);
        navBarLayout.setOrientation(LinearLayout.HORIZONTAL);
        CoordinatorLayout.LayoutParams navBarParams = new CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                (int) activity.getResources().getDimension(R.dimen.nav_bar_height)
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
        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navBarLayout.addView(editButton);
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TextActivity.class);
            startActivity(intent);
        });

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