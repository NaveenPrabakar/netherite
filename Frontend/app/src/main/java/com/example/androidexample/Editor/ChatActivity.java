package com.example.androidexample.Editor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidexample.NavigationBar;
import com.example.androidexample.R;
import com.example.androidexample.WebSockets.WebSocketListener;
import com.example.androidexample.WebSockets.WebSocketManager2;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents a live chat box associated with a note file that numerous users can interact with.
 * It keeps a chat history of previous messages sent and the user associated with the message.
 * @author Jamey Nguyen
 */
public class ChatActivity extends AppCompatActivity implements WebSocketListener {
    /**
     * Button to send the user's message.
     * Triggered when the user clicks to send the message from the {@link #messageBox}.
     */
    private Button sendButt;
    /**
     * Button to go back to the note file.
     */
    private Button backButt;

    /**
     * Text view to display the title of the note file.
     */
    private TextView headerTitle;
    /**
     * Text box to enter the user's message.
     */
    private EditText messageBox;
    /**
     * RecyclerView to display the chat history.
     * Populated by the {@link #getHistory(String)} method.
     */
    private RecyclerView chatHistory;
    /**
     * List of messages in the chat history.
     */
    private List<Message> messages;
    /**
     * WebSocket URL for the chat box.
     * It is connected to a websocket that allows the user to interact with an AI api.
     */
    private String aiUrl;
    /**
     * Custom populator for the chat history.
     * It takes in a list of {@link Message} objects, which are used to populate the chat history.
     */
    private MessagePopulator messagePopulator;

    /**
     * Called when the activity is first created.
     * It creates the user display, retrieves the WebSocket URL from the intent, and sets up the WebSocket connection.
     * @param savedInstanceState this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            if (extras.getString("AIWSURL") != null)
            {
                aiUrl = extras.getString("AIWSURL");
            }
        }

        NavigationBar navigationBar = new NavigationBar(this);
        navigationBar.addNavigationBar();

        WebSocketManager2.getInstance().connectWebSocket(aiUrl);
        WebSocketManager2.getInstance().setWebSocketListener(ChatActivity.this);


        /*
        * Maybe I can create a RecyclerView to display the messages?
         */
        chatHistory = findViewById(R.id.recyclerChatView);
        chatHistory.setLayoutManager(new LinearLayoutManager(this));
        sendButt = findViewById(R.id.sendButt);
        backButt = findViewById(R.id.backButt);
        messageBox = findViewById(R.id.messageBox);

        //messages = new ArrayList<String>();

        sendButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                sendMessage(messageBox.getText().toString());
                messageBox.setText("");
            }
        });

        backButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChatActivity.this, TextActivity.class);
                startActivity(i);
            }
        });
    }

    /**
    * Sends message to the WebSocket Client.
    * @param content The message to send.
     */
    private void sendMessage(String content)
    {
        WebSocketManager2.getInstance().sendMessage(content);
    }

    /**
    * I want the chat box to be populated as soon as the user connects to the chat box
    * with the history of the chat up to that point.
    * It's used as a helper function to display the history onto the display.
    * @param history a JSON array of messages in format message and username
     */
    private void getHistory(String history)
    {
        if (!WebSocketManager2.getInstance().isConnected())
        {
            WebSocketManager2.getInstance().connectWebSocket(aiUrl);
        }
        Log.d("History HERE BITCH ASS", "Processed value: " + history);
        // history is an arrayJsonObject
//            JSONObject historyJson = new JSONObject(history);
//            String content = historyJson.getString("content");
        Gson gson = new Gson();

        Log.d("Type of History", history.getClass().toString());

        // Reflection Library
        Type classType = new TypeToken<List<Message>>() {
        }.getType();
        messages = gson.fromJson(history, classType);


        if (messages == null) {
            messages = new ArrayList<>();
        }

        /*
         * Populate the (RecyclerView) chatHistory with the list of messages, using 'MessagePopulator'.
         */
        runOnUiThread(() -> {
            messagePopulator = new MessagePopulator(messages, ChatActivity.this);
            chatHistory.setAdapter(messagePopulator);
        });
    }

    /**
    * Called when the WebSocket connection is opened.
    * @param handshakedata Information about the server handshake.
     */
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("History", "WebSocket connection opened");

