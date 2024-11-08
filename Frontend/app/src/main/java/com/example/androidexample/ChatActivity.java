package com.example.androidexample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.TextView;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private String message;
    private Button sendButt;
    private Button backButt;
    private RecyclerView chatHistory;
    private List<String> messages;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        /*
        * Maybe I can create a RecyclerView to display the messages?
         */
        chatHistory = findViewById(R.id.recyclerChatView);
        chatHistory.setLayoutManager(new LinearLayoutManager(this));
        sendButt = findViewById(R.id.sendButt);
        backButt = findViewById(R.id.backButt);

        messages = new ArrayList<String>();
    }

    private void sendMessage()
    {

    }

    private void getHistory()
    {

    }


    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onWebSocketMessage(String message) {

    }

    @Override
    public void onWebSocketJsonMessage(JSONObject jsonMessage) {

    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onWebSocketError(Exception ex) {

    }
}
