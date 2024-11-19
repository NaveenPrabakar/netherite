package com.example.androidexample.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidexample.FileView.MainActivity;
import com.example.androidexample.R;

public class SettingsActivity extends AppCompatActivity {

    // Button takes you back home
    private Button backToMain;
    private Button changePassword;
    private Button changeUsername;
    private Button changeEmail;
    private Button forgetPassword;
    private Button deleteUser;
    //private EditText username;
    //private EditText email;
    //private EditText password;
    private TextView msgResponse;

    /**
     * Initializes the activity and sets up the UI components for accessing account settings.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        msgResponse = findViewById(R.id.err_msg);

        backToMain = findViewById(R.id.back2main);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate back to MainActivity
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        changeUsername = findViewById(R.id.change_username);
        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to ChangeUsernameActivity
                Intent intent = new Intent(SettingsActivity.this, ChangeUsernameActivity.class);
                startActivity(intent);
            }
        });

        changePassword = findViewById(R.id.change_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to ChangePasswordActivity
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        changeEmail = findViewById(R.id.change_email);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to ChangeEmailActivity
                Intent intent = new Intent(SettingsActivity.this, ChangeEmailActivity.class);
                startActivity(intent);
            }
        });

        forgetPassword = findViewById(R.id.forget_password);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to ForgetPasswordActivity
                Intent intent = new Intent(SettingsActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        deleteUser = findViewById(R.id.delete_account);
        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to ExterminateUserActivity
                Intent intent = new Intent(SettingsActivity.this, ExterminateUserActivity.class);
                startActivity(intent);
            }
        });
    }
}
