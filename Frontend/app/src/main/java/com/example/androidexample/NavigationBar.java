package com.example.androidexample;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.androidexample.Editor.TextActivity;
import com.example.androidexample.FileView.MainActivity;

public class NavigationBar {

    private final Activity activity;

    public NavigationBar(Activity activity)
    {
        this.activity = activity;
    }

    public void addNavigationBar(int layoutResId) {
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
        addNavigationButtons(navBarLayout);

        // Add the nav bar to the root layout
        rootLayout.addView(navBarLayout);

        // Set the root layout as the content view
        activity.setContentView(rootLayout);
    }

    private void addNavigationButtons(LinearLayout navBarLayout) {
        // Create and add buttons
        ImageButton micButton = createNavButton(R.drawable.mic, "Mic");
        ImageButton homeButton = createNavButton(R.drawable.home, "Home");
        ImageButton editButton = createNavButton(R.drawable.navbar_create_note, "Edit");

        // Set button click listeners
        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        });

        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, TextActivity.class);
            activity.startActivity(intent);
        });

        // Add buttons to the nav bar layout
        navBarLayout.addView(micButton);
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
