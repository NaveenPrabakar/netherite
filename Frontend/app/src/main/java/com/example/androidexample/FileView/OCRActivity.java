package com.example.androidexample.FileView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.androidexample.Editor.TextActivity;
import com.example.androidexample.NavigationBar;
import com.example.androidexample.R;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.MultipartRequest;
import com.example.androidexample.Volleys.VolleySingleton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class OCRActivity extends AppCompatActivity {
    Button selectBtn;
    Button uploadBtn;
    ImageView mImageView;
    Uri selectiedUri;
    String email;
    String language = "english";
    private String username;
    private Uri photoUri;
    private ActivityResultLauncher<Uri> takePicture;
    private String fileSystem;
    private String filePath;
    private String password;
    private String fileKey;
    private String source;
    // replace this with the actual address
    // 10.0.2.2 to be used for localhost if running springboot on the same host
    private static String UPLOAD_URL = "http://coms-3090-068.class.las.iastate.edu:8080/extractText";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private ActivityResultLauncher<String> mGetContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        email = UserPreferences.getEmail(this);
        username = UserPreferences.getUsername(this);
        password = UserPreferences.getPassword(this);
        filePath = UserPreferences.getFilePath(this);
        fileSystem = UserPreferences.getFileSystem(this);

        mImageView = findViewById(R.id.imageSelView);
        selectBtn = findViewById(R.id.selectBtn);

//        NavigationBar navigationBar = new NavigationBar(this);
//        navigationBar.addNavigationBar();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null){
            source = extras.getString("SOURCE", "files");
            fileKey = extras.getString("FILEKEY", "");
        }

        takePicture = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                // Photo was successfully captured
                if (photoUri != null) {
                    mImageView.setImageURI(photoUri);
                    selectiedUri = photoUri; // Set the captured image as the selected one
                }
            } else {
                Log.e("Camera", "Photo capture failed");
            }
        });

        Button captureBtn = findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(v -> checkAndRequestCameraPermission());


        // select image from gallery
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    // Handle the returned Uri
                    if (uri != null) {
                        selectiedUri = uri;
                        ImageView imageView = findViewById(R.id.imageSelView);
                        imageView.setImageURI(uri);
                    }
                });

        selectBtn.setOnClickListener(v -> mGetContent.launch("image/*"));
        uploadBtn = findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(v -> uploadImage());
    }


    /**
     * Uploads an image to a remote server using a multipart Volley request.
     *
     * This method creates and executes a multipart request using the Volley library to upload
     * an image to a predefined server endpoint. The image data is sent as a byte array and the
     * request is configured to handle multipart/form-data content type. The server is expected
     * to accept the image with a specific key ("image") in the request.
     *
     */
    private void uploadImage(){

        byte[] imageData = convertImageUriToBytes(selectiedUri);
        MultipartRequest multipartRequest = new MultipartRequest(
                Request.Method.POST,
                UPLOAD_URL + "/" + email + "/" + language,
                imageData, // Sussy ...
                response -> {
                    // Handle response
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    Log.d("Upload", "Response: " + response);
                    if(source.equals("text")){
                        moveToTextActivity(response);
                    }else{
                        moveToFilesActivity(response);
                    }
                },
                error -> {
                    // Handle error
                    //Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
                    Log.e("Upload", "Error: " + error.getMessage());
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }

    private void moveToFilesActivity(String ImageText){
        Intent i = new Intent(OCRActivity.this, filesActivity.class);
        i.putExtra("IMAGETEXT", ImageText);
        startActivity(i);
    }

    private void moveToTextActivity(String ImageText){
        Intent i = new Intent(OCRActivity.this, TextActivity.class);
        i.putExtra("IMAGETEXT", ImageText);
        i.putExtra("FILEKEY", fileKey);
        startActivity(i);
    }

    /**
     * Converts the given image URI to a byte array.
     *
     * This method takes a URI pointing to an image and converts it into a byte array. The conversion
     * involves opening an InputStream from the content resolver using the provided URI, and then
     * reading the content into a byte array. This byte array represents the binary data of the image,
     * which can be used for various purposes such as uploading the image to a server.
     *
     * @param imageUri The URI of the image to be converted. This should be a content URI that points
     *                 to an image resource accessible through the content resolver.
     * @return A byte array representing the image data, or null if the conversion fails.
     * @throws IOException If an I/O error occurs while reading from the InputStream.
     */
    private byte[] convertImageUriToBytes(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "photo_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            openCamera(); // Call the method to open the camera
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(); // Permission granted, open the camera
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        photoUri = createImageUri();
        if (photoUri != null) {
            takePicture.launch(photoUri);
        } else {
            Log.e("Camera", "Failed to create image URI");
        }
    }
}
