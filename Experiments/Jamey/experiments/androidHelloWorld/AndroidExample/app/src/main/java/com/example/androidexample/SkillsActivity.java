package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;


public class SkillsActivity extends AppCompatActivity {

    private Button portfolioButton;
    private Button projectsButton;
    private Switch switch1;
    private RecyclerView skillsText;
    private RecyclerView interestsText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skills);

        portfolioButton = findViewById(R.id.portfolioButton);
        projectsButton = findViewById(R.id.projectsButton);
        switch1 = findViewById(R.id.switch1);
        skillsText = findViewById(R.id.recyclerView);
        interestsText = findViewById(R.id.recyclerView2);

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
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int visibility = isChecked ? View.VISIBLE :
                        View.GONE;skillsText.setVisibility(visibility);
                        interestsText.setVisibility(visibility);
                /*
                if (isChecked) {
                    skillsText.setVisibility(View.VISIBLE);
                    interestsText.setVisibility(View.VISIBLE);
                } else {
                    skillsText.setVisibility(View.GONE);
                    interestsText.setVisibility(View.GONE);
                }
                 */
            }
        });
    }
}
