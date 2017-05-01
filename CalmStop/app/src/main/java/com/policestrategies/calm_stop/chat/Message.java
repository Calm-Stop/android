package com.policestrategies.calm_stop.chat;

class Message {

    private String mContent;
    private String mTimestamp;
    private String mThreadId;
    private String mAuthorId;

    Message(String content, String timestamp, String threadID, String authorID) {
        mContent = content;
        mTimestamp = timestamp;
        mThreadId = threadID;
        mAuthorId = authorID;
    }

    public String getContent() {
        return mContent;
    }

    public String getAuthorID() {
        return mAuthorId;
    }

    public String getThreadID() {
        return mThreadId;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

} // end class Message
