package com.policestrategies.calm_stop;

public class ChatMessage {
//FIELDS
    private String content;
    private String timestamp;
    private String threadID;
    private String authorID;

//CONSTRUCTOR

    public ChatMessage(String content, String timestamp,
                       String threadID, String authorID) {
        super();
        this.content = content;
        this.timestamp = timestamp;
        this.threadID = threadID;
        this.authorID = authorID;
    }

    //GETTERS for private functions (no setters)
    public String getContent() { return content; }

    public String getAuthorID() { return authorID; }

    public String getThreadID() { return threadID; }

    public String getTimestamp() { return timestamp; }
} // end class ChatMessage
