package com.example.androidexample.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidexample.FileView.MainActivity;
import com.example.androidexample.R;

public class VerificationActivity extends AppCompatActivity {

    // Button takes you back home
    private Button backToMain;
    private Button verificationButton;
    private EditText o_password;
    private EditText verification;
    private EditText n_password;
    private TextView msgResponse;
    private String new_password;
    private String verificationCode;
    private String email;
    private static final String URL_JSON_OBJECT = "http://coms-3090-068.class.las.iastate.edu:8080/userLogin/resetPassword";

    /**
     * Initializes the activity and sets up the UI components for verifying the code.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        verification = findViewById(R.id.verification_code);
        msgResponse = findViewById(R.id.err_msg);

        Intent intent = getIntent();
        verificationCode = intent.getStringExtra("verificationCode");
        email = intent.getStringExtra("EMAIL");


        backToMain = findViewById(R.id.back2main);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate back to MainActivity
                Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                intent.putExtra("EMAIL", email);
                startActivity(intent);
            }
        });

        verificationButton = findViewById(R.id.verification);
        verificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Verification", verification.getText().toString());
                if(verification.getText().toString().equals(verificationCode)){
                    Intent intent = new Intent(VerificationActivity.this, EnterForgetPasswordActivity.class);
                    intent.putExtra("EMAIL", email);
                    startActivity(intent);
                }else{
                    msgResponse.setText("Invalid verification code");
                };
            }
        });
    }

    // Update the password. Put email, old_password in the body, and new_password to change.

}