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

public class PortfolioActivity extends AppCompatActivity {

    private Button homeButton;
    private Button skillsButton;
    private Button projectsButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portfolio);

        homeButton = findViewById(R.id.backToHomeButton);
        skillsButton = findViewById(R.id.skillsButton);
        projectsButton = findViewById(R.id.projectsButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PortfolioActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        skillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PortfolioActivity.this, SkillsActivity.class);
                startActivity(i);
            }
        });
        projectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PortfolioActivity.this, ProjectsActivity.class);
                startActivity(i);
                }
        });
    }
}
