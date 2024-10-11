package com.example.androidexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class filesActivity extends AppCompatActivity {
    private final String URL_STRING_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/pull";
    private final String URL_DELETE_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/deleteFile";
    private String fileSystem =  "{\"root\": []}";
    // path is hard coded. make a path lmao. make it dynamic
    // when i click a file or a folder, it should update the path.
    private String path = "{\"path\": []}";
    private String email;
    private String password;
    private String content;
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            if (!extras.getString("FILESYSTEM").equals("User does not exist")){
                fileSystem = extras.getString("FILESYSTEM");
                Log.d("FILESYSTEM", extras.getString("FILESYSTEM"));
            }
            if (!extras.getString("EMAIL").equals("-1") && !extras.getString("PASSWORD").equals("-1")) {
                email = extras.getString("EMAIL");
                password = extras.getString("PASSWORD");
                Log.d("EMAIL", extras.getString("EMAIL"));
                Log.d("PASSWORD", extras.getString("PASSWORD"));
            }
        }

        rootLayout = findViewById(R.id.rootLayout);
        try {
            JSONObject jsonObject = new JSONObject(fileSystem);
            Log.d("JSON OBJECT", jsonObject.toString());
            JSONArray rootArray = jsonObject.getJSONArray("root");
            Log.d("JSON ARRAY", rootArray.toString());
            // Start DFS traversal
            createFolderWithFiles(rootLayout, "root", rootArray, "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createFolderWithFiles(LinearLayout parentLayout, String folderName, JSONArray filesArray, String indent) throws JSONException {
        // Create a folder TextView (which will act as a clickable item)
        TextView folderTextView = new TextView(this);
        folderTextView.setText(indent + folderName);
        folderTextView.setTextSize(18);
        folderTextView.setPadding(10, 10, 10, 10);
        folderTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        folderTextView.setClickable(true);
        parentLayout.addView(folderTextView);

        // Create a LinearLayout to hold the files and set its visibility to GONE initially
        LinearLayout fileContainer = new LinearLayout(this);
        fileContainer.setOrientation(LinearLayout.VERTICAL);
        fileContainer.setVisibility(View.GONE); // Initially hidden
        parentLayout.addView(fileContainer);

        // Iterate over the files array
        for (int i = 0; i < filesArray.length(); i++) {
            Object item = filesArray.get(i);
            if (item instanceof String) {

                // Create a horizontal LinearLayout to hold the TextView and Delete Button
                LinearLayout fileLayout = new LinearLayout(this);
                fileLayout.setOrientation(LinearLayout.HORIZONTAL);
                fileLayout.setPadding(10, 10, 10, 10); // Set padding around the layout

                // Create a TextView for each file
                TextView fileTextView = new TextView(this);
                fileTextView.setText(String.valueOf(item));
                fileTextView.setPadding(30, 10, 10, 10); // Indented padding

                // Create a Delete Button
                Button deleteButton = new Button(this);
                deleteButton.setText("Delete");
                deleteButton.setPadding(20, 10, 20, 10);

                // Set an onClickListener for the TextView to handle file click events
                fileTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("File Clicked", "File clicked: " + String.valueOf(item));
                        getFileString(String.valueOf(item));
                    }
                });

                // Set an onClickListener for the Delete Button to handle file deletion
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("File Deleted", "File deleted: " + String.valueOf(item));
                        try {

                            JSONObject jsObj = fileDeletor(new JSONObject(fileSystem), new JSONObject(path), String.valueOf(item));
                            deleteFile(String.valueOf(item), jsObj.toString());


                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                // Add the TextView and Button to the LinearLayout
                fileLayout.addView(fileTextView);
                fileLayout.addView(deleteButton);

                // Add the LinearLayout to the file container
                fileContainer.addView(fileLayout);
            } else if (item instanceof JSONObject) {
                // Recursively handle nested folders
                JSONObject nestedObject = (JSONObject) item;
                for (Iterator<String> it = nestedObject.keys(); it.hasNext(); ) {
                    String nestedKey = it.next();
                    JSONArray nestedFilesArray = nestedObject.getJSONArray(nestedKey);
                    createFolderWithFiles(fileContainer, nestedKey, nestedFilesArray, indent);
                }
            }
        }

        // Set an OnClickListener to toggle the visibility of the files
        folderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileContainer.getVisibility() == View.VISIBLE) {
                    fileContainer.setVisibility(View.GONE); // Collapse folder
//                    try {
//                        JSONObject pathObject = new JSONObject(path);
//                        JSONArray pathArray = (JSONArray) pathObject.get("path");
//                        pathArray.remove(pathArray.length() - 1);
//                        path = pathObject.toString();
//                        Log.d("PATH", path);
//                    } catch (JSONException e) { throw new RuntimeException(e); }
                } else {
                    fileContainer.setVisibility(View.VISIBLE); // Expand folder
//                    try {
//                        String folderName = folderTextView.getText().toString();
//                        JSONObject pathObject = new JSONObject(path);
//                        JSONArray pathArray = (JSONArray) pathObject.get("path");
//                        pathArray.put(folderName);
//                        path = pathObject.toString();
//                        Log.d("PATH", path);
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
                }
            }
        });
    }

    public void getFileString(String fileName){
        Uri.Builder builder = Uri.parse(URL_STRING_REQ).buildUpon();
        builder.appendQueryParameter("email", email);
        builder.appendQueryParameter("password", password);
        builder.appendQueryParameter("fileName", fileName );
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        content = response;
                        Intent intent = new Intent(filesActivity.this, TextActivity.class);
                        intent.putExtra("FILESYSTEM", fileSystem );
                        intent.putExtra("PATH", path);
                        intent.putExtra("CONTENT", content);
                        intent.putExtra("FILEKEY", fileName);
                        intent.putExtra("EMAIL", email);
                        intent.putExtra("PASSWORD", password);
                        startActivity(intent);
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

    public JSONObject fileDeletor(JSONObject fileSystem, JSONObject filePath, String fileName) throws JSONException {
        JSONArray pathArray = filePath.getJSONArray("path");
        pathArray.remove(0);
        JSONArray root = fileSystem.getJSONArray("root");

        // Traverse the file system using the provided path
        for (int i = 0; i < pathArray.length(); i++) { //loop through the path
            String key = pathArray.getString(i); //get the path key
            for (int j = 0; j < root.length(); j++) { //loop through current scope
                if (root.get(j) instanceof JSONObject && root.getJSONObject(j).has(key) ) { //if the current item is a JSON object and it contains the path key
                        root = ((JSONObject)root.get(j)).getJSONArray(key); //change scope to the new path key
                        break; //break out of the scope
                }
            }
        }
        JSONArray rootArray = (JSONArray) root;
        int index =-1;
        for(int i = 0; i < rootArray.length(); i++){
            if (rootArray.get(i) instanceof String && rootArray.get(i).equals(fileName)){
                index = i;
            }
        }
        if (index != -1){
            root.remove(index);
        }
        Log.d("File System", fileSystem.toString());
        return fileSystem;

    }

    public void deleteFile(String fileName, String newFileSystem){
        Uri.Builder builder = Uri.parse(URL_DELETE_REQ).buildUpon();
        builder.appendQueryParameter("email", email);
        builder.appendQueryParameter("fileName", fileName);
        builder.appendQueryParameter("json", newFileSystem);
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        rootLayout.removeAllViewsInLayout();
                        try {
                            JSONObject jsObj = new JSONObject(newFileSystem);
                            Log.d("JSON OBJECT", jsObj.toString());
                            fileSystem = newFileSystem;
                            createFolderWithFiles(rootLayout, "root", jsObj.getJSONArray("root"), "");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
}
