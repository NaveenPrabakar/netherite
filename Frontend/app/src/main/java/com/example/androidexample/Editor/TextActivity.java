package com.example.androidexample.Editor;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.StringRequest;
import com.example.androidexample.FileView.OCRActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.example.androidexample.FileView.filesActivity;
import com.example.androidexample.R;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.AudioRecieve;
import com.example.androidexample.Volleys.VolleySingleton;
import com.example.androidexample.WebSockets.WebSocketListener;
import com.example.androidexample.WebSockets.WebSocketManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;

public class TextActivity extends AppCompatActivity implements WebSocketListener {

    private static final String TAG = "TextActivity";
    private static final String URL_AI_GET = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/getUsageAPICount/";
    private static final String URL_AI_POST = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/createAIUser";
    private static final String URL_AI_DELETE = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/resetUsage/";
    private static final String URL_AI_PUT = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/updateAIUser";
    private static final String URL_TEXT_TO_SPEECH = "http://coms-3090-068.class.las.iastate.edu:8080/textToSpeech/synthesizer";
    private Button ttsButt, summarizeButt, acceptButt, rejectButt, voiceButt, OCRButt, translateButt, AIButton;
    private EditText mainText, editor, AIInputText;
    private TextView AITextBox, fileName;
    private Markwon markwon;
    private ImageView back2main;
    private TextWatcher textWatcher;
    private CardView AIText;

    private JSONObject fileSystem, filePath;
    private String email, password, username, aiCount, content = " ", aiURL, source, history = "";

    private String[] languages = {"English", "Spanish", "French", "German", "Italian", "Japanese", "Korean", "Chinese"};

    private String translatedString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        WebSocketManager.getInstance().setWebSocketListener(this);

