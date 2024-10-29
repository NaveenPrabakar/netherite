package com.example.androidexample;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.commonmark.node.Node;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;

public class EditorDisplayActivity extends AppCompatActivity {

    private MarkwonEditor markwonEditor;
    private Node testNode;
    private Spanned markdown;
    private Markwon markwon;
    private EditText display;
    private String content = "";
    private String ghostString = "";
    private Runnable markdownUpdater;
    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle instanceState)
    {
        super.onCreate(instanceState);
        setContentView(R.layout.activity_display);

        /* Display Stuff */
        display = findViewById(R.id.EditMarkdown);

        /* Markwon Stuff */
        markwon = Markwon.create(this);
        markwonEditor = MarkwonEditor.create(markwon);
        testNode = markwon.parse("# Hello, World!");
        markdown = markwon.render(testNode);
        markwon.setParsedMarkdown(display, markdown);
        display.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(markwonEditor));

        display.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (markdownUpdater != null)
                {
                    handler.removeCallbacks(markdownUpdater);
                }

//                markdownUpdater = () ->
//                {
//                    content += charSequence.toString();
//                    testNode = markwon.parse(content);
//                    Log.d("MarkdownOnChange: ", content);
//                };
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (markdownUpdater != null)
                {
                    handler.removeCallbacks(markdownUpdater);
                }

                markdownUpdater = () ->
                {
                    content = editable.toString();
                    testNode = markwon.parse(content);
                    Spanned tMarkdown = markwon.render(testNode);
                    Log.d("MarkdownAfterChange: ", tMarkdown.toString());

                    display.removeTextChangedListener(this);
                    markwon.setParsedMarkdown(display, tMarkdown);
                    display.addTextChangedListener(this);
                };

                handler.postDelayed(markdownUpdater, 500);
            }
        });
    }
}
