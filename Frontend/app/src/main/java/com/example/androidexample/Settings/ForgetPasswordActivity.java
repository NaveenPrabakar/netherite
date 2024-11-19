package com.example.androidexample.Settings;

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
import com.example.androidexample.FileView.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.Volleys.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordActivity extends AppCompatActivity {

    // Button takes you back home
    private Button backToMain;
    private Button changePassword;
    private EditText o_password;
    private EditText email;
    private EditText n_password;
    private TextView msgResponse;
    private String new_password;

    private static final String URL_JSON_OBJECT = "http://coms-3090-068.class.las.iastate.edu:8080/userLogin/forgotPassword";

    /**
     * Initializes the activity and sets up the UI components for the "Forgot Password" functionality.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = findViewById(R.id.login_email_edt);
        msgResponse = findViewById(R.id.err_msg);

        backToMain = findViewById(R.id.back2main);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate back to MainActivity
                Intent intent = new Intent(ForgetPasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        changePassword = findViewById(R.id.forget_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Email", email.getText().toString());
                makeJsonObjPost(email.getText().toString());
            }
        });
    }

    /**
     * Sends a POST request to the server to initiate the "Forgot Password" process.
     *
     * @param email the email address associated with the user's account
     */
    // Update the password. Put email, old_password in the body, and new_password to change.
    private void makeJsonObjPost(String email) {

        // Create the request body as a JSON object
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjPost = new JsonObjectRequest(
                Request.Method.POST,
                URL_JSON_OBJECT,
                requestBody, // Pass body because its a post request
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject resp = response;
                        Log.d("Change Password", resp.toString());
                        try{
                            if (resp.has("message")){
                                Intent intent = new Intent(ForgetPasswordActivity.this, VerificationActivity.class);
                                try {
                                    intent.putExtra("EMAIL", email);
                                    intent.putExtra("verificationCode", resp.getString("verificationCode"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                startActivity(intent);
                            }
                        }catch (Exception e){
                            Log.d("Error", e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        msgResponse.setText(error.toString());
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
                params.put("username", email);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjPost);
    };
}