        initializeUI();
        loadUserPreferences();
        setupEditorListeners();
        setupButtons();
        processIncomingIntent();
    }

    private void initializeUI() {
        mainText = findViewById(R.id.textViewMarkdown);
        editor = findViewById(R.id.EditMarkdown);
        fileName = findViewById(R.id.headerTitle);
        AIText = findViewById(R.id.AITextView);
        AITextBox = findViewById(R.id.AITextBox);
        translateButt = findViewById(R.id.translateButt);
        summarizeButt = findViewById(R.id.summarizeButt);
        acceptButt = findViewById(R.id.acceptButt);
        rejectButt = findViewById(R.id.rejectButt);
        voiceButt = findViewById(R.id.voiceButt);
        ttsButt = findViewById(R.id.text2speech);
        back2main = findViewById(R.id.back2main);
        OCRButt = findViewById(R.id.OCRButtTXT);
        AIButton = findViewById(R.id.AIbutton);
        AIInputText = findViewById(R.id.AIChatBar);


        markwon = Markwon.builder(this).build();
        mainText.setAlpha(1);
        editor.setAlpha(0);

        toggleSummarizeVisibility(true);

    }

    private void loadUserPreferences() {
        try {
            fileSystem = new JSONObject(UserPreferences.getFileSystem(this));
            filePath = new JSONObject(UserPreferences.getFilePath(this));
            email = UserPreferences.getEmail(this);
            password = UserPreferences.getPassword(this);
            username = UserPreferences.getUsername(this);
            Log.d(TAG, "File System: " + fileSystem);
            Log.d(TAG, "File Path: " + filePath);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupEditorListeners() {
        MarkwonEditor editorMarkwon = MarkwonEditor.create(markwon);

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content = s.toString();
                updateParsedOutput(content);
                
                WebSocketManager.getInstance().sendMessage(content);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editor.addTextChangedListener(textWatcher);
        editor.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editorMarkwon));
        editor.setOnFocusChangeListener((v, hasFocus) -> toggleEditorFocus(hasFocus));
        mainText.setOnClickListener(v -> redirectFocusToEditor());
    }

    private void setupButtons() {
        summarizeButt.setOnClickListener(v -> startSummarization());
        acceptButt.setOnClickListener(v -> acceptAIResponse());
        rejectButt.setOnClickListener(v -> rejectAIResponse());
        voiceButt.setOnClickListener(v -> navigateToVoiceActivity());
        back2main.setOnClickListener(v -> navigateToFilesActivity());
        ttsButt.setOnClickListener(v -> sendStringAndReceiveAudioMultipart(mainText.getText().toString()));
        OCRButt.setOnClickListener(v -> OCRNavigate());
        translateButt.setOnClickListener(v -> {
            // Create and show the popup
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Select Language")
                    .setItems(languages, (dialog, which) -> {
                        // Handle language selection
                        String selectedLanguage = languages[which];
                        Toast.makeText(this, "Selected: " + selectedLanguage, Toast.LENGTH_SHORT).show();

                        promptString(selectedLanguage, mainText.getText().toString()).thenAccept(translatedText -> {
                            AITextBox.setText(translatedText);
                            toggleSummarizeVisibility(false);
                        })
                                .exceptionally(throwable -> {
                                    Log.e("Translation Error", throwable.toString());
                                    Toast.makeText(this, "I fucked up!", Toast.LENGTH_SHORT).show();
                                    return null;
                                });
                    })
                    .show();
        });
        AIButton.setOnClickListener(v -> {
            if (!AIInputText.getText().toString().isEmpty()){
                promptString(AIInputText.getText().toString(), mainText.getText().toString()).thenAccept(promptResult -> {
                    AITextBox.setText(promptResult);
                    toggleSummarizeVisibility(false);
                });
            }else{
                Toast.makeText(this, "Please enter a prompt!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void OCRNavigate(){
        Intent intent = new Intent(this, OCRActivity.class);
        intent.putExtra("SOURCE", "text");
        intent.putExtra("CONTENT", content);
        intent.putExtra("FILEKEY", fileName.getText().toString());
        startActivity(intent);
    }

    private void processIncomingIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() == null) return;

        Bundle extras = intent.getExtras();
        content = extras.getString("CONTENT", content);
        editor.setText(content);

        fileName.setText(extras.getString("FILEKEY", ""));
        processAIExtras(extras);
    }

    private void processAIExtras(Bundle extras) {
        if (extras.containsKey("IMAGETEXT")) {
            AITextBox.setText(extras.getString("IMAGETEXT"));
            toggleSummarizeVisibility(false);
        }
        if (extras.containsKey("RECORDED")) {
            AITextBox.setText(extras.getString("RECORDED"));
            toggleSummarizeVisibility(false);
        }

        aiURL = extras.getString("AIWSURL", aiURL);
    }

    private void toggleEditorFocus(boolean hasFocus) {
        if (hasFocus) {
            mainText.setAlpha(0);
            editor.setAlpha(1);
            toggleSummarizeVisibility(true);
        } else {
            mainText.setAlpha(1);
            editor.setAlpha(0);
            toggleSummarizeVisibility(true);
        }
    }

    private void redirectFocusToEditor() {
        editor.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editor, InputMethodManager.SHOW_IMPLICIT);
    }

    private void navigateToChatActivity() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("AIWSURL", aiURL);
        startActivity(intent);
    }

    private void startSummarization() {
        summarizeString(Request.Method.GET, content, email, "summarize", URL_AI_GET);
        toggleSummarizeVisibility(false);
    }

    private void acceptAIResponse() {
        appendAIResponse();
        clearAIText();
        toggleSummarizeVisibility(true);
    }

    private void rejectAIResponse() {
        summarizeString(Request.Method.DELETE, content, email, "reject", URL_AI_DELETE);
        clearAIText();
        toggleSummarizeVisibility(true);
    }

    private void navigateToVoiceActivity() {
        Intent intent = new Intent(this, VoiceRecordActivity.class);
        intent.putExtra("FILEKEY", fileName.getText().toString());
        intent.putExtra("CONTENT", content);
        startActivity(intent);
    }

    private void navigateToFilesActivity() {
        WebSocketManager.getInstance().disconnectWebSocket();
        Intent intent = new Intent(this, filesActivity.class);
        startActivity(intent);
    }


    private void appendAIResponse() {
        editor.append("\n\n---\nAI Response: " + AITextBox.getText() + "\n---\n");
    }

    private void clearAIText() {
        toggleSummarizeVisibility(true);
        AITextBox.setText("");
    }

    private void toggleSummarizeVisibility(boolean visible) {
        acceptButt.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        rejectButt.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        AIText.setAlpha(visible ? 0 : 1);
    }

    private String updateParsedOutput(String markdown) {
        StringBuilder parsedContent = new StringBuilder();
        for (char ch : markdown.toCharArray()) {
            parsedContent.append(ch == '\n' ? "  \n" : ch);
        }
        markwon.setMarkdown(mainText, parsedContent.toString());
        return parsedContent.toString();
    }

    private void summarizeString(int method, String contentToSummarize, String email, String prompt, String URL) {
        JsonObjectRequest summarizePost = new JsonObjectRequest(
                method,
                URL + email,
                null,
                response -> {
                    Toast.makeText(getApplicationContext(), "Successfully requested", Toast.LENGTH_SHORT).show();
                    handleSummarizeResponse(method, contentToSummarize, email, prompt, response);

                },
                error -> {
                    Log.e(TAG, "Error retrieving request", error);
                    Toast.makeText(getApplicationContext(), "Summarize request failed", Toast.LENGTH_SHORT).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(summarizePost);
    }

    private void handleSummarizeResponse(int method, String contentToSummarize, String email, String prompt, JSONObject response) {
        try {
            aiCount = response.getString("reply");
            if (method == Request.Method.GET) {
                if ("-1".equals(aiCount)) {
                    summarizeStringHelp(Request.Method.POST, contentToSummarize, email, prompt, URL_AI_POST);
                } else {
                    summarizeStringHelp(Request.Method.PUT, contentToSummarize, email, prompt, URL_AI_PUT);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing summarize response", e);
        }
    }

    private void summarizeStringHelp(int method, String contentToSummarize, String email, String prompt, String URL) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("email", email);
            requestBody.put("prompt", prompt);
            requestBody.put("content", contentToSummarize);

            JsonObjectRequest summarizePost = new JsonObjectRequest(
                    method,
                    URL,
                    requestBody,
                    response -> {
                        Toast.makeText(getApplicationContext(), "Successfully requested", Toast.LENGTH_SHORT).show();
                        handleSummarizeHelpResponse(response);
                        },
                    error -> {
                        Log.e(TAG, "Error retrieving request", error);
                        Toast.makeText(getApplicationContext(), "Summarize prompting failed", Toast.LENGTH_SHORT).show();
                    }
            );
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(summarizePost);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating summarize request body", e);
        }
    }

    private void handleSummarizeHelpResponse(JSONObject response) {
        try {
            toggleSummarizeVisibility(false);
            AITextBox.setText(response.getString("reply"));
            aiCount = response.getString("count");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing summarize help response", e);
        }
    }

    private void logVolleyError(VolleyError error) {
        Log.e(TAG, "Volley Error", error);
        Toast.makeText(this, "Error retrieving request", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "WebSocket Connected");
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> processWebSocketMessage(message));
    }

    private void processWebSocketMessage(String message) {
        int newCursorPosition = Math.max(getCorrectCursorLocation(content, message, editor.getSelectionStart()), 0);

        editor.removeTextChangedListener(textWatcher);
        editor.setText(message);
        content = message;

        if (newCursorPosition <= editor.getText().length()) {
            editor.setSelection(newCursorPosition);
        } else {
            editor.setSelection(editor.getText().length());
        }
        updateParsedOutput(editor.getText().toString());
        editor.addTextChangedListener(textWatcher);
    }

    @Override
    public void onWebSocketJsonMessage(JSONObject jsonMessage) {
        try {
            source = jsonMessage.getString("source");
            String messageContent = jsonMessage.getString("content");
            String user = jsonMessage.getString("username");

            history += messageContent + ":" + user + " ";
            toggleSummarizeVisibility(false);
            AITextBox.setText(messageContent);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON message", e);
        }
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d(TAG, "WebSocket Closed");
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.e(TAG, "WebSocket Error", ex);
    }

    private int getCorrectCursorLocation(String before, String after, int cursorPos) {
        int diffIndex = findDifferenceIndex(before, after);
        int lenChanged = after.length() - before.length();

        if (lenChanged > 0 && diffIndex < cursorPos) {
            return cursorPos + lenChanged;
        } else if (lenChanged < 0 && diffIndex <= cursorPos) {
            return cursorPos + lenChanged;
        }
        return cursorPos;
    }

    private int findDifferenceIndex(String before, String after) {
        int minLen = Math.min(before.length(), after.length());
        for (int i = 0; i < minLen; i++) {
            if (before.charAt(i) != after.charAt(i)) {
                return i;
            }
        }
        return minLen;
    }

    private void sendStringAndReceiveAudioMultipart(String contentToSendString) {
        Log.d(TAG, "Sending string: " + contentToSendString);
        try {
            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("text", contentToSendString);
            // Create and configure the request
            AudioRecieve audioRequest = new AudioRecieve(
                    Request.Method.POST,
                    URL_TEXT_TO_SPEECH,
                    requestBody.toString(),
                    response -> handleAudioResponseMultipart(response),
                    this::logVolleyError
            );

            // Add the request to the Volley request queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(audioRequest);

        } catch (JSONException e) {
            Log.e(TAG, "Error creating request body", e);
        }
    }


    private void handleAudioResponseMultipart(byte[] response) {
        try {
            // Save the audio file locally
            File audioFile = new File(getExternalFilesDir(null), "received_audio.mp3");
            FileOutputStream fos = new FileOutputStream(audioFile);
            fos.write(response);
            fos.close();

            playSavedAudio(audioFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "Audio Save/Playback Error", e);
        }
    }

    private void playSavedAudio(String mFileName) {
        MediaPlayer mPlayer = new MediaPlayer();
        try {
            Log.d("Voice", "playAudio: " + mFileName);
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
    }

    /**
     * Translates a given string and returns that string translated into a desired language.
     * @param prompt
     * @param content
     */
    private CompletableFuture<String> promptString(String prompt, String content)
    {
        CompletableFuture<String> futureString = new CompletableFuture<>();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                UserPreferences.getUrlTranslate(),
                response -> {
                    Log.d("Response Translated", response);
                    futureString.complete(response);
                    Log.d("Translated String is", translatedString);
                    Toast.makeText(getApplicationContext(), "Successfully retrieved prompt result", Toast.LENGTH_SHORT).show();
                    },
                this::logVolleyError
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("prompt", prompt);
                params.put("text", content);
                return params;
            }
        };
        Log.d("Plz Don't Be Empty", translatedString);
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        return futureString;
    }

}
