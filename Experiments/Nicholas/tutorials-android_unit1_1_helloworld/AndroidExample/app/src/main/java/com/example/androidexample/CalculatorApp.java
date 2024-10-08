package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorApp extends AppCompatActivity {

    private TextView messageText;
    private TextView calc;
    private Button nine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg3);      // link to message textview in the Main activity XML
        messageText.setText("Hello Calculator");
        calc = findViewById(R.id.calc);

        nine = findViewById(R.id.nine);
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calc.setText('9');
            }
        });




    }
}