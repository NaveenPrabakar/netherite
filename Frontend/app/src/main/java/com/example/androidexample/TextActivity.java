package com.example.androidexample;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.Volley;

import org.commonmark.node.Node;
import org.json.JSONException;
import org.json.JSONObject;

import io.noties.markwon.Markwon;

public class TextActivity extends AppCompatActivity {
    private final String URL_STRING_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/upload";
    private final String URL_AI_GET = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/getUsageAPICount/";
    private final String URL_AI_POST = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/createAIUser";
    private final String URL_AI_DELETE = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/resetUsage/"; // PUT IN A PATH VARIABLE
    private final String URL_AI_PUT = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/updateAIUser";
    private Button back2main;
    private Button saveButt;
    private Button summarizeButt;
    private EditText mainText;
    private Button editButton;
    private EditText editor;
    private EditText fileName;
    private TextView AIText;
    private Markwon markwon;
    private String content = "";
    private JSONObject fileSystem;
    private JSONObject filePath;
    private String email;
    private String password;
    private String aiCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);


        mainText = findViewById(R.id.textViewMarkdown);
        AIText = findViewById(R.id.AITextView);
        editor = findViewById(R.id.EditMarkdown);
        fileName = findViewById(R.id.fileName);
        editButton = findViewById(R.id.editButton);

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
                email = extras.getString("EMAIL");
                password = extras.getString("PASSWORD");
                Log.d("EMAIL", extras.getString("EMAIL"));
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
                intent.putExtra("FILESYSTEM", fileSystem.toString());
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
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

        summarizeButt = findViewById(R.id.summarizeButt);
        summarizeButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TESTsummarizeString(content, email, "summarize", URL_AI_GET);
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
            builder.appendQueryParameter("email", email);
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
                            Log.e("Email", email);

                            Log.e("Volley Error", error.toString());
                        }
                    }
            );

            // Adding request to request queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void TESTsummarizeString(String contentToSummarize, String email, String prompt, String URL)
    {

        JsonObjectRequest summarizePost = new JsonObjectRequest (
                Request.Method.GET,
                URL + email,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("AI TEXT SHOULD LOOK LIKE", response.toString());
                        try
                        {
                            aiCount = response.getString("reply");
                            if (aiCount.equals("-1"))
                            {
                                summarizeString(Request.Method.POST, contentToSummarize, email, prompt, URL_AI_POST);
                            }
                            else
                            {
                                summarizeString(Request.Method.PUT, contentToSummarize, email, prompt, URL_AI_PUT);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Log.e("Email", email);
                        Log.e("Content", contentToSummarize);
                        Log.e("Prompt", prompt);
                    }
                }
        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                //headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                //headers.put("Content-Type", "application/json");
//                return headers;
//            }

            // This function?? i don't remember what the fuck it does
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("prompt", prompt);
                params.put("content", contentToSummarize);
                return params;
            }
        };

        // Add the string request to the Volley Queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(summarizePost);

    }

    private void summarizeString(int method, String contentToSummarize, String email, String prompt, String URL)
    {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("prompt", prompt);
            requestBody.put("content", contentToSummarize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest summarizePost = new JsonObjectRequest (
                method,
                URL,
                requestBody, // Pass body because its a post request
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("AI TEXT SHOULD LOOK LIKE", response.toString());
                        try
                        {
                            AIText.setText(response.getString("reply"));
                            aiCount = response.getString("count");
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Log.e("Email", email);
                        Log.e("Content", contentToSummarize);
                        Log.e("Prompt", prompt);
                    }
                }
        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                //headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                //headers.put("Content-Type", "application/json");
//                return headers;
//            }

            // This function?? i don't remember what the fuck it does
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("prompt", prompt);
                params.put("content", contentToSummarize);
                return params;
            }
        };

        // Add the string request to the Volley Queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(summarizePost);
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
