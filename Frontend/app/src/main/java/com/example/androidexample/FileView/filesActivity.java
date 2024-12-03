package com.example.androidexample.FileView;

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
import com.example.androidexample.Editor.TextActivity;
import com.example.androidexample.R;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.VolleySingleton;
import com.example.androidexample.WebSockets.WebSocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class filesActivity extends AppCompatActivity {
    private final String URL_STRING_PUSH = "http://coms-3090-068.class.las.iastate.edu:8080/files/upload";
    private final String URL_STRING_PULL = "http://coms-3090-068.class.las.iastate.edu:8080/files/pull";
    private final String URL_ID_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/fileid";
    private final String URL_DELETE_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/deleteFile";
    private final String URL_FOLDER_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/update";
    private final String URL_FRIEND_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/share/new";
    private final String URL_WS = "ws://coms-3090-068.class.las.iastate.edu:8080/document/";
    private final String URL_AIWS = "ws://coms-3090-068.class.las.iastate.edu:8080/chat/";

    private String fileSystem =  "{\"root\": []}";
    private String path = "{\"path\": [\"root\"]}";
    private String email;
    private String username;
    private String password;
    private String content;
    private Button goback;
    private Button OCRButt;
    private Button AutoIndex;
    private LinearLayout rootLayout;
    private String currentArray = "{\"root\": []}";
    private LinearLayout fileLayout;
    private String aiURL;


    /**
     * Initializes the activity and sets up the user interface.
     * Retrieves user preferences and initializes necessary variables, such as the file system and user credentials.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        email = UserPreferences.getEmail(this);
        password = UserPreferences.getPassword(this);
        username = UserPreferences.getUsername(this);
        fileSystem = UserPreferences.getFileSystem(this);

        currentArray = fileSystem;

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

        AutoIndex = findViewById(R.id.AutoIndex);
        AutoIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoIndexAPI(fileSystem);
            }});

    }

    /**
     * Creates the user interface dynamically, including buttons for creating new folders and files.
     * Also initializes the current folder view and displays its contents.
     *
     * @param parentLayout the root layout to which the UI elements are added
     */
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
        newFolderName.setHint("New Name");
        newFolderName.setPadding(20, 10, 20, 10);

        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("New Folder", "New Folder clicked: " + newFolderName.getText().toString());
                try {
                    String correctedFolderName = newFolderCheck(newFolderName.getText().toString());
                    if (!correctedFolderName.isEmpty()){
                        currentArray = newItem(currentArray, fileSystem, correctedFolderName, "folder");
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
                try {
                    String correctedFolderName = newFolderCheck(newFolderName.getText().toString());
                    if (!correctedFolderName.isEmpty()){
                        currentArray = newItem(currentArray, fileSystem, correctedFolderName, "file");
                        sendFileString(correctedFolderName, fileSystem);

                    }else{
                        Toast.makeText(getApplicationContext(), "File Name is invalid", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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
    /**
     * Dynamically creates a folder view along with its files and subfolders.
     * Adds functionality to open, delete, and share files or folders.
     *
     * @param parentLayout the parent layout to which the folder view is added
     * @param folderName   the name of the current folder
     * @param filesArray   the array of files or folders within the current folder
     * @throws JSONException if an error occurs while parsing the JSON
     */
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
                                UserPreferences.saveUserDetails(filesActivity.this, username, email, password, fileSystem, path);
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

    /**
     * Retrieves the file's content as a string from the server and starts the `TextActivity`
     * to display and edit the file content.
     *
     * @param fileName the name of the file to retrieve
     */
    public void getFileString(String fileName){
        Uri.Builder builder = Uri.parse(URL_STRING_PULL).buildUpon();
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
                        intent.putExtra("FILEKEY", fileName);
                        intent.putExtra("CONTENT", content);
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

    /**
     * Retrieves the file's unique ID from the server and connects to its WebSocket instance.
     * Loads the file's content for editing in a `TextActivity`.
     *
     * @param fileName the name of the file to retrieve
     */
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

    /**
     * Updates the file system on the server after modifications, such as adding or deleting folders.
     *
     * @param fileSystem the updated file system JSON string
     */
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
    /**
     * Deletes a file from the server and updates the local file system structure.
     *
     * @param fileName       the name of the file to delete
     * @param newFileSystem  the updated file system JSON string
     */
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
                            UserPreferences.saveUserDetails(filesActivity.this, username, email, password, fileSystem, path);
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

    /**
     * Shares a file with another user by sending a request to the server.
     *
     * @param fromUser the email of the user sharing the file
     * @param toUser   the username of the recipient
     * @param docName  the name of the file to share
     */
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

    /**
     * Validates the new folder name to ensure it does not contain illegal characters.
     *
     * @param newFileName the name of the folder to validate
     * @return the validated folder name, or an empty string if invalid
     */
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

    /**
     * Deletes a file from the specified file system and path.
     *
     * @param fileSystem the JSON object representing the root of the file system
     * @param filePath   the JSON object containing the "path" array to locate the file
     * @param fileName   the name of the file to delete
     * @return the updated JSON object representing the file system after deletion
     * @throws JSONException if there is an error parsing the JSON objects
     */
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

    /**
     * Creates a new folder in the specified file system and updates the current array.
     *
     * @param currentArray the current directory as a JSON string
     * @param fileSystem   the JSON string representing the root of the file system
     * @param folderName   the name of the new folder to create
     * @return the updated JSON string representing the current directory
     * @throws JSONException if there is an error parsing the JSON objects
     */
    public String newItem(String currentArray, String fileSystem , String folderName, String type) throws JSONException {
        JSONObject currentArrayJS = new JSONObject(currentArray);
        String newKey = currentArrayJS.keys().next();

        JSONArray newRoot = currentArrayJS.getJSONArray(newKey);
        if (type.equals("folder")){
            newRoot.put(new JSONObject("{\""+ folderName+"\" : []}"));
        }else{
            newRoot.put(folderName);
        }

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
        if (type.equals("folder")){
            currArrayJS.put(new JSONObject("{\""+ folderName+"\" : []}"));
        }else{
            currArrayJS.put(folderName);
        }
        this.fileSystem = fileSystemJS.toString();
        UserPreferences.saveUserDetails(filesActivity.this, username, email, password, this.fileSystem, this.path);
        Log.d("JSON ARRAY", currentArray.toString());
        return currentArrayJS.toString();
    }

    /**
     * Navigates to a specified path in the file system and returns the corresponding JSON object.
     *
     * @param currentPath the JSON string representing the path to navigate to
     * @param fileSystem  the JSON string representing the root of the file system
     * @return a JSON object representing the directory or file at the specified path
     * @throws JSONException if there is an error parsing the JSON objects
     */
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


    public void sendFileString(String fileName, String fileSystem){
        Uri.Builder builder = Uri.parse(URL_STRING_PUSH).buildUpon();
        builder.appendQueryParameter("fileName", fileName);
        builder.appendQueryParameter("content", "");
        builder.appendQueryParameter("json", fileSystem);
        builder.appendQueryParameter("email", email);
        builder.appendQueryParameter("password", password);
        String url = builder.build().toString();
        Log.d("URL", url);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        rootLayout.removeAllViewsInLayout();
                        runOnUiThread(()->{
                            createUI(rootLayout);
                        });
                        folderUpdate(fileSystem);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Email", email);

                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


    public void autoIndexAPI(String fileSys){
        Uri.Builder builder = Uri.parse(URL_FOLDER_REQ).buildUpon();
        builder.appendQueryParameter("json", fileSys);
        builder.appendQueryParameter("email", email);
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        fileSystem = response;
                        UserPreferences.saveUserDetails(filesActivity.this, username, email, password, fileSystem, path);
                        rootLayout.removeAllViewsInLayout();
                        path = "{\"path\": [\"root\"]}";
                        runOnUiThread(()->{
                            createUI(rootLayout);
                        });
                        folderUpdate(fileSystem);
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
}
