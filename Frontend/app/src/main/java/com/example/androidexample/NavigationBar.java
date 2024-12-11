package com.example.androidexample;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.androidexample.Editor.TextActivity;
import com.example.androidexample.FileView.MainActivity;
import com.example.androidexample.FileView.OCRActivity;
import com.example.androidexample.Gallery.PhotoGalleryActivity;
import com.example.androidexample.Settings.SettingsActivity;

public class NavigationBar {

    private final Activity activity;

    public NavigationBar(Activity activity)
    {
        this.activity = activity;
    }

    public void addNavigationBar() {
        // Root layout of the activity
        ViewGroup rootLayout = (ViewGroup) activity.findViewById(android.R.id.content);

        // Inflate the navigation bar layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        View navBar = inflater.inflate(R.layout.navigation_bar, null);

        // Set layout params to position the navigation bar at the bottom
        FrameLayout.LayoutParams navBarParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        navBarParams.gravity = Gravity.BOTTOM;
        navBarParams.bottomMargin = 30; // Adjust as needed
        navBarParams.topMargin = 20; // Adjust as needed
        navBarParams.leftMargin = 25; // Adjust as needed
        navBarParams.rightMargin = 25; // Adjust as needed

        // Add the navigation bar to the root layout
        rootLayout.addView(navBar, navBarParams);

        // Set up button listeners
        setUpNavigationButtonListeners((LinearLayout) navBar.findViewById(R.id.navbarMAINMAINMAIN));
    }


    private void setUpNavigationButtonListeners(LinearLayout navBarLayout) {
        // Find buttons by their IDs in the inflated nav_bar_layout
        ImageButton ocrButton = navBarLayout.findViewById(R.id.NAVocrButton);
        ImageButton homeButton = navBarLayout.findViewById(R.id.NAVhomeButton);
        ImageButton editButton = navBarLayout.findViewById(R.id.NAVeditButton);
        ImageButton galleryNavButt = navBarLayout.findViewById(R.id.NAVgalleryNavButt);
        ImageButton settingsButt = navBarLayout.findViewById(R.id.NAVsettingsButt);

        // Set click listeners for navigation
        ocrButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, OCRActivity.class);
            intent.putExtra("SOURCE", "files");
            activity.startActivity(intent);
        });

        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        });

        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TextActivity.class);
            activity.startActivity(intent);
        });

        galleryNavButt.setOnClickListener(view -> {
            Intent i = new Intent(activity, PhotoGalleryActivity.class);
            activity.startActivity(i);
        });

        settingsButt.setOnClickListener(view ->{
            Intent i = new Intent(activity, SettingsActivity.class);
            activity.startActivity(i);
        });
    }



    private void addNavigationButtons(LinearLayout navBarLayout) {
        // Create and add buttons
        ImageButton ocrButton = createNavButton(R.drawable.ic_camera,  "OCR");
        ImageButton homeButton = createNavButton(R.drawable.home, "Home");
        ImageButton editButton = createNavButton(R.drawable.navbar_create_note, "Edit");

        //Set Button click listeners
        ocrButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, OCRActivity.class);
            activity.startActivity(intent);
        });

        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        });

        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TextActivity.class);
            activity.startActivity(intent);
        });

        // Add buttons to the nav bar layout
        navBarLayout.addView(ocrButton);
        navBarLayout.addView(homeButton);
        navBarLayout.addView(editButton);
    }

    /**
     * Helper function to create individual navigation buttons.
     *
     * @param iconResId          The drawable resource ID for the icon.
     * @param contentDescription A description for accessibility.
     * @return The created ImageButton.
     */
    private ImageButton createNavButton(int iconResId, String contentDescription) {
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
