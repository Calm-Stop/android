package com.policestrategies.calm_stop;

public class ChatMessage {
//FIELDS
    private String content;
    private long timestamp;
    private String threadID;
    private String authorID;
    private String recipientID;
    private String authorName;
    public boolean left;

//CONSTRUCTOR

    public ChatMessage(boolean left, String content, long timestamp,
                       String threadID, String authorID, String authorName) {
        super();
        this.left = left;
        this.content = content;
        this.timestamp = timestamp;
        this.threadID = threadID;
        this.authorID = authorID;
        this.authorName = authorName;
    }

    public ChatMessage(boolean left, String content) {
        super();
        this.left = left;
        this.content = content;
    }

    //GETTERS for private functions (no setters)
    public String getContent() { return content; }

    public String getAuthorID() { return authorID; }

    public String getAuthorName() { return authorName; }

    public String getThreadID() { return threadID; }
} // end class ChatMessage
