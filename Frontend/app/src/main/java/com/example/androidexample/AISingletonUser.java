package com.example.androidexample;

import android.content.Context;



public class AISingletonUser {
    private static AISingletonUser instance;
    private String email;
    private String password;
    private static Context context;

    private AISingletonUser(Context ctx) {
        context = ctx;
    }

    public static AISingletonUser getInstance(Context ctx) {
        if (instance == null) {
            instance = new AISingletonUser(ctx);
        }
        return instance;
    }

    public void broadcast(String message)
    {

    }
}
