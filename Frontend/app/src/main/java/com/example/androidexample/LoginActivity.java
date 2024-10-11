package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private Button loginButton;         // define login button variable
    private Button signupButton;
    private Button forgetPassword;
    private TextView err_msg;// define signup button variable
    private Button back2main;
    private Boolean ApiStatus;
    private static final String URL_JSON_OBJECT = "http://coms-3090-068.class.las.iastate.edu:8080/userLogin/searchemail";
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
        forgetPassword = findViewById(R.id.forget_password);
        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                /* when login button is pressed, use intent to switch to Login Activity */

                makeJsonObjReq(email, password);


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

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);  // go to SignupActivity
            }
        });
    }

    private void makeJsonObjReq(String email, String password) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjGet = new JsonObjectRequest(
                Request.Method.POST,
                URL_JSON_OBJECT,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject resp = response;
                        try {
                            Log.d("Volley Response", resp.getString("response"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        err_msg.setText(response.toString());
                        try {
                            if (resp.getString("response").equals("ok")){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("EMAIL", email);
                                intent.putExtra("PASSWORD", password);
                                intent.putExtra("FILESYSTEM", resp.toString());
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        err_msg.setText(error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("param1", "value1");
//                params.put("param2", "value2");
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjGet);
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
