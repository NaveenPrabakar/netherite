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


public class ProjectsActivity extends AppCompatActivity{

    private Button portfolioButton;
    private Button skillsButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.projects);

        portfolioButton = findViewById(R.id.portfolioButton);
        skillsButton = findViewById(R.id.skillsButton);

        portfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProjectsActivity.this, PortfolioActivity.class);
                startActivity(i);
            }
        });

        skillsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProjectsActivity.this, SkillsActivity.class);
                startActivity(i);
            }
        });
    }
}
