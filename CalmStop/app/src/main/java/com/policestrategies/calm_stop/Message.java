package com.policestrategies.calm_stop;

public class Message {
//FIELDS
    private String content;
    private int timestamp;
    private String authorID;
    private String threadID;
    private String messageID;

//CONSTRUCTOR


    public Message(String content, int timestamp, String authorID, String threadID, String messageID) {
        this.content = content;
        this.timestamp = timestamp;
        this.authorID = authorID;
        this.threadID = threadID;
        this.messageID = messageID;
    }

    //GETTERS AND SETTERS
    public int getTimestamp() { return timestamp; }
    public void setTimestamp(int timestamp) { this.timestamp = timestamp; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthorID() { return authorID; }
    public void setAuthorID(String author) { this.authorID = author; }

    public String getThreadID(){ return threadID; }
    public void setThreadID(String threadID) { this.threadID = threadID; }

    public String getMessageID() { return messageID; }
    public void setMessageID(String messageID) { this.messageID = messageID; }

} // end class Message
