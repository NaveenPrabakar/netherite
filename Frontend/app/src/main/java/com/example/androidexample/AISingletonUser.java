package com.example.androidexample;

import android.content.Context;
import android.text.Editable;
import android.util.Log;

/*
* There should only be one AI user so I created a single instance of it at any time.
* This class also calls the websocketmanager to send messages to the server and it basically
* just mimics a human user.
 */
public class AISingletonUser {
    private static AISingletonUser instance;
    private String email;
    private String password;
    private static Context context;
    private final String aiURL = "ws://coms-3090-068.class.las.iastate.edu:8080/chat/";

    // Link is /chat/ {username}

    private AISingletonUser(Context ctx) {
        context = ctx;
    }

    public static AISingletonUser getInstance(Context ctx) {
        if (instance == null) {
            instance = new AISingletonUser(ctx);
        }
        return instance;
    }

    public void AIMessage(String content)
    {
        WebSocketManager2.getInstance().sendMessage(content);
        Log.d("AI Message", content);
    }
}
