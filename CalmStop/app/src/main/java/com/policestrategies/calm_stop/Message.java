package com.policestrategies.calm_stop;

public class Message {
//FIELDS
    private String timestamp;
    private String content;
    private int authorID;
    private int threadID;
    private int messageID;

//CONSTRUCTOR
    public Message(String timestamp, String content, int authorID, int threadID, int messageID) {
        this.timestamp = timestamp;
        this.content = content;
        this.authorID = authorID;
        this.threadID = threadID;
        this.messageID = messageID;
    }

//GETTERS AND SETTERS
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getAuthor() { return authorID; }
    public void setAuthor(int author) { this.authorID = author; }

    public int getThreadID(){ return threadID; }
    public void setThreadID(int threadID) { this.threadID = threadID; }

    public int getMessageID() { return messageID; }
    public void setMessageID(int messageID) { this.messageID = messageID; }

} // end class Message
