package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhotoGalleryActivity extends AppCompatActivity {
    private String email;
    private final String getPhotoList = "http://coms-3090-068.class.las.iastate.edu:8080/getImageNamesByUser/nvnprabakar@gmail.com";
    private Button backButt;
    private List<String> photos;
    private RecyclerView galleryView;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photogallery);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null)
        {
            if (extras.getString("EMAIL") != null)
            {
                email = extras.getString("EMAIL");
            }
        }

        backButt = findViewById(R.id.backButt);
        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(PhotoGalleryActivity.this, MainActivity.class);
                i.putExtra("EMAIL", email);
                startActivity(i);
            }
        });

        galleryView = findViewById(R.id.recyclerGalleryView);
        galleryView.setLayoutManager(new LinearLayoutManager(this));


        // How to get that same list of photo file names from getPhotoList?
        getPhotos();

    }

    private void getPhotos()
    {
        /*
        * Creates a new HTTP client so that I can get the list of photo names from 'getPhotoList'.
         */
        OkHttpClient client = new OkHttpClient();

        /*
        * It's a request to access the client url.
         */
        Request request = new Request.Builder()
                .url(getPhotoList).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(PhotoGalleryActivity.this, "Failed Photo Fetch", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null)
                {
                    String listOfNames = response.body().string();
                    Log.d("List of Names", listOfNames);
                    Gson gson = new Gson();

                    // Reflection Library
                    Type classType = new TypeToken<List<String>>(){}.getType();
                    photos = gson.fromJson(listOfNames, classType);

                    // Populate the gallery with the photos
                    new Handler(Looper.getMainLooper()).post(() -> {
                        GalleryPopulator gallery = new GalleryPopulator(photos, PhotoGalleryActivity.this);
                        galleryView.setAdapter(gallery);
                    });
                }
            }
        });
    }
}
