package com.example.androidexample;

public class Message {
    private String source;
    private String content;
    private String username;

    public Message(String source, String content, String username) {
        this.source = source;
        this.content = content;
        this.username = username;
    }

    // Getters and setters
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

