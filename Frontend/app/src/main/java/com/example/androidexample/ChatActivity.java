package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private String message;
    private String username;
    private Button sendButt;
    private Button backButt;
    private EditText messageBox;
    private RecyclerView chatHistory;
    private List<Message> messages;
    private String aiUrl;

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
    }

    /*
    * Sends message to the WS Client. (intended behavior) very easy
     */
    private void sendMessage(String content)
    {
        WebSocketManager2.getInstance().sendMessage(content);
    }

    /*
    * I want the chat box to be populated as soon as the user connects to the chat box
    * with the history of the chat up to that point.
     */
    private void getHistory(String history)
    {
        Log.d("History HERE BITCH ASS", "Processed value: " + history);

        Gson gson = new Gson();

        // Reflection Library
        Type classType = new TypeToken<List<Message>>(){}.getType();
        messages = gson.fromJson(history, classType);

        /*
         * Populate the (RecyclerView) galleryView with the list of photos, using 'GalleryPopulator'.
         */

        MessagePopulator msg = new MessagePopulator(messages, ChatActivity.this);
        chatHistory.setAdapter(msg);

    }


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

    @Override
    public void onWebSocketMessage(String msg) {
        Log.d("How the fuck did i get here", "bruh");

        if (msg.contains("\"source\":\"sourceHISTORY\"")) {
            Log.d("Message Source Check", "Detected sourceHISTORY, skipping message processing");
            return;
        }

        String user = "";
        String ctn = "";
        String source = "";
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

    @Override
    public void onWebSocketJsonMessage(JSONObject jsonMessage) {
        Log.d("YES, I SHOULD BE PARSING HISTORY", "HELL YEA");

        try {
            String source = jsonMessage.getString("source");
            String ctn = jsonMessage.getString("content");
            Log.d("The source of the message on connection", source);
            Log.d("The content of the message on connection", ctn);
            if (source.equals("sourceHISTORY"))
            {
                Log.d("I am calling history", ctn);
                getHistory(ctn);
            }
        } catch (JSONException e) {
            Log.e("History", "Failed to process JSON: " + e.getMessage());
        }
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onWebSocketError(Exception ex) {

    }
}
