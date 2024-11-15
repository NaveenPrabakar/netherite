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
    private final String URL_ID_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/fileid";
    private final String URL_DELETE_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/deleteFile";
    private final String URL_FOLDER_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/update";
    private final String URL_FRIEND_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/share/new";
    private final String URL_WS = "ws://coms-3090-068.class.las.iastate.edu:8080/document/";
    private final String URL_AIWS = "ws://coms-3090-068.class.las.iastate.edu:8080/chat/";

    private String fileSystem =  "{\"root\": []}";
    // path is hard coded. make a path lmao. make it dynamic
    // when i click a file or a folder, it should update the path.
    private String path = "{\"path\": [\"root\"]}";
    private String email;
    private String username;
    private String password;
    private String content;
    private Button goback;
    private Button OCRButt;
    private LinearLayout rootLayout;
    private String currentArray = "{\"root\": []}";
    private LinearLayout fileLayout;
    private String aiURL;

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
        goback = findViewById(R.id.goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    JSONObject base = new JSONObject(path);
                    JSONArray pathArray = base.getJSONArray("path");
                    if (pathArray.length() > 1){
                        pathArray.remove(pathArray.length()-1);
                        currentArray = goToPath(String.valueOf(pathArray), fileSystem).toString();
                        String currFolder = pathArray.get(pathArray.length()-1).toString();
                        path = base.toString();
                        fileLayout.removeAllViewsInLayout();
                        runOnUiThread(()->{
                            try {
                                createFolderWithFiles(fileLayout, currFolder, new JSONObject(currentArray).getJSONArray(currFolder) );
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        Log.d("Current Array", currentArray);
                    }else{
                        Intent intent = new Intent(filesActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                ;
            }
        });

        rootLayout = findViewById(R.id.rootLayout);
        runOnUiThread(()->{
            createUI(rootLayout);
        });

        OCRButt = findViewById(R.id.OCRButt);
        OCRButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(filesActivity.this, OCRActivity.class);
                i.putExtra("FILESYSTEM", fileSystem);
                i.putExtra("PATH", path);
                i.putExtra("EMAIL", email);
                i.putExtra("PASSWORD", password);
                i.putExtra("USERNAME", username);
                startActivity(i);
            }
        });
    }

    private void createUI(LinearLayout parentLayout) {
        LinearLayout newFolderLayout = new LinearLayout(this);
        newFolderLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button newFolder = new Button(this);
        newFolder.setText("New Folder");
        newFolder.setPadding(20, 10, 20, 10);

        Button newFile = new Button(this);
        newFile.setText("New File");
        newFile.setPadding(20, 10, 20, 10);

        newFolderLayout.addView(newFolder);
        newFolderLayout.addView(newFile);

        EditText newFolderName = new EditText(this);
        newFolderName.setHint("New Folder Name");
        newFolderName.setPadding(20, 10, 20, 10);

        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("New Folder", "New Folder clicked: " + newFolderName.getText().toString());
                try {
                    String correctedFolderName = newFolderCheck(newFolderName.getText().toString());
                    if (!correctedFolderName.isEmpty()){
                        currentArray = newFolder(currentArray, fileSystem, correctedFolderName);
                        rootLayout.removeAllViewsInLayout();
                        runOnUiThread(()->{
                            createUI(rootLayout);
                        });
                        folderUpdate(fileSystem);
                    }else{
                        Toast.makeText(getApplicationContext(), "Folder Name is invalid", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        newFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(filesActivity.this, TextActivity.class);
                i.putExtra("FILESYSTEM", fileSystem);
                i.putExtra("PATH", path);
                i.putExtra("CONTENT", "");
                i.putExtra("EMAIL", email);
                i.putExtra("PASSWORD", password);
                i.putExtra("AIWSURL", aiURL);
                startActivity(i);
            }
        });

        parentLayout.addView(newFolderName);
        parentLayout.addView(newFolderLayout);
        try{
            fileLayout = new LinearLayout(this);
            fileLayout.setOrientation(LinearLayout.VERTICAL);
            parentLayout.addView(fileLayout);
            JSONObject currObj = new JSONObject(currentArray);
            String key = currObj.keys().next();
            runOnUiThread(()->{
                try {
                    createFolderWithFiles(fileLayout, key, currObj.getJSONArray(key));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

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
                        getFile(String.valueOf(item));

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
                        shareToUser(email.toString(), toUser.getText().toString(), String.valueOf(item));
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
                    LinearLayout linearLayout = new LinearLayout(this);

                    String nestedKey = it.next();
                    TextView folderTextView = new TextView(this);
                    folderTextView.setText(nestedKey);
                    folderTextView.setTextSize(18);
                    folderTextView.setPadding(10, 10, 10, 10);
                    linearLayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    folderTextView.setClickable(true);

                    // Create a Delete Button
                    Button deleteButton = new Button(this);
                    deleteButton.setText("Delete");
                    deleteButton.setPadding(20, 10, 20, 10);

                    linearLayout.addView(folderTextView);
                    linearLayout.addView(deleteButton);
                    parentLayout.addView(linearLayout);

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                JSONObject jsObj = fileDeletor(new JSONObject(fileSystem), new JSONObject(path), String.valueOf(item));
                                rootLayout.removeAllViewsInLayout();
                                fileSystem = jsObj.toString();
                                Log.d("item", String.valueOf(item));

                                JSONObject currentJS = new JSONObject(currentArray);
                                JSONArray currentArr = currentJS.getJSONArray(currentJS.keys().next());
                                for (int i = 0; i < currentArr.length(); i++){
                                    if (currentArr.get(i).toString().equals(String.valueOf(item))){
                                        currentArr.remove(i);
                                    }
                                }
                                currentArray = currentJS.toString();
                                runOnUiThread(()->{
                                    createUI(rootLayout);
                                });
                                folderUpdate(fileSystem);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        };
                    });

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
                                runOnUiThread(()->{
                                    try {
                                        createFolderWithFiles(fileLayout, nestedKey,  nestedObject.getJSONArray(nestedKey));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

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
                        intent.putExtra("AIWSURL", aiURL);
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

    public void getFile(String fileName){
        Uri.Builder builder = Uri.parse(URL_ID_REQ).buildUpon();
        builder.appendQueryParameter("email", email);
        builder.appendQueryParameter("fileName", fileName );
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        int id = Integer.parseInt(response);

                        String serverUrl = URL_WS + id;
                        aiURL = URL_AIWS + id + "/" + username;

                        Log.d("Instance URL", aiURL);
                        WebSocketManager.getInstance().connectWebSocket(serverUrl);
                        getFileString(fileName);

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

    public void folderUpdate(String fileSystem){
        Uri.Builder builder = Uri.parse(URL_FOLDER_REQ).buildUpon();
        builder.appendQueryParameter("json", fileSystem);
        builder.appendQueryParameter("email", email);
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        Toast.makeText(getApplicationContext(), "Folder Updated", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Email", email);
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(getApplicationContext(), "Folder Updated", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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
                            JSONObject currentJS = new JSONObject(currentArray);
                            JSONArray currentArr = currentJS.getJSONArray(currentJS.keys().next());
                            for (int i = 0; i < currentArr.length(); i++){
                                if (currentArr.get(i).equals(fileName)){
                                    currentArr.remove(i);
                                }
                            }
                            currentArray = currentJS.toString();
                            runOnUiThread(()->{
                                createUI(rootLayout);
                            });
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
        Uri.Builder builder = Uri.parse(URL_FRIEND_REQ).buildUpon();
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
                        Toast.makeText(getApplicationContext(), "File Share Failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public String newFolderCheck(String newFileName){
        if (    newFileName.isEmpty()||
                newFileName.equals(" ")||
                newFileName.contains(",")||
                newFileName.contains("{")||
                newFileName.contains("}")||
                newFileName.contains("[")||
                newFileName.contains("]")||
                newFileName.contains("/")||
                newFileName.trim().isEmpty()){
            return "";
        }
        return newFileName;
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
            if (rootArray.get(i).toString().equals(fileName)){
                index = i;
            }
        }
        if (index != -1){
            root.remove(index);
        }
        Log.d("File System", fileSystem.toString());
        return fileSystem;

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

    public JSONObject goToPath(String currentPath, String fileSystem) throws JSONException {
        JSONArray pathArray = new JSONArray(currentPath);
        JSONObject fileSystemJS = new JSONObject(fileSystem);
        JSONArray currArrayJS = fileSystemJS.getJSONArray("root");
        String internalKey = pathArray.getString(0);
        pathArray.remove(0);

        for (int i = 0; i < pathArray.length(); i++) {
            String key = (String) pathArray.get(i);
            for (int j = 0; j < currArrayJS.length(); j++) {
                Object item = currArrayJS.get(j);
                if (item instanceof JSONObject){
                    JSONObject itemJs = (JSONObject) item;
                    internalKey = itemJs.keys().next();
                    if (internalKey.equals(key)){
                        currArrayJS = itemJs.getJSONArray(internalKey);
                        break;
                    }
                }
            }
        }
        return new JSONObject('{' + internalKey + " : " + currArrayJS.toString() + '}');
    };
}
