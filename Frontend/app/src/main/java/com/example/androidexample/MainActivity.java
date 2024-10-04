package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;
    private TextView messageText2;// define message textview variable
    private Button button1;
    private EditText editText;
    private String newString;
    private Button loginButt;
    private Button signupButt;
    private TextView usernameDisplay;
    private TextView passwordDisplay;
    private Button makeFile;
    private Button summarizeButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg);      // link to message textview in the Main activity XML
        messageText.setText("Hello World");

        usernameDisplay = findViewById(R.id.usernameDisplay);
        passwordDisplay = findViewById(R.id.passwordDisplay);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            String username = extras.getString("USERNAME");
            String password = extras.getString("PASSWORD");
            usernameDisplay.setText(username);
            passwordDisplay.setText(password);

        }

        loginButt = findViewById(R.id.loginButt);
        loginButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        makeFile = findViewById(R.id.makeFile);
        makeFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, TextActivity.class);
                startActivity(i);
            }
        });

        signupButt = findViewById(R.id.signupButt);
        signupButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        summarizeButt = findViewById(R.id.summarizeButt);
        summarizeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, SummarizeActivity.class);
                startActivity(i);
            }
        });



    }
}