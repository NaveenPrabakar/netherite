package com.example.androidexample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private String path = "{\"path\": [\"root\"]}";
    private String email;
    private String password;
    private String username;
    private String content;
    private LinearLayout rootLayout;
    private String currentArray = "{\"root\": []}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            if (!extras.getString("FILESYSTEM").equals("User does not exist")){
                fileSystem = extras.getString("FILESYSTEM");
                currentArray = fileSystem;
                Log.d("FILESYSTEM", extras.getString("FILESYSTEM"));
            }
            if (!extras.getString("EMAIL").equals("-1") && !extras.getString("PASSWORD").equals("-1") && !extras.getString("USERNAME").equals("-1")) {
                email = extras.getString("EMAIL");
                password = extras.getString("PASSWORD");
                username = extras.getString("USERNAME");
                Log.d("EMAIL", extras.getString("EMAIL"));
                Log.d("PASSWORD", extras.getString("PASSWORD"));
            }
        }

        rootLayout = findViewById(R.id.rootLayout);
        createUI(rootLayout);
    }
    private void createUI(LinearLayout parentLayout) {
        Button newFolder = new Button(this);
        newFolder.setText("New Folder");
        newFolder.setPadding(20, 10, 20, 10);

        EditText newFolderName = new EditText(this);
        newFolderName.setHint("New Folder Name");
        newFolderName.setPadding(20, 10, 20, 10);

        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("New Folder", "New Folder clicked: " + newFolderName.getText().toString());
                try {
                    currentArray = newFolder(currentArray, fileSystem, newFolderName.getText().toString());
                    parentLayout.removeAllViewsInLayout();
                    createUI(parentLayout);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        parentLayout.addView(newFolderName);
        parentLayout.addView(newFolder);
        try{
            LinearLayout fileLayout = new LinearLayout(this);
            fileLayout.setOrientation(LinearLayout.VERTICAL);
            parentLayout.addView(fileLayout);
            JSONObject currObj = new JSONObject(currentArray);
            String key = currObj.keys().next();
            createFolderWithFiles(fileLayout, key, currObj.getJSONArray(key));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void createFolderWithFiles(LinearLayout parentLayout, String folderName, JSONArray filesArray) throws JSONException {
        // Create a LinearLayout to hold the files and set its visibility to GONE initially
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

                // Create a shareTo Button
                Button shareTo = new Button(this);
                shareTo.setText("Share to");
                shareTo.setPadding(20, 10, 20, 10);

                EditText toUser = new EditText(this);
                toUser.setHint("Username");
                toUser.setPadding(20, 10, 20, 10);

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

                shareTo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareToUser(username.toString(), toUser.getText().toString(), String.valueOf(item));
                    }
                });

                // Add the TextView and Button to the LinearLayout
                fileLayout.addView(fileTextView);
                fileLayout.addView(deleteButton);
                fileLayout.addView(shareTo);
                fileLayout.addView(toUser);

                // Add the LinearLayout to the file container
                parentLayout.addView(fileLayout);
            } else if (item instanceof JSONObject) {
                // Recursively handle nested folders
                JSONObject nestedObject = (JSONObject) item;
                for (Iterator<String> it = nestedObject.keys(); it.hasNext(); ) {
                    String nestedKey = it.next();
                    TextView folderTextView = new TextView(this);
                    folderTextView.setText(nestedKey);
                    folderTextView.setTextSize(18);
                    folderTextView.setPadding(10, 10, 10, 10);
                    folderTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    folderTextView.setClickable(true);
                    parentLayout.addView(folderTextView);

                    // Set an OnClickListener to toggle the visibility of the files
                    folderTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Folder Clicked", "Folder clicked: " + folderName);
                            Log.d("File System", fileSystem);
                            Log.d("nestedObject", nestedObject.toString());
                            Log.d("nestedKey", nestedKey);
                            try{
                                currentArray = nestedObject.toString();
                                parentLayout.removeAllViewsInLayout();

                                JSONObject pathJS = new JSONObject(path);
                                JSONArray pathArray = pathJS.getJSONArray("path");
                                pathArray.put(nestedKey);
                                path = pathJS.toString();
                                Log.d("PATH", path);

                                createFolderWithFiles(parentLayout, nestedKey,  nestedObject.getJSONArray(nestedKey));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });


                }
            }
        }


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
                            createFolderWithFiles(rootLayout, "root", jsObj.getJSONArray("root"));
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

    public void shareToUser(String fromUser, String toUser, String docName){
        Uri.Builder builder = Uri.parse(URL_DELETE_REQ).buildUpon();
        builder.appendQueryParameter("fromUser", fromUser);
        builder.appendQueryParameter("toUser", toUser);
        builder.appendQueryParameter("docName", docName);
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        Toast.makeText(getApplicationContext(), "File Shared", Toast.LENGTH_SHORT).show();
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

    public String newFolder(String currentArray, String fileSystem , String folderName) throws JSONException {
        JSONObject currentArrayJS = new JSONObject(currentArray);
        String newKey = currentArrayJS.keys().next();

        JSONArray newRoot = currentArrayJS.getJSONArray(newKey);
        newRoot.put(new JSONObject("{\""+ folderName+"\" : []}"));

        JSONObject pathJS = new JSONObject(this.path);
        JSONArray pathArray = pathJS.getJSONArray("path");
        JSONObject fileSystemJS = new JSONObject(fileSystem);
        JSONArray currArrayJS = fileSystemJS.getJSONArray("root");
        pathArray.remove(0);

        for (int i = 0; i < pathArray.length(); i++) {
            String key = (String) pathArray.get(i);
            for (int j = 0; j < currArrayJS.length(); j++) {
                Object item = currArrayJS.get(j);
                if (item instanceof JSONObject){
                    JSONObject itemJs = (JSONObject) item;
                    String internalKey = itemJs.keys().next();
                    if (internalKey.equals(key)){
                        currArrayJS = itemJs.getJSONArray(internalKey);
                        break;
                    }
                }
            }
        }
        currArrayJS.put(new JSONObject("{\""+ folderName+"\" : []}"));
        this.fileSystem = fileSystemJS.toString();
        Log.d("JSON ARRAY", currentArray.toString());
        return currentArrayJS.toString();
    }

    public void addItemToPath(String currentPath, String fileName){

    };
}
