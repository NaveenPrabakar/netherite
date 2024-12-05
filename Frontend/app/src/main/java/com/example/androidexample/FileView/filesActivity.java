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
        OCRButt = findViewById(R.id.OCRButt);
        AutoIndex = findViewById(R.id.AutoIndex);

        goback.setOnClickListener(view -> handleGoBack());
        OCRButt.setOnClickListener(view -> navigateToOCR());
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
        newFolder.setId(1000001);

        Button newFile = new Button(this);
        newFile.setText("New File");
        newFile.setPadding(20, 10, 20, 10);
        newFile.setId(1000002);

        newFolderLayout.addView(newFolder);
        newFolderLayout.addView(newFile);

        EditText newFolderName = new EditText(this);
        newFolderName.setHint("New Name");
        newFolderName.setPadding(20, 10, 20, 10);
        newFolderName.setId(1000003);

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
        // Create a horizontal LinearLayout for the file
        LinearLayout fileLayout = new LinearLayout(this);
        fileLayout.setOrientation(LinearLayout.HORIZONTAL);
        fileLayout.setPadding(10, 10, 10, 10);

        // Create a TextView for the file
        TextView fileTextView = new TextView(this);
        fileTextView.setText(fileName);
        fileTextView.setPadding(30, 10, 10, 10);

        // Create buttons and input field
        Button deleteButton = createFileDeleteButton(fileName);
        EditText toUser = createShareToInput();
        Button shareToButton = createShareToButton(fileName, toUser);


        // Add onClickListener for file TextView
        fileTextView.setOnClickListener(view -> {
            Log.d("File Clicked", "File clicked: " + fileName);
            getFile(fileName);
        });

        // Add components to the file layout
        fileLayout.addView(fileTextView);
        fileLayout.addView(deleteButton);
        fileLayout.addView(shareToButton);
        fileLayout.addView(toUser);

        // Add the file layout to the parent layout
        parentLayout.addView(fileLayout);
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

    private Button createShareToButton(String fileName, EditText toUser) {
        Button shareToButton = new Button(this);
        shareToButton.setText("Share to");
        shareToButton.setPadding(20, 10, 20, 10);
        shareToButton.setOnClickListener(view -> {
            shareToUser(username, toUser.getText().toString(), fileName);
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
        String folderName = nestedObject.keys().next();
        LinearLayout folderLayout = new LinearLayout(this);
        folderLayout.setOrientation(LinearLayout.HORIZONTAL);
        folderLayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        folderLayout.setPadding(10, 10, 10, 10);

        // Create a TextView for the folder
        TextView folderTextView = new TextView(this);
        folderTextView.setText(folderName);
        folderTextView.setTextSize(18);
        folderTextView.setPadding(10, 10, 10, 10);

        // Create a Delete Button for the folder
        Button deleteButton = createFolderDeleteButton(nestedObject);

        // Add components to the folder layout
        folderLayout.addView(folderTextView);
        folderLayout.addView(deleteButton);
        parentLayout.addView(folderLayout);

        // Set OnClickListener to toggle files in the folder
        folderTextView.setOnClickListener(view -> moveToInnerFolder(parentLayout, nestedObject, folderName));
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
        Log.d("URL", url);

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
