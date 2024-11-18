package com.example.androidexample.Editor;

/**
 * Represents a message in a chat box.
 * It's displayed as an .xml file, {@code message_item.xml} which serves as a div to be populated.
 */
public class Message {
    /**
     * The source of this message.
     * Used to determine whether the message is from the AI or the user.
     */
    private String source;
    /**
     * The content of this message.
     */
    private String content;
    /**
     * The username of the user who sent this message.
     */
    private String username;

    /**
     * Constructor for a message.
     * @param source The source of this message.
     * @param content The content of this message.
     * @param username The username of the user who sent this message, or AI if it was AI generated.
     */
    public Message(String source, String content, String username) {
        this.source = source;
        this.content = content;
        this.username = username;
    }

    /**
     * Getter for the source of this message.
     * @return The source of this message.
     */
    public String getSource() {
        return source;
    }

    /**
     * Setter for the source of this message.
     * @param source The new source of this message.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Getter of the content of this message.
     * @return The content of this message.
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter for the content of this message.
     * @param content The new content of this message.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Getter for the username of the user who sent this message.
     * @return The username of the user who sent this message.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter for the username of the user who sent this message.
     * @param username The new username of the user who sent this message.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}

