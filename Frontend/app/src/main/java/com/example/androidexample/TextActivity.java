package com.example.androidexample;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.noties.markwon.Markwon;

public class TextActivity extends AppCompatActivity {
    private final String URL_STRING_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/upload";
    private Button back2main;
    private Button saveButt;
    private EditText mainText;
    private EditText editor;
    private EditText fileName;
    private Markwon markwon;
    private String content = "";
    private JSONObject fileSystem;
    private JSONObject filePath;
    private String username;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mainText = findViewById(R.id.textViewMarkdown);
        editor = findViewById(R.id.EditMarkdown);
        fileName = findViewById(R.id.fileName);

        markwon = Markwon.create(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    content = charSequence.toString();
                    updateParsedOutput(content);
                    Log.d("Text changed", content);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        editor.setAlpha(0f);

        if(extras != null) {
            try {
                fileSystem = new JSONObject(extras.getString("FILESYSTEM"));
                filePath = new JSONObject(extras.getString("PATH"));
                username = extras.getString("USERNAME");
                password = extras.getString("PASSWORD");
                Log.d("USERNAME", extras.getString("USERNAME"));
                Log.d("PASSWORD", extras.getString("PASSWORD"));
                Log.d("FILESYSTEM", extras.getString("FILESYSTEM"));
                Log.d("PATH", extras.getString("PATH"));

                if (extras.getString("CONTENT") != null){
                    Log.d("content", extras.getString("CONTENT"));
                    editor.setText(extras.getString("CONTENT"));
                }
                if (extras.getString("FILEKEY") != null){
                    Log.d("filekey", extras.getString("FILEKEY"));
                    fileName.setText(extras.getString("FILEKEY"));
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }else{
            filePath = new JSONObject();
            fileSystem = new JSONObject();
            try {
                filePath.put("path", new JSONArray());
                fileSystem.put("root", new JSONArray());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

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
                if (fileName.getText().toString().isEmpty()) {
                    System.out.println("file name is empty");
                } else {

                    try {
                        sendFileString(fileName.getText().toString(), String.valueOf(fileLocator(fileSystem, filePath, fileName.getText().toString())));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        });
    }

    private void updateParsedOutput(String markdown) {
        String contentParsed = "";
        for (int i = 0; i < markdown.length(); i++){
            if (markdown.charAt(i) == '\n'){
                contentParsed += "  \n";
                Log.d("content", "newline detected");
            }else{
                contentParsed += markdown.charAt(i);
            }
        }
        markwon.setMarkdown(mainText, contentParsed);
    }

    public void sendFileString(String fileName, String fileSystem){
            Uri.Builder builder = Uri.parse(URL_STRING_REQ).buildUpon();
            builder.appendQueryParameter("fileName", fileName);
            builder.appendQueryParameter("content", content);
            builder.appendQueryParameter("json", fileSystem);
            builder.appendQueryParameter("username", username);
            builder.appendQueryParameter("password", password);
            String url = builder.build().toString();

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Volley Response", response);
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


    /*
    This stupid ass function takes a file, path, and the system of phone.
    It puts the file into the path, and the path is in the file system.
     */
    public JSONObject fileLocator(JSONObject fileSystem, JSONObject filePath, String fileName) throws JSONException {
        JSONArray pathArray = filePath.getJSONArray("path");
        JSONArray root = fileSystem.getJSONArray("root");

        // Traverse the file system using the provided path
        for (int i = 0; i < pathArray.length(); i++) {
            String key = pathArray.getString(i);
            for (int j = 0; j < root.length(); j++) {
                Object currentItem = root.get(j);
                if (currentItem instanceof JSONObject ) {
                    JSONObject currentItemJson = (JSONObject) currentItem;
                    if (currentItemJson.has(key)){
                        root = currentItemJson.getJSONArray(key);
                        break;
                    }
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
        if (index == -1){
            root.put(fileName);
        }
        Log.d("File System", fileSystem.toString());
        return fileSystem;

    }
}
