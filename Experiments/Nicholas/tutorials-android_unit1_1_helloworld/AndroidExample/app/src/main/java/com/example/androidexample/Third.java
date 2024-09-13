package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Third extends AppCompatActivity {

    private Button switchbutton;
    private TextView messageText;
    private String newString;
    private TextView calculate;
    private Double operand1 = 0.0;
    private Double operand2 = 0.0;
    private String currentOperation = "?";
    private Button nine, eight, seven, six, five, four, three, two, one, zero;
    private Button clear, plusminus, percent, divide, multiply, minus, plus, equals, point;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg3);      // link to message textview in the Main activity XML
        messageText.setText("Hello Calculator");
        calculate = findViewById(R.id.calculator);

        clear = findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newString = " ";
                calculate.setText(" ");
            }
        });
        equals = findViewById(R.id.equals);
        equals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calculate.getText().toString().length() != 0 && !currentOperation.equals("?")) {
                    operand2 = Double.parseDouble(calculate.getText().toString());
                    if (currentOperation.equals("+")) {
                        Double result = operand1 + operand2;
                        calculate.setText(result.toString());
                    } else if (currentOperation.equals("-")) {
                        Double result = operand1 - operand2;
                        calculate.setText(result.toString());
                    } else if (currentOperation.equals("*")) {
                        Double result = operand1 * operand2;
                        calculate.setText(result.toString());
                    } else if (currentOperation.equals("/")) {
                        if (operand2 != 0) {
                            Double result = operand1 / operand2;
                            calculate.setText(result.toString());
                        } else {
                            calculate.setText("Error"); // Handle division by zero
                        }
                    }
                }
            }
        });
        percent = findViewById(R.id.percent);
        percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calculate.getText().toString().length() != 0) {
                    Double result = Double.parseDouble(calculate.getText().toString()) / 100;
                    calculate.setText(result.toString());
                }
            }
        });

        plusminus = findViewById(R.id.plusminus);
        plusminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calculate.getText().toString().length() != 0){
                    if (calculate.getText().toString().charAt(0) != '-'){
                        calculate.setText("-"+calculate.getText().toString());
                    }
                    else{
                        calculate.setText(calculate.getText().toString().substring(1));
                    }
                }
            }
        });

        plus = findViewById(R.id.plus);
        plus.setOnClickListener( view -> onOperationButtonClick("+"));

        divide = findViewById(R.id.divide);
        divide.setOnClickListener(view -> onOperationButtonClick("/"));

        multiply = findViewById(R.id.multiply);
        multiply.setOnClickListener(view -> onOperationButtonClick("*"));

        minus = findViewById(R.id.minus);
        minus.setOnClickListener(view -> onOperationButtonClick("-"));

        nine = findViewById(R.id.nine);
        nine.setOnClickListener(view -> onNumberButtonClick("9"));

        eight = findViewById(R.id.eight);
        eight.setOnClickListener(view -> onNumberButtonClick("8"));

        seven = findViewById(R.id.seven);
        seven.setOnClickListener(view -> onNumberButtonClick("7"));

        six = findViewById(R.id.six);
        six.setOnClickListener(view -> onNumberButtonClick("6"));

        five = findViewById(R.id.five);
        five.setOnClickListener(view -> onNumberButtonClick("5"));

        four = findViewById(R.id.four);
        four.setOnClickListener(view -> onNumberButtonClick("4"));

        three = findViewById(R.id.three);
        three.setOnClickListener(view -> onNumberButtonClick("3"));

        two = findViewById(R.id.two);
        two.setOnClickListener(view -> onNumberButtonClick("2"));

        one = findViewById(R.id.one);
        one.setOnClickListener(view -> onNumberButtonClick("1"));

        zero = findViewById(R.id.zero);
        zero.setOnClickListener(view -> onNumberButtonClick("0"));

        point = findViewById(R.id.point);
        point.setOnClickListener(view -> onNumberButtonClick("."));

        switchbutton = findViewById(R.id.switchbutton3);
        switchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Third.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
    private void onNumberButtonClick(java.lang.String number) {
        newString = calculate.getText().toString();
        if (newString.equals("0")) {
            calculate.setText(number);
        } else {
            calculate.setText(newString + number);
        }
    }

    private void onOperationButtonClick(java.lang.String operation) {
        currentOperation = operation;
        operand1 = Double.parseDouble(calculate.getText().toString());
        calculate.setText(" ");

    }

}