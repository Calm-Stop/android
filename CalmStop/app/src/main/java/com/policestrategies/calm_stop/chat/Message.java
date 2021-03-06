package com.policestrategies.calm_stop.chat;

public class Message {

    private String mContent;
    private long mTimestamp;
    private String mAuthor;

    public Message(String content, long timestamp, String author) {
        mContent = content;
        mTimestamp = timestamp;
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

} // end class Message
