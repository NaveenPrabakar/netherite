package com.example.androidexample;

import static com.android.volley.Request.Method.POST;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class TextActivity extends AppCompatActivity {
    private final String URL_STRING_REQ = "http://10.26.47.170:8080/files/upload";
    private Button back2main;
    private Button saveButt;
    private EditText mainText;
    private EditText fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        // link to Login activity XML
        mainText = findViewById(R.id.editTextTextMultiLine4);
        fileName = findViewById(R.id.fileName);


        back2main = findViewById(R.id.back2main);
        back2main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(TextActivity.this, MainActivity.class);
                startActivity(intent);  // go to SignupActivity
            }
        });


        saveButt = findViewById(R.id.saveButt);
        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileName.getText().toString().isEmpty()){
                    System.out.println("file name is empty");
                }else{
                    writeToFile();
                    listFiles();
                    sendFile();
                    readFromFile(fileName.getText().toString()+".md");
                }

            }});
    }

    public void writeToFile() {
        File path = getApplicationContext().getFilesDir();

        try {
            File file = new File(path,fileName.getText().toString()+".md");
            // Create the file
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(mainText.getText().toString().getBytes());
            writer.close();
            Toast.makeText(getApplicationContext(), "File saved", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readFromFile(String fileKey) {
        String fileName = fileKey;
        try  {
            FileInputStream fis = openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String fileContents = stringBuilder.toString();
            System.out.println("Contents of file: "+ fileName + ": " + fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] listFiles(){
        File directory = getApplicationContext().getFilesDir();  // Internal storage directory
        File[] files = directory.listFiles();  // List all files in the directory
        String[] fileKeys = new String[files.length];
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                fileKeys[i] = files[i].getName();
                System.out.println("File: " + files[i].getName());
            }
        } else {
            System.out.println("no files found");;
        }
        return fileKeys;
    }

    public void sendFile() {
        File path = getApplicationContext().getFilesDir();
        File file = new File(path, fileName.getText().toString() + ".md");

        if (!file.exists()) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = URL_STRING_REQ;  // Replace with your server's URL

        HashMap<String, String> params = new HashMap<>();
        params.put("description", "This is a file upload");

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(POST, url, file,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(TextActivity.this, "File sent successfully!", Toast.LENGTH_SHORT).show();
                        Log.d("VolleyResponse", new String(response.data));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TextActivity.this, "File upload failed", Toast.LENGTH_SHORT).show();
                        Log.e("VolleyError", error.toString());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(multipartRequest);
    }

}
