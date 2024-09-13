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


public class SkillsActivity extends AppCompatActivity {

    private Button portfolioButton;
    private Button projectsButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skills);

        portfolioButton = findViewById(R.id.portfolioButton);
        projectsButton = findViewById(R.id.projectsButton);

        portfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SkillsActivity.this, PortfolioActivity.class);
                startActivity(i);

            }
        });
        projectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SkillsActivity.this, ProjectsActivity.class);
                startActivity(i);
            }
        });
    }
}