//        if (WebSocketManager2.getInstance().isConnected())
//        {
//            try {
//                JSONObject message = new JSONObject(String.valueOf(handshakedata));
//                getHistory(message);
//            }
//            catch (JSONException e)
//            {
//                Log.e("History", e.getMessage());
//            }
//        }
    }

    /**
     * Called when a WebSocket message is received.
     * @param msg The received WebSocket message.
     */
    @Override
    public void onWebSocketMessage(String msg) {
        Log.d("How the fuck did i get here", "bruh");

        if (msg.contains("\"source\":\"sourceHISTORY\"")) {
            Log.d("Message Source Check", "Detected sourceHISTORY, skipping message processing");
            return;
        }

        String user = "jamey";
        String ctn = "";
        String source = "";

//        if (msg.contains("/AI:"))
//        {
//            source = "sourceAI";
//        }
//        else {
//            source = "sourceLIVE";
//        }

        String[] cover = msg.split(",");

        for (String c : cover){
            if(c.contains("source")){
                String[] temp = c.split(":");
                source = temp[1];
            }
            else if(c.contains("username")){
                String[] temp = c.split(":");
                user = temp[1];
            }
            else if(c.contains("content")){
                String[] temp = c.split(":");
                ctn = temp[1];

            }
            else{
                ctn += c;
            }
        }

//        Message m = new Message(source, ctn, user);
//        MessagePopulator pop = new MessagePopulator(m, ChatActivity.this);
//        chatHistory.setAdapter(pop);
    }

    /**
     * Called when a JSON WebSocket message is received.
     * @param jsonMessage The received WebSocket message as a JSON object.
     */
    @Override
    public void onWebSocketJsonMessage(JSONObject jsonMessage) {
        Log.d("YES, I SHOULD BE PARSING HISTORY", "HELL YEA");

        if (!WebSocketManager2.getInstance().isConnected())
        {
            Log.d("Websocket Connection", "Not Connected");
        }

        try {
            String source = jsonMessage.getString("source");
            String ctn = jsonMessage.getString("content");
            String user = jsonMessage.getString("username");

            Log.d("The source of the message on connection", source);
            Log.d("The content of the message on connection", ctn);
            if (source.equals("sourceHISTORY"))
            {
                Log.d("I am calling history", ctn);
                getHistory(ctn);
            }
            else
            {
                // RECENT CHANGES
                Message m = new Message(source, ctn, user);

                // THIS ADD MESSAGE IS SCUFFED!
                messagePopulator.addMessage(m);
                //messages.add(m);
                //MessagePopulator pop = new MessagePopulator(messages, ChatActivity.this);
                Log.d("MOHTERFUCKER YOU BETTER UPDATE", "updated");

                // Recent change: 11/8
                //chatHistory.swapAdapter(messagePopulator, true);
                chatHistory.setAdapter(messagePopulator);
            }

            // SUS !! DONTT OUCH
//            chatHistory.setAdapter(pop);


        } catch (JSONException e) {
            Log.e("History", "Failed to process JSON: " + e.getMessage());
        }
    }

    /**
     * Called when the WebSocket connection is closed.
     * @param code   The status code indicating the reason for closure.
     * @param reason A human-readable explanation for the closure.
     * @param remote Indicates whether the closure was initiated by the remote endpoint.
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {

    }

    /**
     * Called when an error occurs in the WebSocket communication.
     * @param ex The exception that describes the error.
     */
    @Override
    public void onWebSocketError(Exception ex) {

    }
}
