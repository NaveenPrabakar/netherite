package com.example.androidexample;

import static com.android.volley.Request.Method.POST;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.commonmark.node.Node;
import org.json.JSONException;
import org.json.JSONObject;

import io.noties.markwon.Markwon;

public class TextActivity extends AppCompatActivity {
    private final String URL_STRING_REQ = "http://10.26.47.170:8080/files/upload";
    private final String URL_SUMMARIZE_REQ = "https://5a2cd8da-ae65-4e72-9b37-93e9c4132497.mock.pstmn.io";
    private Button back2main;
    private Button saveButt;
    private Button summarizeButt;
    private EditText mainText;
    private Button editButton;
    private EditText editor;
    private EditText fileName;
    private Markwon markwon;
    private String content = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);


        mainText = findViewById(R.id.textViewMarkdown);
        editor = findViewById(R.id.EditMarkdown);
        fileName = findViewById(R.id.fileName);
        editButton = findViewById(R.id.editButton);

        markwon = Markwon.create(this);

        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    content = charSequence.toString();
                    updateParsedOutput(content);
                    Log.d("content", content);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        editor.setAlpha(0f);

        back2main = findViewById(R.id.back2main);
        back2main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(TextActivity.this, MainActivity.class);
                startActivity(intent);  // go to SignupActivity
            }
        });


        saveButt = findViewById(R.id.saveButt);
        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileName.getText().toString().isEmpty()) {
                    System.out.println("file name is empty");
                } else {
                    writeToFile();
                    listFiles();
                    sendFileString(fileName.getText().toString() + ".md", URL_STRING_REQ);
                    readFromFile(fileName.getText().toString() + ".md");
                }

            }
        });

        summarizeButt = findViewById(R.id.summarizeButt);
        summarizeButt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (fileName.getText().toString().isEmpty())
                {
                    System.out.println("file name is empty");
                }
                else
                {
                    summarizeString(content, URL_SUMMARIZE_REQ);
                }
            }
        });
    }

    private void updateParsedOutput(String markdown) {
        String contentParsed = "";
        for (int i = 0; i < markdown.length(); i++){
            if (markdown.charAt(i) == '\n'){
                contentParsed += "  \n";
                Log.d("content", "newline detected");
            }else{
                contentParsed += markdown.charAt(i);
            }
        }
        markwon.setMarkdown(mainText, contentParsed);
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
            writer.write(content.getBytes());
            writer.close();
            Toast.makeText(getApplicationContext(), "File saved", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Pair<String, String> readFromFile(String fileKey) {
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
            System.out.println("Contents of file: "+ fileName + ": " + fileContents);
            return Pair.create(fileName, fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Pair.create("Null", "Null");
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

    public void sendFile() {
        File path = getApplicationContext().getFilesDir();
        File file = new File(path, fileName.getText().toString() + ".md");

        if (!file.exists()) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = URL_STRING_REQ;  // Replace with your server's URL

        HashMap<String, String> params = new HashMap<>();
        params.put("description", "This is a file upload");

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(POST, url, file,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(TextActivity.this, "File sent successfully!", Toast.LENGTH_SHORT).show();
                        Log.d("VolleyResponse", new String(response.data));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TextActivity.this, "File upload failed", Toast.LENGTH_SHORT).show();
                        Log.e("VolleyError", error.toString());
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(multipartRequest);
    }


    public void sendFileString(String fileName, String URL){
            Uri.Builder builder = Uri.parse(URL).buildUpon();
            Pair<String, String> pair = readFromFile(fileName);
            builder.appendQueryParameter("file", pair.first);
            builder.appendQueryParameter("content", pair.second);
            String url = builder.build().toString();

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Volley Response", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle any errors that occur during the request
                            Log.e("Volley Error", error.toString());
                        }
                    }
            );

            // Adding request to request queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void summarizeString(String contentToSummarize, String URL)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d("Signup Success", response.toString());
                        String resp = response;
                        System.out.println(resp);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("Volley Error", error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("content", contentToSummarize);
                return params;
            }
        };


        // Add the string request to the Volley Queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
