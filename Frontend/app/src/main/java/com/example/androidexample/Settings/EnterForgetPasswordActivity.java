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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EnterForgetPasswordActivity extends AppCompatActivity {

    // Button takes you back home
    private Button backToMain;
    private Button changePassword;
    private TextView email;
    private EditText n_password;
    private TextView msgResponse;
    private String new_password;

    private static final String URL_JSON_OBJECT = "http://coms-3090-068.class.las.iastate.edu:8080/userLogin/resetPassword";

    /**
     * Initializes the activity and sets up the UI components for entering a new password.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout. activity_enter_forget_password);

        email = findViewById(R.id.login_email_edt);
        n_password = findViewById(R.id.new_password_edt);
        msgResponse = findViewById(R.id.err_msg);

        Intent intent = getIntent();
        if (intent != null) {
            String emailIntent = intent.getStringExtra("EMAIL");
            Log.d("Email", intent.getStringExtra("EMAIL"));
            email.setText(emailIntent);

        }

        backToMain = findViewById(R.id.back2main);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate back to MainActivity
                Intent intent = new Intent(EnterForgetPasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        changePassword = findViewById(R.id.change_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Password", n_password.getText().toString());
                makeJsonObjPost(email.getText().toString(),  n_password.getText().toString());
            }
        });
    }

    /**
     * Sends a PUT request to the server to reset the user's password.
     *
     * @param email        the email address associated with the user's account
     * @param new_password the new password to update to
     */
    private void makeJsonObjPost(String email, String new_password) {

        // Create the request body as a JSON object
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", new_password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjPost = new JsonObjectRequest(
                Request.Method.PUT,
                URL_JSON_OBJECT,
                requestBody, // Pass body because its a post request
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject resp = response;
                        Log.d("Change Password", resp.toString());
                        try{
                            if (resp.has("responses")){
                                Intent intent = new Intent(EnterForgetPasswordActivity.this, MainActivity.class);
                                intent.putExtra("EMAIL", email);
                                intent.putExtra("PASSWORD", new_password);
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
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjPost);
    };
}