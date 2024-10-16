package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    // Button takes you back home
    private Button backToMain;
    private Button changePassword;
    private EditText o_password;
    private EditText email;
    private EditText n_password;
    private TextView msgResponse;
    private String new_password;

    private static final String URL_JSON_OBJECT = "http://coms-3090-068.class.las.iastate.edu:8080/edit/changepassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        o_password = findViewById(R.id.old_password_edt);
        email = findViewById(R.id.login_email_edt);
        n_password = findViewById(R.id.new_password_edt);
        msgResponse = findViewById(R.id.err_msg);

//        Intent intent = getIntent();
//        if (intent != null) {
//            String emailIntent = intent.getStringExtra("EMAIL");
//            //Log.d("Email", intent.getStringExtra("EMAIL"));
//            email.setText(emailIntent);
//        }

        backToMain = findViewById(R.id.back2main);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate back to MainActivity
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        changePassword = findViewById(R.id.change_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_email = email.getText().toString();
                new_password = n_password.getText().toString();
                String old_password = o_password.getText().toString();

                Log.e("Password", old_password);
                makeJsonObjPost(user_email, old_password, new_password);
            }
        });
    }

    // Update the password. Put email, old_password in the body, and new_password to change.
    private void makeJsonObjPost(String email, String old_password, String new_password) {

        // Create the request body as a JSON object
        JSONObject requestBody = new JSONObject();
        try {
            //Log.e("CHECK HERRE PIECE OF SHIELDPassword", old_password);
            requestBody.put("email", email);
            requestBody.put("password", old_password);
            //requestBody.put("newPassword", new_password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjPost = new JsonObjectRequest(
                Request.Method.PUT,
                "http://coms-3090-068.class.las.iastate.edu:8080/edit/changepassword/" + new_password,
                requestBody, // Pass body because its a post request
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Change Password", response.toString());
                        JSONObject resp = response;
                        try {
                            msgResponse.setText(resp.getString("response"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                        // Bring in extras ?
                        startActivity(intent);  // go to MainActivity with the key-value data
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", old_password);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjPost);
    };
}