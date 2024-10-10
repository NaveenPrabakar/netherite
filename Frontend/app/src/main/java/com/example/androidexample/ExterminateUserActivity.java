package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ExterminateUserActivity extends AppCompatActivity {
    private Button backToMain;
    private Button exterminateUser;
    private EditText password;
    private EditText email;
    private TextView msgResponse;

    private static final String URL_JSON_OBJECT = "http://coms-3090-068.class.las.iastate.edu:8080/edit/exterminateUser";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exterminateuser);

        password = findViewById(R.id.password_edt);
        email = findViewById(R.id.email_edt);
        msgResponse = findViewById(R.id.err_msg);

        backToMain = findViewById(R.id.back2main);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate back to MainActivity
                Intent intent = new Intent(ExterminateUserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        exterminateUser = findViewById(R.id.kill_user);
        exterminateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_email = email.getText().toString();
                String user_password = password.getText().toString();

                killUser(user_email, user_password);
            }
        });
    }

    // find email and password and send to server to kill user.
    private void killUser(String email, String password) {

        // Create the request body as a JSON object
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", email);
            requestBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjPost = new JsonObjectRequest(
                Request.Method.DELETE,
                URL_JSON_OBJECT,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("KILL HIM! KILL HIM! KILL", response.toString());
                        //JSONObject resp = response;
                        try {
                            msgResponse.setText(response.getString("killed user"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Intent intent = new Intent(ExterminateUserActivity.this, SettingsActivity.class);
                        // Bring in extras ?
                        startActivity(intent);  // go to MainActivity with the key-value data
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("username", email);
                        Log.e("password", password);
                        Log.e("Volley Error", error.toString());
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjPost);
    };
}
