package com.example.androidexample.Editor;
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

import com.example.androidexample.FileView.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.VolleySingleton;
import com.example.androidexample.WebSockets.WebSocketListener;
import com.example.androidexample.WebSockets.WebSocketManager;

import io.noties.markwon.Markwon;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;



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
    private Button liveChatButt;
    private Button rejectButt;
    private EditText mainText;
    private EditText editor;
    private EditText fileName;
    private EditText AIInputText;
    private TextView AIText;
    private Markwon markwon;
    private String content = " ";
    private JSONObject fileSystem;
    private JSONObject filePath;
    private String email;
    private String password;
    private String username;
    private String aiCount;
    private Button voiceButt;
    private TextWatcher textWatcher;
    private String source;
    private String history = "";
    private String aiURL;
    BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);


        WebSocketManager.getInstance().setWebSocketListener(TextActivity.this);

        mainText = findViewById(R.id.textViewMarkdown);
        AIText = findViewById(R.id.AITextView);
        //AIText.setVisibility(View.INVISIBLE);
        editor = findViewById(R.id.EditMarkdown);
        fileName = findViewById(R.id.fileName);
        liveChatButt = findViewById(R.id.liveChatButt);
        AIInputText = findViewById(R.id.AIChatBar);
        voiceButt = findViewById(R.id.voiceButt);


        saveButt = findViewById(R.id.saveButt);
        summarizeButt = findViewById(R.id.summarizeButt);
        acceptButt = findViewById(R.id.acceptButt);
        acceptButt.setVisibility(View.INVISIBLE);

        rejectButt = findViewById(R.id.rejectButt);
        rejectButt.setVisibility(View.INVISIBLE);

        try {
            fileSystem = new JSONObject(UserPreferences.getFileSystem(this));
            filePath = new JSONObject(UserPreferences.getFilePath(this));
            email = UserPreferences.getEmail(this);
            password = UserPreferences.getPassword(this);
            username = UserPreferences.getUsername(this);
            Log.d("File System", fileSystem.toString());
            Log.d("File Path", filePath.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // This is the live chat button
        liveChatButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TextActivity.this, ChatActivity.class);
                i.putExtra("AIWSURL", aiURL);
                startActivity(i);
            }
        });


        markwon = Markwon.create(this);



        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                content = charSequence.toString();
                updateParsedOutput(content);
                Log.d("Text changed", content);
                WebSocketManager.getInstance().sendMessage(content);

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        editor.addTextChangedListener(textWatcher);
        mainText.setAlpha(0f);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null) {
            if (extras.getString("CONTENT") != null){
                Log.d("content", extras.getString("CONTENT"));
                editor.setText(extras.getString("CONTENT"));
                content = extras.getString("CONTENT");
            }
            if (extras.getString("FILEKEY") != null){
                Log.d("filekey", extras.getString("FILEKEY"));
                fileName.setText(extras.getString("FILEKEY"));
            }
            if (extras.getString("RECORDED") != null){
                Log.d("recorded", extras.getString("RECORDED"));
                editor.setText(content+ "   \n   \n" + extras.getString("RECORDED"));
            }
            if (extras.getString("IMAGETEXT") != null)
            {
                AIText.setText(extras.getString("IMAGETEXT"));
                //mainText.setText(extras.getString("IMAGETEXT"));
                acceptButt.setVisibility(View.VISIBLE);
                rejectButt.setVisibility(View.VISIBLE);
                summarizeButt.setVisibility(View.INVISIBLE);
            }
            if (extras.getString("AIWSURL") != null)
            {
                aiURL = extras.getString("AIWSURL");
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
                intent.putExtra("USERNAME", username);
                intent.putExtra("PASSWORD", password);
                startActivity(intent);  // go to SignupActivity
            }
        });


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


        summarizeButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TESTsummarizeString(Request.Method.GET, content, email, "summarize", URL_AI_GET);
                acceptButt.setVisibility(View.VISIBLE);
                rejectButt.setVisibility(View.VISIBLE);
                AIText.setVisibility(View.VISIBLE);
                summarizeButt.setVisibility(View.INVISIBLE);
                voiceButt.setVisibility(View.INVISIBLE);


            }
        });


        acceptButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                acceptButt.setVisibility(View.INVISIBLE);
                rejectButt.setVisibility(View.INVISIBLE);
                AIText.setVisibility(View.INVISIBLE);
                summarizeButt.setVisibility(View.VISIBLE);
                voiceButt.setVisibility(View.VISIBLE);
                //markwon.setMarkdown(mainText, mainText.getText().toString() + "\nAI Response: " + AIText.getText().toString());
