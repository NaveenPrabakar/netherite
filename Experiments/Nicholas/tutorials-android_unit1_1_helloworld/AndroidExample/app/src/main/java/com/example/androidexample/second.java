package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class second extends AppCompatActivity {

    private TextView messageText;
    private TextView messageText2;// define message textview variable
    private Button button1;
    private EditText editText;
    private String newString;
    private Button switchbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg);      // link to message textview in the Main activity XML
        messageText.setText("Hello Intent");
        messageText2 = findViewById(R.id.textView2);


        editText = findViewById(R.id.editTextText);

        button1 = findViewById(R.id.big_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newString = editText.getText().toString();
                messageText2.setText("Hello "+ newString);
            }
        });

        switchbutton = findViewById(R.id.switchbutton);
        switchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(second.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}