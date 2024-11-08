package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

/*
* The Photo View activity will display a single photo.
 */
public class PhotoViewActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button goBackButt;
    private String filename;

    private String username;
    private String password;
    private String email;
    private final String getPhotoImageURL = "http://coms-3090-068.class.las.iastate.edu:8080/getImage/"; // and then + filename

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);

        goBackButt = findViewById(R.id.backButt);
        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null)
        {
            if (extras.getString("FILENAME") != null)
            {
                filename = extras.getString("FILENAME");
                Log.d("FILENAME", filename);
            }
            if (extras.getString("EMAIL") != null)
            {
                email = extras.getString("EMAIL");
            }
            if (extras.getString("USERNAME") != null)
            {
                username = extras.getString("USERNAME");
            }
            if (extras.getString("PASSWORD") != null)
            {
                password = extras.getString("PASSWORD");
            }
        }
        // Needs to wait for image to fully load though.
        loadImage();

        goBackButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(PhotoViewActivity.this, PhotoGalleryActivity.class);
                i.putExtra("EMAIL", email);
                i.putExtra("USERNAME", username);
                i.putExtra("PASSWORD", password);
                startActivity(i);
            }
        });
    }

    private void loadImage()
    {
        ImageRequest imageRequest = new ImageRequest(
                getPhotoImageURL + filename,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Display the image in the ImageView
                        imageView.setImageBitmap(response);
                    }
                },
                0, // Width, set to 0 to get the original width
                0, // Height, set to 0 to get the original height
                ImageView.ScaleType.FIT_XY, // ScaleType
                Bitmap.Config.RGB_565, // Bitmap config

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors here
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);
    }
}
