package com.example.androidexample.Settings;

import android.content.Intent;
import android.net.Uri;
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
import com.android.volley.toolbox.StringRequest;
import com.example.androidexample.FileView.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.Volleys.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class ExterminateUserActivity extends AppCompatActivity {
    private Button backToMain;
    private Button exterminateUser;
    private EditText password;
    private EditText email;
    private TextView msgResponse;

    private static final String URL_DELETE_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/edit/exterminateUser";

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

        Uri.Builder builder = Uri.parse(URL_DELETE_REQ).buildUpon();
        builder.appendQueryParameter("email", email);
        builder.appendQueryParameter("password", password);
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley Response", response);
                        try {
                            JSONObject jsObj = new JSONObject(response);
                            if (jsObj.getBoolean("success")) {
                                Intent intent = new Intent(ExterminateUserActivity.this, MainActivity.class);
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
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                    }
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    };
}
