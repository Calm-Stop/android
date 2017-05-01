package com.policestrategies.calm_stop.chat;

import android.app.Activity;

/**
 * Manages the sending and receiving of messages in {@link ChatActivity}
 * @author Talal Abou Haiba
 */

class ChatManager {

    private Activity mActivityReference;

    ChatManager(Activity ctx) {
        mActivityReference = ctx;
    }

    void sendChatMessage(String content, String author, String timestamp) {
        Message newMessage = new Message(content, timestamp, author);
        ((ChatActivity) mActivityReference).displayChatMessage(newMessage);
    }

} // end class ChatManager
