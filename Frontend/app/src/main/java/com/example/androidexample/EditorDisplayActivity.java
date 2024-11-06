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

    /* Try your best :) */
    /*
    This class aims to create a globally unique ID for each character that appears on the screen editor.
    It is used to identify and position stuff on the screen.
    Say the string "bruh" is written by User1. would have each char associated with an identifier..
    b [(1, 1)]
    r [(2, 1)]
    u [(3, 1)]
    h [(4, 1)]
    User2 writes to the same string. "brxuh"
    b [(1, 1)]
    r [(2, 1)]
    x [(2, 1), (1, 2)]  aka the fractional indice 2.1 , where 2 is associated with user1 and 1 is associated with user2.
    u [(3, 1)]
    h [(4, 1)]
    */
    private class Identifer {
        int digit; // The indice of the character in the string. This can also be used for fractional indicies.
        int site; // Assume that all clients have an unique site ID.
    }
    /*
    Each character has associated with it the tuple (digit, site),
    a clock time lamport,
    and the actual char itself value.
     */
    private class Char {
        Identifer[] position;
        int lamport;
        String value;
    }
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

                ghostString = charSequence.toString();
                Log.d("GhostString: ", ghostString);

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

    /*
    * Goals:
    * Create operation
    * Delete operation
    * Sorting of characters to determine which char should be added first
    * */

    /* Define sort order between Identifers.
    *  It'd sort the string like this:
    * Example:
    * [(1, 0)]  --> 1
    * [(1, 0), (1, 1)] --> 1.1
    * [(1, 0), (2, 1)] --> 1.2
    * [(1, 1)] --> 1  (why is 1 down here? because it lost the tiebreaker to [(1, 0)]. )
    * [(2, 0)] --> 2
    * [(2, 1)] --> 2
    * */
    private int comparePosition(Identifer[] a, Identifer[] b)
    {
     for (int i = 0; i < Math.min(a.length, b.length); i++)
        {
            int result = compareIdentifer(a[i], b[i]);
            if (result != 0) {
                return result;
            }
        }
     return 0;
    }

    private int compareIdentifer(Identifer a, Identifer b)
    {
        if (a.digit < b.digit) {
            return -1;
        }
        else if (a.digit > b.digit) {
            return 1;
        } // If there is a tie breaker, then we'll just use the site number instead.
        else {
            if (a.site < b.site) {
                return -1;
            }
            else if (a.site > b.site) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }

    private Identifer[] generatePosition(Identifer[] a, Identifer[] b, int site)
    {


        return null;
    }
}
