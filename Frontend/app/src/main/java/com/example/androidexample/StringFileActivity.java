package com.example.androidexample;

import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class StringFileActivity extends AppCompatActivity {

    private static Context ctx;

    private void StringFile(Context context)
    {
        ctx = context;
    }

    public void writeToFile() {
        File path = getApplicationContext().getFilesDir();

        try {
            File file = new File(path,fileName.getText().toString()+".md");
            // Create the file
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileOutputStream writer = new FileOutputStream(file);
            writer.write(mainText.getText().toString().getBytes());
            writer.close();
            Toast.makeText(getApplicationContext(), "File saved", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void readFromFile(String fileKey) {
        String fileName = fileKey;
        try  {
            FileInputStream fis = openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String fileContents = stringBuilder.toString();
            System.out.println("Contents of file: " + fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] listFiles(){
        File directory = getApplicationContext().getFilesDir();  // Internal storage directory
        File[] files = directory.listFiles();  // List all files in the directory
        String[] fileKeys = new String[files.length];
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                fileKeys[i] = files[i].getName();
                System.out.println("File: " + files[i].getName());
            }
        } else {
            System.out.println("no files found");;
        }
        return fileKeys;
    }


}
