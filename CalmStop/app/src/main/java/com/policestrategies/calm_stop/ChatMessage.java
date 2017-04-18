package com.policestrategies.calm_stop;

public class ChatMessage {
//FIELDS
    private String content;
    private String timestamp;
    private String threadID;
    private String authorID;
    public String left;

//CONSTRUCTOR

    public ChatMessage(String left, String content, String timestamp,
                       String threadID, String authorID) {
        super();
        this.left = left;
        this.content = content;
        this.timestamp = timestamp;
        this.threadID = threadID;
        this.authorID = authorID;
    }

    public ChatMessage(String left, String content) {
        super();
        this.left = left;
        this.content = content;
    }

    //GETTERS for private functions (no setters)
    public String getContent() { return content; }

    public String getAuthorID() { return authorID; }

    public String getThreadID() { return threadID; }

    public String getTimestamp() { return timestamp; }
} // end class ChatMessage
