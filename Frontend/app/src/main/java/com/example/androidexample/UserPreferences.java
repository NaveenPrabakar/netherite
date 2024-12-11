package com.example.androidexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class UserPreferences {
    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FILESYSTEM = "filesystem";
    private static final String KEY_FILEPATH = "filepath";

    /*
    * OUR URLS
     */
    // Used in Main Activity
    private final String URL_SYSTEM_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/system";

    // Used in Files Activity
    private static final String URL_STRING_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/pull";
    private static final String URL_ID_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/fileid";
    private final String URL_DELETE_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/deleteFile";
    private final String URL_FOLDER_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/update";
    private final String URL_FRIEND_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/share/new";
    private static final String URL_WS = "ws://coms-3090-068.class.las.iastate.edu:8080/document/";
    private static final String URL_AIWS = "ws://coms-3090-068.class.las.iastate.edu:8080/chat/";

    // Used in OCR Activity
    private static String UPLOAD_URL = "http://coms-3090-068.class.las.iastate.edu:8080/extractText";

    // Settings:
    // Used in Change Email
    private static final String URL_CHANGE_EMAIL = "http://coms-3090-068.class.las.iastate.edu:8080/edit/changeemail/";
    // Change Password
    private static final String URL_CHANGE_PASSWORD = "http://coms-3090-068.class.las.iastate.edu:8080/edit/changepassword";
    // Change Username
    private static final String URL_CHANGE_USERNAME = "http://coms-3090-068.class.las.iastate.edu:8080/edit/changeusername/";
    // Enter Forget Password
    private static final String URL_SUBMIT_PASSWORD = "http://coms-3090-068.class.las.iastate.edu:8080/userLogin/resetPassword";
    // EXTERMINATE User
    private static final String URL_EXTERMINATE_USER = "http://coms-3090-068.class.las.iastate.edu:8080/edit/exterminateUser";
    // Forget Password
    private static final String URL_FORGET_PASSWORD = "http://coms-3090-068.class.las.iastate.edu:8080/userLogin/forgotPassword";
    // Verification
    private static final String URL_JSON_OBJECT = "http://coms-3090-068.class.las.iastate.edu:8080/userLogin/resetPassword";


    // Used in RecentFiles
    private static final String URL_RECENT_FILES = "http://coms-3090-068.class.las.iastate.edu:8080/recent"; // where the params are the email. i.e ../takuli@iastate.edu

    private static final String URL_TRANSLATE = "http://coms-3090-068.class.las.iastate.edu:8080/translateText/translate";


    public static void saveUserDetails(Context context, String username, String email, String password, String fileSystem, String filePath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_FILESYSTEM, fileSystem);
        editor.putString(KEY_FILEPATH, filePath);
        editor.apply();
        Log.d("UserPreferences", "User details saved:");
    }

    public void setFiles(Context context, String fileSystem, String filePath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FILEPATH, fileSystem);
        editor.putString(KEY_FILEPATH, filePath);
    }

    public static String getUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, "takuli");
    }

    public static String getEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, "takuli@iastate.edu");
    }

    public static String getPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PASSWORD, "admin123!");
    }

    public static String getFileSystem(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FILESYSTEM,  "{\"root\": [] }");
    }

    public static String getFilePath(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FILEPATH, "{\"path\": [\"root\"] }");
    }

    public static String getUrlRecentFiles() {
        return URL_RECENT_FILES;
    }

    public static String getFileID() {
        return URL_ID_REQ;
    }

    public static String getUrlWs() {
        return URL_WS;
    }
    public static String getUrlAIws() {
        return URL_AIWS;
    }

    public static String getUrlFilesPull() {
        return URL_STRING_REQ;
    }

    public static String getUrlTranslate()
    {return URL_TRANSLATE;}
}
