package com.example.androidexample;

import android.content.Context;
import android.text.Editable;
import android.util.Log;


public class AISingletonUser {
    private static AISingletonUser instance;
    private String email;
    private String password;
    private static Context context;

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
        WebSocketManager.getInstance().sendMessage(content);
        Log.d("AI Message", content);
    }
}
