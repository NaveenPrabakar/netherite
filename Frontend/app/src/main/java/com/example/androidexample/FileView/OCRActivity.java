package com.example.androidexample.FileView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.example.androidexample.Editor.TextActivity;
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
    String email = "takuli@iastate.edu";
    String language = "english";
    private String username;

    private String fileSystem;
    private String filePath;
    private String password;
    // replace this with the actual address
    // 10.0.2.2 to be used for localhost if running springboot on the same host
    private static String UPLOAD_URL = "http://coms-3090-068.class.las.iastate.edu:8080/extractText";

    private ActivityResultLauncher<String> mGetContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
//
//        if(extras != null) {
//            try {
//                fileSystem = extras.getString("FILESYSTEM");
//                filePath = extras.getString("PATH");
//                email = extras.getString("EMAIL");
//                username = extras.getString("USERNAME");
//                password = extras.getString("PASSWORD");
//                //Log.d("EMAIL", extras.getString("EMAIL"));
//                Log.d("PASSWORD", extras.getString("PASSWORD"));
//                Log.d("FILESYSTEM", extras.getString("FILESYSTEM"));
//                Log.d("PATH", extras.getString("PATH"));
//
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }else{
//            filePath = "{\"path\": []}";
//            fileSystem = "{\"root\": [] }";
//        }

        email = UserPreferences.getEmail(this);
        username = UserPreferences.getUsername(this);
        password = UserPreferences.getPassword(this);
        filePath = UserPreferences.getFilePath(this);
        fileSystem = UserPreferences.getFileSystem(this);

        mImageView = findViewById(R.id.imageSelView);
        selectBtn = findViewById(R.id.selectBtn);

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
                    Intent i = new Intent(OCRActivity.this, TextActivity.class);
                    i.putExtra("IMAGETEXT", response);
                    i.putExtra("CONTENT", "");
                    i.putExtra("FILESYSTEM", fileSystem);
                    i.putExtra("PATH", "{\"path\": []}");
                    i.putExtra("EMAIL", email);
                    i.putExtra("PASSWORD", password);
                    i.putExtra("USERNAME", username);
                    startActivity(i);
                },
                error -> {
                    // Handle error
                    //Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG).show();
                    Log.e("Upload", "Error: " + error.getMessage());
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
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
}
