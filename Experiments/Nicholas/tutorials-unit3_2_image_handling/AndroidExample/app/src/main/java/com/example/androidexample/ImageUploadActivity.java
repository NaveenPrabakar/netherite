package com.example.androidexample;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUploadActivity extends AppCompatActivity {

    Button selectBtn, uploadBtn;
    ImageView mImageView;
    Uri selectedUri; // Fixed typo from selectiedUri

    // Replace this with the actual address.
    private static final String UPLOAD_URL = "http://10.0.2.2:8080/images";

    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        mImageView = findViewById(R.id.imageSelView);
        selectBtn = findViewById(R.id.selectBtn);
        uploadBtn = findViewById(R.id.uploadBtn);

        // Initialize the content picker for images
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedUri = uri;
                mImageView.setImageURI(uri);
                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            }
        });

        selectBtn.setOnClickListener(v -> mGetContent.launch("image/*"));

        uploadBtn.setOnClickListener(v -> {
            if (selectedUri != null) {
                uploadImage();
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Uploads an image to the remote server using a multipart Volley request.
     */
    private void uploadImage() {
        byte[] imageData = convertImageUriToBytes(selectedUri);

        if (imageData == null) {
            Toast.makeText(this, "Error reading image data", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();

        MultipartRequest multipartRequest = new MultipartRequest(
                Request.Method.POST,
                UPLOAD_URL,
                imageData,
                response -> {
                    Toast.makeText(getApplicationContext(), "Upload Successful!", Toast.LENGTH_LONG).show();
                    Log.d("Upload", "Response: " + response);
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Upload Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Upload", "Error: " + error.getMessage());
                }
        );

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }

    /**
     * Converts the given image URI to a byte array.
     * Ensures proper closing of InputStream resources.
     */
    private byte[] convertImageUriToBytes(Uri imageUri) {
        try (InputStream inputStream = getContentResolver().openInputStream(imageUri);
             ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
            Log.e("ImageUpload", "Error: ", e);
            return null;
        }
    }
}
