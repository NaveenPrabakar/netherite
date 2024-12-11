package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.androidexample.FileView.MainActivity;
import com.example.androidexample.FileView.filesActivity;
import com.example.androidexample.Settings.ForgetPasswordActivity;
import com.example.androidexample.Volleys.VolleySingleton;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;
    private MaterialButton signupButton;
    private MaterialButton forgetPasswordButton;
    private ProgressBar loadingSpinner;



    private static final String URL_JSON_OBJECT = "http://coms-3090-068.class.las.iastate.edu:8080/userLogin/searchemail";
    private final String URL_STRING_REQ = "http://coms-3090-068.class.las.iastate.edu:8080/files/system";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);            // link to Login activity XML

        // Initialize UI components
        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        signupButton = findViewById(R.id.login_signup_btn);
        forgetPasswordButton = findViewById(R.id.forget_password);
        loadingSpinner = findViewById(R.id.loading_spinner);

        TextView loginTitle = findViewById(R.id.loginTitle);
        ImageView appLogo = findViewById(R.id.appLogo);

        usernameEditText.setText("takuli@iastate.edu");
        passwordEditText.setText("admin123!");

        ObjectAnimator translateX = ObjectAnimator.ofFloat(appLogo, "translationX", 0f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(appLogo, "translationY", 0f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(loginTitle, "alpha", 0f, 1f);
//        fadeIn.setDuration(2000);
//        fadeIn.start();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateX, translateY, fadeIn);
        animatorSet.setDuration(3000);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet.start();

        Glide.with(this).asGif().load(R.raw.enchanted_netherite_leggings).into(appLogo);

        appLogo.setScaleX(0.0f);
        appLogo.setScaleY(0.0f);
        appLogo.animate().scaleX(1.0f).scaleY(1.0f).setDuration(1500).start();

        // Set up button click listeners
        loginButton.setOnClickListener(v -> attemptLogin());

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        forgetPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
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

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                URL_JSON_OBJECT,
                requestBody,
                response -> handleLoginResponse(response),
                error -> handleLoginError(error)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return new HashMap<>();
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    private void attemptLogin() {
        // Get user input
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading spinner
        loadingSpinner.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Make network request
        makeJsonObjReq(email, password);
    }

    private void handleLoginResponse(JSONObject response) {
        loadingSpinner.setVisibility(View.GONE);
        loginButton.setEnabled(true);

        try {
            String serverResponse = response.getString("response");
            if ("ok".equals(serverResponse)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                UserPreferences.saveUserDetails(LoginActivity.this, response.getString("userName"), usernameEditText.getText().toString(), passwordEditText.getText().toString(), "", "");
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLoginError(VolleyError error) {
        loadingSpinner.setVisibility(View.GONE);
        loginButton.setEnabled(true);

        Log.e("LoginError", error.toString());
        Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
    }
}
