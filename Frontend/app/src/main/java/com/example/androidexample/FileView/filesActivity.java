package com.example.androidexample.FileView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.androidexample.Editor.TextActivity;
import com.example.androidexample.Editor.VoiceRecordActivity;
import com.example.androidexample.Gallery.GalleryPopulator;
import com.example.androidexample.Gallery.PhotoGalleryActivity;
import com.example.androidexample.NavigationBar;
import com.example.androidexample.R;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.VolleySingleton;
import com.example.androidexample.WebSockets.WebSocketManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class filesActivity extends AppCompatActivity {
    private final String URL_STRING_PUSH = "http://coms-3090-068.class.las.iastate.edu:8080/files/upload";
    private final String URL_STRING_PULL = "http://coms-3090-068.class.las.iastate.edu:8080/files/pull";
    private final String URL_ID_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/fileid";
    private final String URL_DELETE_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/deleteFile";
    private final String URL_FOLDER_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/update";
    private final String URL_FRIEND_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/share/new";
    private final String URL_WS = "ws://coms-3090-068.class.las.iastate.edu:8080/document/";
    private final String URL_AIWS = "ws://coms-3090-068.class.las.iastate.edu:8080/chat/";
    private final String URL_AUTOINDEX = "";

    private String fileSystem =  "{\"root\": []}";
    private String path = "{\"path\": [\"root\"]}";
    private String currentArray = "{\"root\": []}";
    private String aiURL;

    private String email;
    private String username;
    private String password;

    private List<String> recentFiles;
    private RecyclerView recentFilesView;

    private Button goback;
    private Button OCRButt;
    private Button AutoIndex;
    private LinearLayout rootLayout;
    private LinearLayout fileLayout;

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

        NavigationBar navigationBar = new NavigationBar(this);
        navigationBar.addNavigationBar(R.layout.activity_file_view);

        initializeVariables();
        setupUIComponents();
    }

    /**
     * Initializes instance variables from user preferences or other sources.
     */
    private void initializeVariables() {
        email = UserPreferences.getEmail(this);
        password = UserPreferences.getPassword(this);
        username = UserPreferences.getUsername(this);
        fileSystem = UserPreferences.getFileSystem(this);
        path = UserPreferences.getFilePath(this);
    }

    /**
     * Sets up the UI components and their event listeners.
     */
    private void setupUIComponents() {
        goback = findViewById(R.id.goback);
        rootLayout = findViewById(R.id.rootLayout);
        //OCRButt = findViewById(R.id.OCRButt);
        AutoIndex = findViewById(R.id.AutoIndex);
        recentFilesView = findViewById(R.id.recentFilesView);
        recentFilesView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));

        goback.setOnClickListener(view -> handleGoBack());
        //OCRButt.setOnClickListener(view -> navigateToOCR());
        AutoIndex.setOnClickListener(view -> autoIndexAPI(fileSystem));

        refreshLayout();
    }

    private void handleGoBack() {
        try {
            JSONObject base = new JSONObject(path);
            JSONArray pathArray = base.getJSONArray("path");

            if (pathArray.length() > 1) {
                pathArray.remove(pathArray.length() - 1);
                setPath(base.toString());
                refreshLayout();
                Log.d("Current Array", currentArray);
            } else {
                Intent intent = new Intent(filesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void navigateToOCR() {
        Intent intent = new Intent(filesActivity.this, OCRActivity.class);
        startActivity(intent);
    }

    private void getRecentFiles()
    {
        /*
         * Creates a new HTTP client so that I can get the list of photo names from 'getPhotoList'.
         */
        OkHttpClient client = new OkHttpClient();

        String filesUrl = UserPreferences.getUrlRecentFiles();
        filesUrl += "/" + email;

        Log.d("URLRecent", filesUrl);

        /*
         * It's a request to access the client url.
         */
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(filesUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(filesActivity.this, "Failed File Fetch", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null)
                {
                    String listOfNames = response.body().string();
                    Log.d("List of Names", listOfNames);
                    Gson gson = new Gson();

                    // Reflection Library
                    Type classType = new TypeToken<List<String>>(){}.getType();
                    recentFiles = gson.fromJson(listOfNames, classType);

                    Log.d("Recent Files", recentFiles.toString());

                    /*
                     * Populate the (RecyclerView) galleryView with the list of photos, using 'GalleryPopulator'.
                     */
                    new Handler(Looper.getMainLooper()).post(() -> {
                        RecentFilePopulator recentlyViewed = new RecentFilePopulator(recentFiles, filesActivity.this, email, password, username);
                        recentFilesView.setAdapter(recentlyViewed);
                    });
                }
            }
        });
    }

    private void newButtonFunctionality(EditText newFolderName, String type){
        Log.d("New Folder", "New Folder clicked: " + newFolderName.getText().toString());
        try {
            String correctedFolderName = newFolderCheck(newFolderName.getText().toString());
            if (!correctedFolderName.isEmpty()){
                newItem(currentArray, fileSystem, correctedFolderName, type);
            }else{
                Toast.makeText(getApplicationContext(), "Folder Name is invalid", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createUIButtons(LinearLayout parentLayout){
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


        newFolder.setOnClickListener(view -> newButtonFunctionality(newFolderName, "folder"));
        newFile.setOnClickListener(view -> newButtonFunctionality(newFolderName, "file"));

        parentLayout.addView(newFolderName);
        parentLayout.addView(newFolderLayout);
    }

    /**
     * Creates the user interface dynamically, including buttons for creating new folders and files.
     * Also initializes the current folder view and displays its contents.
     *
     * @param parentLayout the root layout to which the UI elements are added
     */
    private void createUI(LinearLayout parentLayout) {
        createUIButtons(parentLayout);
        try{
            fileLayout = new LinearLayout(this);
            fileLayout.setOrientation(LinearLayout.VERTICAL);
            fileLayout.setId(View.generateViewId());
            getRecentFiles();
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

            //addNavigationBar();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Helper function to dynamically add a navigation bar at the bottom of the screen.
     */
    private void addNavigationBar() {
        // Create a new LinearLayout for the navigation bar
        LinearLayout rootLayout = findViewById(R.id.navbar);
        LinearLayout navBarLayout = new LinearLayout(this);
        navBarLayout.setOrientation(LinearLayout.HORIZONTAL);
        navBarLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        navBarLayout.setPadding(8, 8, 8, 8);
        navBarLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        navBarLayout.setElevation(4); // Add shadow for elevation
        navBarLayout.setGravity(Gravity.CENTER);

        // Create navigation buttons
        ImageButton micButton = createNavButton(R.drawable.mic, "Mic");

        micButton.setOnClickListener(view -> {
            Intent intent = new Intent(filesActivity.this, VoiceRecordActivity.class);
            startActivity(intent);
        });
        ImageButton homeButton = createNavButton(R.drawable.home, "Home");
        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(filesActivity.this, MainActivity.class);
            startActivity(intent);
        });
        ImageButton editButton = createNavButton(R.drawable.navbar_create_note, "Edit");
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(filesActivity.this, TextActivity.class);
            startActivity(intent);
        });

        // Add buttons to the navigation bar layout
        navBarLayout.addView(micButton);
        navBarLayout.addView(homeButton);
        navBarLayout.addView(editButton);

        // Add the navigation bar to the parent layout
        rootLayout.addView(navBarLayout);
    }

    /**
     * Helper function to create individual navigation buttons.
     */
    private ImageButton createNavButton(int iconResId, String contentDescription) {
        ImageButton navButton = new ImageButton(this);
        navButton.setLayoutParams(new LinearLayout.LayoutParams(
                0, // Equal spacing
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1 // Weight for equal distribution
        ));
        navButton.setImageResource(iconResId);
        //navButton.setBackgroundResource(android.R.attr.selectableItemBackground); // Touch feedback
        navButton.setContentDescription(contentDescription);
        navButton.setPadding(8, 8, 8, 8); // Add padding for spacing
        navButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // Adjust scaling
        return navButton;
    }

    private void createFolderWithFiles(LinearLayout parentLayout, String folderName, JSONArray filesArray) throws JSONException {
        for (int i = 0; i < filesArray.length(); i++) {
            Object item = filesArray.get(i);
            if (item instanceof String) {
                createFileLayout(parentLayout, (String) item);
            } else if (item instanceof JSONObject) {
                createFolderLayout(parentLayout, (JSONObject) item);
            }
        }
    }
    //Files
    private void createFileLayout(LinearLayout parentLayout, String fileName) {
        // Inflate the XML layout and add it to the parent layout
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout rootLayout = findViewById(R.id.rootLayout); // Ensure parentLayout exists in your main XML

        // Dynamically create a new note item
        View noteItem = inflater.inflate(R.layout.note_item, rootLayout, false);

        // Get references to the child views
        TextView fileTextView = noteItem.findViewById(R.id.fileTextView);
        ImageButton deleteButton = noteItem.findViewById(R.id.deleteButton);
        ImageButton shareButton = noteItem.findViewById(R.id.shareButton);
        EditText toUser = noteItem.findViewById(R.id.toUser);

        // Set the note name or data for the TextView
        fileTextView.setText(fileName);

        // Set click listeners for each component
        fileTextView.setOnClickListener(view -> {
            Log.d("File Clicked", "File clicked: " + fileName);
            getFile(fileName);
        });

        deleteButton.setOnClickListener(view -> {
            Log.d("File Deleted", "File deleted: " + fileName);
            try {
                JSONObject jsObj = fileDeletor(new JSONObject(fileSystem), new JSONObject(path), fileName);
                deleteFile(fileName, jsObj.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        shareButton.setOnClickListener(view -> {
            String username = toUser.getText().toString();
            shareToUser(email, username, fileName); // Call your existing method
        });

        // Add the note item to the parent layout
        rootLayout.addView(noteItem);
    }

    private Button createFileDeleteButton(String fileName) {
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setPadding(20, 10, 20, 10);
        deleteButton.setOnClickListener(view -> {
            Log.d("File Deleted", "File deleted: " + fileName);
            try {
                JSONObject jsObj = fileDeletor(new JSONObject(fileSystem), new JSONObject(path), fileName);
                deleteFile(fileName, jsObj.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return deleteButton;
    }

    private Button createShareToButton(String fileName) {
        Button shareToButton = new Button(this);
        shareToButton.setText("Share to");
        shareToButton.setPadding(20, 10, 20, 10);
        shareToButton.setOnClickListener(view -> {
            EditText toUser = new EditText(this);
            shareToUser(email.toString(), toUser.getText().toString(), fileName);
        });
        return shareToButton;
    }

    private EditText createShareToInput() {
        EditText toUser = new EditText(this);
        toUser.setHint("Username");
        toUser.setPadding(20, 10, 20, 10);
        return toUser;
    }
    //Files End

    //Folders
    private void createFolderLayout(LinearLayout parentLayout, JSONObject nestedObject) {
        // Inflate the folder_item.xml layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View folderView = inflater.inflate(R.layout.folder_item, parentLayout, false);

        // Find components inside folder_item.xml
        TextView folderTextView = folderView.findViewById(R.id.folderTextView);
        ImageButton deleteButton = folderView.findViewById(R.id.deleteButton);

        // Set folder name
        String folderName = nestedObject.keys().next();
        folderTextView.setText(folderName);

        // Set OnClickListener for folder TextView to handle navigation
        folderTextView.setOnClickListener(view ->
                moveToInnerFolder(parentLayout, nestedObject, folderName)
        );

        // Set OnClickListener for delete button to handle deletion
        deleteButton.setOnClickListener(view -> {
            // Handle folder deletion logic here
            deleteFolder(nestedObject);
        });

        // Add the inflated folder view to the parent layout
        parentLayout.addView(folderView);

    }

    private void deleteFolder(JSONObject nestedObject) {
        try {
            JSONObject jsObj = fileDeletor(new JSONObject(fileSystem), new JSONObject(path), nestedObject.toString());
            setFileSystem(jsObj.toString());
            newfolderUpdate(fileSystem);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private Button createFolderDeleteButton(JSONObject nestedObject) {
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setPadding(20, 10, 20, 10);
        deleteButton.setOnClickListener(view -> {
            try {
                JSONObject jsObj = fileDeletor(new JSONObject(fileSystem), new JSONObject(path), nestedObject.toString());
                setFileSystem(jsObj.toString());
                newfolderUpdate(fileSystem);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        return deleteButton;
    }

    private void moveToInnerFolder(LinearLayout parentLayout, JSONObject nestedObject, String folderName) {
        Log.d("Folder Clicked", "Folder clicked: " + folderName);
        try {
            JSONObject pathJS = new JSONObject(path);
            JSONArray pathArray = pathJS.getJSONArray("path");
            pathArray.put(folderName);
            setPath(pathJS.toString());
            refreshLayout();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    //Folders end

    /**
     * Retrieves the file's content as a string from the server and starts the `TextActivity`
     * to display and edit the file content.
     *
     * @param fileName the name of the file to retrieve
     */
    private void getFileString(String fileName){
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

                        Intent intent = new Intent(filesActivity.this, TextActivity.class);
                        intent.putExtra("FILEKEY", fileName);
                        intent.putExtra("CONTENT", response);
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
    private void getFile(String fileName){
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
    private void newfolderUpdate(String fileSystem){
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
                        refreshLayout();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Email", email);
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(getApplicationContext(), "Folder Not Updated", Toast.LENGTH_SHORT).show();
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
    private void deleteFile(String fileName, String newFileSystem){
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
                        Log.d("JSON OBJECT", newFileSystem);
                        setFileSystem(newFileSystem);
                        refreshLayout();
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
    private void shareToUser(String fromUser, String toUser, String docName){
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
    private String newFolderCheck(String newFileName){
        if (    newFileName.isEmpty()||
                newFileName.equals(" ")||
                newFileName.contains(",")||
                newFileName.contains("{")||
                newFileName.contains("}")||
                newFileName.contains("[")||
                newFileName.contains("]")||
                newFileName.contains("/")||
                newFileName.trim().isEmpty() ||
                fileSystem.contains(newFileName)
        )
        {
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
    private JSONObject fileDeletor(JSONObject fileSystem, JSONObject filePath, String fileName) throws JSONException {
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
    private String newItem(String currentArray, String fileSystem , String folderName, String type) throws JSONException {
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
            setFileSystem(fileSystemJS.toString());
            newfolderUpdate(this.fileSystem);
        }else{
            currArrayJS.put(folderName);
            setFileSystem(fileSystemJS.toString());
            newFileUpdate(folderName, this.fileSystem);
        }
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
    private JSONObject goToPath(String currentPath, String fileSystem) throws JSONException {
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
        return new JSONObject("{\"" + internalKey + "\" : " + currArrayJS.toString() + '}');
    };


    private void newFileUpdate(String fileName, String fileSystem){
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
                        refreshLayout();
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

    private void autoIndexAPI(String fileSys){
        Uri.Builder builder = Uri.parse(URL_AUTOINDEX).buildUpon();
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
                        setFileSystem(response);
                        refreshLayout();
                        newfolderUpdate(fileSystem);
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

    public void setFileSystem(String fs){
        this.fileSystem = fs;
        UserPreferences.saveUserDetails(filesActivity.this, username, email, password, fileSystem, path);
    }

    public void setPath(String p){
        this.path = p;
        UserPreferences.saveUserDetails(filesActivity.this, username, email, password, fileSystem, path);
    }

    private void refreshLayout() {
        try {
            JSONObject PathObj = new JSONObject(path);
            JSONArray pathArray = PathObj.getJSONArray("path");
            currentArray = goToPath(String.valueOf(pathArray), fileSystem).toString();
            rootLayout.removeAllViewsInLayout();
            runOnUiThread(() -> createUI(rootLayout));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}
