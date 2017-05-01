package com.policestrategies.calm_stop.chat;

class Message {

    private String mContent;
    private String mTimestamp;
    private String mAuthorId;

    Message(String content, String timestamp, String authorID) {
        mContent = content;
        mTimestamp = timestamp;
        mAuthorId = authorID;
    }

    public String getContent() {
        return mContent;
    }

    public String getAuthorID() {
        return mAuthorId;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

} // end class Message
