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
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.noties.markwon.Markwon;

public class TextActivity extends AppCompatActivity implements WebSocketListener {
    private final String URL_STRING_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/upload";
    private final String URL_AI_GET = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/getUsageAPICount/";
    private final String URL_AI_POST = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/createAIUser";
    private final String URL_AI_DELETE = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/resetUsage/"; // PUT IN A PATH VARIABLE
    private final String URL_AI_PUT = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/updateAIUser";
    private Button back2main;
    private Button saveButt;
    private Button summarizeButt;
    private Button acceptButt;
    private Button rejectButt;
    private EditText mainText;
    private EditText editor;
    private EditText fileName;
    private TextView AIText;
    private Markwon markwon;
    private String content = " ";
    private JSONObject fileSystem;
    private JSONObject filePath;
    private String email;
    private String password;
    private String username;
    private String aiCount;
    private TextWatcher textWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        WebSocketManager.getInstance().setWebSocketListener(TextActivity.this);

        mainText = findViewById(R.id.textViewMarkdown);
        AIText = findViewById(R.id.AITextView);
        editor = findViewById(R.id.EditMarkdown);
        fileName = findViewById(R.id.fileName);

        markwon = Markwon.create(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                content = charSequence.toString();
                WebSocketManager.getInstance().sendMessage(updateParsedOutput(content));
                Log.d("Text changed", content);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };

        editor.addTextChangedListener(textWatcher);
        editor.setAlpha(0f);

        if(extras != null) {
            try {
                fileSystem = new JSONObject(extras.getString("FILESYSTEM"));
                filePath = new JSONObject(extras.getString("PATH"));
                email = extras.getString("EMAIL");
                password = extras.getString("PASSWORD");
                username = extras.getString("USERNAME");
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
                TESTsummarizeString(Request.Method.GET, content, email, "summarize", URL_AI_GET);
                acceptButt.setVisibility(View.VISIBLE);
                rejectButt.setVisibility(View.VISIBLE);
                summarizeButt.setVisibility(View.INVISIBLE);
            }
        });

        acceptButt = findViewById(R.id.acceptButt);
        acceptButt.setVisibility(View.INVISIBLE);
        acceptButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                acceptButt.setVisibility(View.INVISIBLE);
                rejectButt.setVisibility(View.INVISIBLE);
                summarizeButt.setVisibility(View.VISIBLE);
                //markwon.setMarkdown(mainText, mainText.getText().toString() + "\nAI Response: " + AIText.getText().toString());
//                mainText.append("\nAI Response: " + AIText.getText());
//                content += "\nAI Response: " + AIText.getText().toString();
                editor.append("  \n  \n ---  \nAI Response: " + AIText.getText() + "  \n  \n ---  \n");
                AIText.setText("");
            }
        });

        rejectButt = findViewById(R.id.rejectButt);
        rejectButt.setVisibility(View.INVISIBLE);
        rejectButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TESTsummarizeString(Request.Method.DELETE, content, email, "reject", URL_AI_DELETE);
                acceptButt.setVisibility(View.INVISIBLE);
                rejectButt.setVisibility(View.INVISIBLE);
                summarizeButt.setVisibility(View.VISIBLE);
                AIText.setText("");
            }
        });
    }

    private String updateParsedOutput(String markdown) {
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
        return contentParsed;
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

    private void TESTsummarizeString(int method, String contentToSummarize, String email, String prompt, String URL)
    {
        //Log.d("AICOUNT", aiCount);
        JsonObjectRequest summarizePost = new JsonObjectRequest (
                method,
                URL + email,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("AI TEXT SHOULD LOOK LIKE", response.toString());
                        if (method == Request.Method.GET) {
                            try {
                                aiCount = response.getString("reply");
                                if (aiCount.equals("-1")) {
                                    summarizeString(Request.Method.POST, contentToSummarize, email, prompt, URL_AI_POST);
                                } else {
                                    summarizeString(Request.Method.PUT, contentToSummarize, email, prompt, URL_AI_PUT);
                                }
                            } catch (JSONException e) {
                                //AIText.setText("Error: " + e.toString());
                                e.printStackTrace();
                            }
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
                        //AIText.setText("Error: " + error.toString());
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
                            //AIText.setText("Error: " + e.toString());
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
                        //AIText.setText("Error: " + error.toString());
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

    public int getCorrectCursorLocation(String before, String after, int cursorPos){
        int lenBefore = before.length()-1;
        int lenAfter = after.length()-1;

        Boolean isAddition = false;

        if (lenBefore == lenAfter){
            return cursorPos;
        }

        // Find the first position where the two strings differ
        int minLen = Math.min(lenBefore, lenAfter);
        int diffIndex = minLen; // Default to end if no early difference is found

        // Loop to find the first differing index
        for (int i = 0; i < minLen; i++) {
            char beforeC = before.charAt(i);
            char afterC = after.charAt(i);
            if (before.charAt(i) != after.charAt(i)) {
                diffIndex = i;
                break;
            }
        }

        // Determine what was added or removed
        String changeType;
        String diffChars;
        if (lenAfter > lenBefore) {
            isAddition = true;
        } else if (lenAfter < lenBefore) {
            isAddition = false;
        }

        int lenChanged = lenAfter - lenBefore;

        if (isAddition) {
            // If the change is an addition
            if (diffIndex > cursorPos) {
                // Do nothing if the addition is after the cursor
                return cursorPos;
            } else if (diffIndex < cursorPos) {
                // Increment the cursor if the addition is before the cursor
                return cursorPos + lenChanged;
            } else {
                // Do nothing if the addition is at the cursor
                return cursorPos;
            }
        } else {
            // If the change is a deletion
            if (diffIndex > cursorPos) {
                // Do nothing if the deletion is after the cursor
                return cursorPos;
            } else if (diffIndex < cursorPos) {
                // Decrement the cursor by the length of the removed part
                return cursorPos - lenChanged;
            } else {
                // Decrement by 1 if the deletion is at the cursor
                return cursorPos + 1;
            }
        }


    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected");
    }

    @Override
    public void onWebSocketMessage(String message) {
        Log.d("WebSocket", "Received message: " + message);
        String messagePar = message.substring(9, message.length());
        editor.removeTextChangedListener(textWatcher);
        int newCursorPosition = Math.max(getCorrectCursorLocation(content, messagePar, editor.getSelectionStart()), 0);
        editor.setText(messagePar);
        editor.setSelection(newCursorPosition);
        updateParsedOutput(messagePar);
        editor.addTextChangedListener(textWatcher);
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Closed");
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error", ex);
    }
}
