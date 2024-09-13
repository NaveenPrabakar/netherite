package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    Button b1;
    private Button counterBut;
    private Button loginBut;
    private Button signupBut;
    private Button portfolioBut;
    private int number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML
        messageText = findViewById(R.id.main_msg_txt);     // link to message textview in the Main activity XML
        /* initialize UI elements */
        /*b1 = (Button)findViewById(R.id.but1);*/
        counterBut = findViewById(R.id.counterButton);
        loginBut = findViewById(R.id.loginButton);
        signupBut = findViewById(R.id.SignupButton);
        portfolioBut = findViewById(R.id.portfolioButton);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            messageText.setText("Hello");
        } else {
            number = extras.getInt("NUM");  // this will come from LoginActivity
            messageText.setText("Hello " + number);
        }


        counterBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CounterActivity.class);
                intent.putExtra("COUNTER", Integer.valueOf(number));
                startActivity(intent);
            }
        });

        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        signupBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        portfolioBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PortfolioActivity.class);
                startActivity(i);
            }
        });

    }
    public void goToHome(View view) {
        TextView messageText = findViewById(R.id.main_msg_txt); // link to message textview in the Main activity XML
        EditText name = findViewById(R.id.userInputText); // Links to .xml file
        messageText.setText("Hello " + name.getText().toString());
    }

}