//                mainText.append("\nAI Response: " + AIText.getText());
//                content += "\nAI Response: " + AIText.getText().toString();
                editor.append("  \n  \n ---  \nAI Response: " + AIText.getText() + "  \n  \n ---  \n");
                AIText.setText("");
            }
        });


        rejectButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TESTsummarizeString(Request.Method.DELETE, content, email, "reject", URL_AI_DELETE);
                acceptButt.setVisibility(View.INVISIBLE);
                rejectButt.setVisibility(View.INVISIBLE);
                summarizeButt.setVisibility(View.VISIBLE);
                voiceButt.setVisibility(View.VISIBLE);
                AIText.setText("");
            }
        });

        voiceButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(TextActivity.this, VoiceRecordActivity.class);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                intent.putExtra("USERNAME", username);
                intent.putExtra("FILESYSTEM", fileSystem.toString());
                intent.putExtra("PATH", filePath.toString());
                intent.putExtra("CONTENT", content);
                startActivity(intent);
            }
        });

    }

    /**
     * Processes a Markdown string by replacing newline characters with Markdown line breaks
     * and updates the parsed content to be rendered using Markwon.
     *
     * @param markdown the input Markdown text to be processed
     * @return the parsed content with newline characters replaced by Markdown line breaks
     */
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

    /**
     * Sends a file string to a remote server using a POST request. Constructs a URL with query parameters
     * including the file name, content, file system, email, and password, and uses the Volley library to
     * send the request.
     *
     * @param fileName   the name of the file being sent
     * @param fileSystem the file system as a JSON string
     */
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

    /**
     * Sends a request to summarize content using AI. Based on the method (GET, POST, PUT),
     * it decides how to interact with the server.
     *
     * @param method             the HTTP method to use (e.g., GET, POST, PUT)
     * @param contentToSummarize the content that needs to be summarized
     * @param email              the email identifier for the request
     * @param prompt             the prompt or query for the AI summarization
     * @param URL                the base URL for the request
     */

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

    /**
     * Sends a POST or PUT request to summarize content using AI.
     *
     * @param method             the HTTP method to use (e.g., POST, PUT)
     * @param contentToSummarize the content that needs to be summarized
     * @param email              the email identifier for the request
     * @param prompt             the prompt or query for the AI summarization
     * @param URL                the base URL for the request
     */

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

    /**
     * Locates or adds a file in a hierarchical file system based on the given file path.
     *
     * @param fileSystem the root JSON object representing the file system
     * @param filePath   the JSON object containing the "path" array, representing the file's location
     * @param fileName   the name of the file to locate or add in the file system
     * @return the updated JSON object representing the file system
     * @throws JSONException if there is an error parsing the JSON objects
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

    /**
     * Calculates the correct cursor position after changes are made to a text string.
     * This assumes that all changes occur either before or after the cursor, not both simultaneously.
     *
     * @param before    the original text before the change
     * @param after     the modified text after the change
     * @param cursorPos the initial cursor position
     * @return the updated cursor position after the change
     */

    public int getCorrectCursorLocation(String before, String after, int cursorPos){
        /*
        This method runs assuming the all the changes happens before or after the cursor
        not both at the same time.
        Because the each user have 1 cursor, they can only update in 1 place each broadcast
        Therefore it is impossible to have a change be both before and after the cursor
         */
        int lenBefore = before.length();
        int lenAfter = after.length();
        // Find the first position where the two strings differ
        int minLen = Math.min(lenBefore, lenAfter);
        int diffIndex = minLen; // Default to end if no early difference is found

        /*
        Find the differing index, and then find how much it differs
        if the lenChanged is positive, that means there is an addition to the text,
        and if it is negative then there is a deletion

        in an Addition
        If the different index (first occurance of a change) is after the cursor,
        we dont change the cursor location
        if it is before the cursor,
        we add to the cursor the length of the change
        if it is equal (we add to where the cursor is) we do nothing, to prevent the user's
        cursor to be changed by external input

        in a Deletion
        If the different index (first occurance of a change) is after the cursor,
        we dont change the cursor location
        if it is before the cursor,
        we subtract from the cursor the length of the change
        if it is equal we (we delete to the cursor where the user is adding)
        we subtract from the cursor the length of the change

        In short the only thing that matters is if we add before the cursor,
        and if we delete on the cursor and before the cursor

         */
        if (lenBefore != lenAfter){
            // Loop to find the first differing index
            for (int i = 0; i < minLen; i++) {
                if (before.charAt(i) != after.charAt(i)) {
                    diffIndex = i;
                    break;
                }
            }
            int lenChanged = lenAfter - lenBefore;
            if (lenChanged > 0) {
                // If the change is an addition
                if (diffIndex < cursorPos) {
                    // Increment the cursor if the addition is before the cursor
                    return cursorPos + lenChanged;
                }
            } else if (lenChanged < 0) {
                // If the change is a deletion
                if (diffIndex <= cursorPos) {
                    // Decrement the cursor by the length of the removed part
                    return cursorPos + lenChanged;
                }
            }
        }
        return cursorPos;
    }

    /**
     * Handles the event when the WebSocket connection is successfully opened.
     *
     * @param handshakedata the handshake data from the server
     */

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Connected");
    }

    /**
     * Handles the event when a string message is received over the WebSocket.
     *
     * @param message the string message received from the WebSocket
     */
    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(()->{
            Log.d("THREAD","Processing item: " + queue.size());
            int newCursorPosition = Math.max(getCorrectCursorLocation(content, message, editor.getSelectionStart()), 0);

            Log.d("WebSocket", "Received message: " + message);
            editor.removeTextChangedListener(textWatcher);
            editor.setText(message);
            content = message;

            if (newCursorPosition <= editor.getText().length()) {
                editor.setSelection(newCursorPosition);
            }
            else { editor.setSelection(editor.getText().length());}
            updateParsedOutput(editor.getText().toString());

            editor.addTextChangedListener(textWatcher);
            Log.d("THREAD","Finished Processing item: " + queue.size());

        });
    }

    /**
     * Handles the event when a JSON message is received over the WebSocket.
     *
     * @param jsonMessage the JSON object received from the WebSocket
     */

    @Override
    public void onWebSocketJsonMessage(JSONObject jsonMessage) {
        Log.d("WebSocket", "JSON message: " + jsonMessage.toString());
        // At this point in time, I should have the JSON object
        // Lets try to actually parse the JSON object
        try {
            // Example: extract data from the JSON object
            source = jsonMessage.getString("source");
            String stuff = jsonMessage.getString("content");
            String user = jsonMessage.getString("username");
            history += stuff + ":" + user + " ";
            Log.d("History: ", history);
            Log.d("Source", source);

            AIText.setText(stuff);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the event when the WebSocket connection is closed.
     *
     * @param code   the closing code indicating why the WebSocket was closed
     * @param reason the reason for the WebSocket closure
     * @param remote indicates whether the closure was initiated by the remote peer
     */

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", "Closed");
    }

    /**
     * Handles the event when an error occurs on the WebSocket connection.
     *
     * @param ex the exception that occurred during the WebSocket operation
     */
    @Override
    public void onWebSocketError(Exception ex) {
        Log.e("WebSocket", "Error", ex);
    }

}
