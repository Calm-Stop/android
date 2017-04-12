package com.policestrategies.calm_stop;

public class ChatMessage {
//FIELDS
    private String content;
    private int timestamp;
    private String authorID;
    private String recipientID;
    private String authorName;
    public boolean left;

//CONSTRUCTOR

    public ChatMessage(boolean left, String content, int timestamp,
                       String authorID, String authorName) {
        super();
        this.left = left;
        this.content = content;
        this.timestamp = timestamp;
        this.authorID = authorID;
        this.authorName = authorName;
    }

    public ChatMessage(boolean left, String content) {
        super();
        this.left = left;
        this.content = content;
    }

    //GETTERS
    public int getTimestamp() { return timestamp; }

    public String getContent() { return content; }

    public String getAuthorID() { return authorID; }

    public String getAuthorName() { return authorName; }
} // end class ChatMessage
