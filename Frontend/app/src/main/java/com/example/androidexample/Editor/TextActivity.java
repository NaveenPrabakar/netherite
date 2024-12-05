package com.example.androidexample.Editor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.example.androidexample.FileView.filesActivity;
import com.example.androidexample.R;
import com.example.androidexample.UserPreferences;
import com.example.androidexample.Volleys.VolleySingleton;
import com.example.androidexample.WebSockets.WebSocketListener;
import com.example.androidexample.WebSockets.WebSocketManager;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;

public class TextActivity extends AppCompatActivity implements WebSocketListener {

    private static final String TAG = "TextActivity";
    private static final String URL_AI_GET = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/getUsageAPICount/";
    private static final String URL_AI_POST = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/createAIUser";
    private static final String URL_AI_DELETE = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/resetUsage/";
    private static final String URL_AI_PUT = "http://coms-3090-068.class.las.iastate.edu:8080/OpenAIAPIuse/updateAIUser";

    private Button back2main, saveButt, summarizeButt, acceptButt, liveChatButt, rejectButt, voiceButt;
    private EditText mainText, editor, fileName, AIInputText;
    private TextView AIText;
    private Markwon markwon;
    private TextWatcher textWatcher;

    private JSONObject fileSystem, filePath;
    private String email, password, username, aiCount, content = " ", aiURL, source, history = "";

    private BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);


        initializeUI();
        loadUserPreferences();
        setupEditorListeners();
        setupButtons();
        processIncomingIntent();
    }

    private void initializeUI() {
        mainText = findViewById(R.id.textViewMarkdown);
        editor = findViewById(R.id.EditMarkdown);
        fileName = findViewById(R.id.fileName);
        AIText = findViewById(R.id.AITextView);
        liveChatButt = findViewById(R.id.liveChatButt);
        summarizeButt = findViewById(R.id.summarizeButt);
        acceptButt = findViewById(R.id.acceptButt);
        rejectButt = findViewById(R.id.rejectButt);
        voiceButt = findViewById(R.id.voiceButt);

        markwon = Markwon.builder(this).build();

        acceptButt.setVisibility(View.INVISIBLE);
        rejectButt.setVisibility(View.INVISIBLE);
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
        liveChatButt.setOnClickListener(v -> navigateToChatActivity());
        summarizeButt.setOnClickListener(v -> startSummarization());
        acceptButt.setOnClickListener(v -> acceptAIResponse());
        rejectButt.setOnClickListener(v -> rejectAIResponse());
        voiceButt.setOnClickListener(v -> navigateToVoiceActivity());
        back2main = findViewById(R.id.back2main);
        back2main.setOnClickListener(v -> navigateToFilesActivity());
    }

    private void processIncomingIntent() {
        Intent intent = getIntent();
        if (intent.getExtras() == null) return;

        Bundle extras = intent.getExtras();
        content = extras.getString("CONTENT", content);
        editor.setText(content);

        fileName.setText(extras.getString("FILEKEY", ""));
        appendRecordedContent(extras.getString("RECORDED", ""));
        processAIExtras(extras);
    }

    private void processAIExtras(Bundle extras) {
        if (extras.containsKey("IMAGETEXT")) {
            AIText.setText(extras.getString("IMAGETEXT"));
            toggleSummarizeVisibility(false);
        }
        aiURL = extras.getString("AIWSURL", aiURL);
    }

    private void toggleEditorFocus(boolean hasFocus) {
        if (hasFocus) {
            mainText.setAlpha(0);
            editor.setAlpha(1);
        } else {
            mainText.setAlpha(1);
            editor.setAlpha(0);
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
        intent.putExtra("EMAIL", email);
        intent.putExtra("PASSWORD", password);
        intent.putExtra("USERNAME", username);
        intent.putExtra("FILESYSTEM", fileSystem.toString());
        intent.putExtra("PATH", filePath.toString());
        intent.putExtra("CONTENT", content);
        startActivity(intent);
    }

    private void navigateToFilesActivity() {
        Intent intent = new Intent(this, filesActivity.class);
        startActivity(intent);
    }

    private void appendRecordedContent(String recordedContent) {
        if (!recordedContent.isEmpty()) {
            editor.append("   \n   \n" + recordedContent);
        }
    }

    private void appendAIResponse() {
        editor.append("\n\n---\nAI Response: " + AIText.getText() + "\n---\n");
    }

    private void clearAIText() {
        AIText.setText("");
    }

    private void toggleSummarizeVisibility(boolean visible) {
        summarizeButt.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        voiceButt.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        acceptButt.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        rejectButt.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
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
                response -> handleSummarizeResponse(method, contentToSummarize, email, prompt, response),
                this::logVolleyError
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
                    response -> handleSummarizeHelpResponse(response),
                    this::logVolleyError
            );
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(summarizePost);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating summarize request body", e);
        }
    }

    private void handleSummarizeHelpResponse(JSONObject response) {
        try {
            AIText.setText(response.getString("reply"));
            aiCount = response.getString("count");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing summarize help response", e);
        }
    }

    private void logVolleyError(VolleyError error) {
        Log.e(TAG, "Volley Error", error);
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
            AIText.setText(messageContent);
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
}
