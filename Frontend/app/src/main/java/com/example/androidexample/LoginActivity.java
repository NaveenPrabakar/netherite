package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private Button loginButton;         // define login button variable
    private Button signupButton;
    private TextView err_msg;// define signup button variable
    private Button back2main;
    private Boolean ApiStatus = false;
    private final String URL_STRING_REQ = "https://4efc3913-a738-4f74-b964-b0290a1b0fe9.mock.pstmn.io/login1/sucess";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);            // link to Login activity XML

        /* initialize UI elements */
        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        err_msg = findViewById(R.id.err_msg);

        loginButton = findViewById(R.id.login_login_btn);    // link to login button in the Login activity XML
        signupButton = findViewById(R.id.login_signup_btn);  // link to signup button in the Login activity XML

        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.indexOf('@') == -1){
                    err_msg.setText("Username must be an email");
                    return;
                }
                if (password.length() < 8){
                    err_msg.setText("Password must be at least 8 characters");
                    return;
                }
                if (password.indexOf('!') == -1){
                    err_msg.setText("Password must contain at least one '!' ");
                    return;
                }
                if (username.contains(".com") == false){
                    err_msg.setText("Password must be a valid email ");
                    return;
                }

                /* when login button is pressed, use intent to switch to Login Activity */
                makeStringReq(username, password);


            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);  // go to SignupActivity
            }
        });

        back2main = findViewById(R.id.back2main);
        back2main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);  // go to SignupActivity
            }
        });
    }

    private void makeStringReq(String username, String password) {
        Uri.Builder builder = Uri.parse("https://4efc3913-a738-4f74-b964-b0290a1b0fe9.mock.pstmn.io/login1/sucess").buildUpon();
        builder.appendQueryParameter("username", username);
        builder.appendQueryParameter("password", password);
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        HashMap<String, String> responseMap = jsonToMap(response);
                        Log.d("Volley Response", response);
                        for (String key : responseMap.keySet()) {
                            Log.d("Key", key);
                            Log.d("Value", responseMap.get(key));
                        }
                        ApiStatus=true;
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USERNAME", username);  // key-value to pass to the MainActivity
                        intent.putExtra("PASSWORD", password);  // key-value to pass to the MainActivity
                        startActivity(intent);  // go to MainActivity with the key-value data
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                        ApiStatus=false;
                        err_msg.setText("Failed to send request");
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public HashMap<String, String> jsonToMap(String jsonString) {
        HashMap<String, String> map = new HashMap<>();

        try {
            // Convert JSON string to JSONObject
            JSONObject jsonObject = new JSONObject(jsonString);

            // Get the keys of the JSONObject
            Iterator<String> keys = jsonObject.keys();

            // Loop through the keys and put key-value pairs into the HashMap
            while (keys.hasNext()) {
                String key = keys.next();
                String value = jsonObject.getString(key);
                map.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();  // Handle the exception
        }

        return map;
    }

}
