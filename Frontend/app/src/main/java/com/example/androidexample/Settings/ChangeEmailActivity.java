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
import com.example.androidexample.NavigationBar;
import com.example.androidexample.R;
import com.example.androidexample.Volleys.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangeEmailActivity extends AppCompatActivity {
    private Button backToMain;
    private Button changeEmail;
    private EditText password;
    private EditText n_email;
    private EditText email;
    private TextView msgResponse;

    private static final String URL_JSON_OBJECT = "http://coms-3090-068.class.las.iastate.edu:8080/edit/changeemail/";

    /**
     * Initializes the activity and sets up the UI components for changing the email.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied; otherwise, it is null.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeemail);

        password = findViewById(R.id.password_edt);
        email = findViewById(R.id.email_edt);
        n_email = findViewById(R.id.new_email_edt);

        msgResponse = findViewById(R.id.err_msg);
        NavigationBar navigationBar = new NavigationBar(this);
        navigationBar.addNavigationBar(R.layout.activity_changeemail);

        backToMain = findViewById(R.id.back2main);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate back to MainActivity
                Intent intent = new Intent(ChangeEmailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        changeEmail = findViewById(R.id.change_email);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_email = email.getText().toString();
                String user_password = password.getText().toString();
                String new_email = n_email.getText().toString();

                makeJsonObjPost(user_email, user_password, new_email);
            }
        });
    }

    /**
     * Sends a PUT request to the server to update the email.
     *
     * @param email       the current email of the user
     * @param password    the user's password
     * @param new_email   the new email to update to
     */
    private void makeJsonObjPost(String email, String password, String new_email) {

        // Create the request body as a JSON object
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjPost = new JsonObjectRequest(
                Request.Method.PUT,
                URL_JSON_OBJECT + new_email,
                requestBody, // Pass body because its a post request
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Change Password", response.toString());
                        JSONObject resp = response;
                        try {
                            msgResponse.setText(resp.getString("response"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Intent intent = new Intent(ChangeEmailActivity.this, SettingsActivity.class);
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
                params.put("password", password);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjPost);
    };
}
