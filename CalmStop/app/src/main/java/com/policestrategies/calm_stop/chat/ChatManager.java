package com.policestrategies.calm_stop.chat;

import android.app.Activity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Manages the sending and receiving of messages in {@link ChatActivity}
 * @author Talal Abou Haiba
 */

class ChatManager {

    private Activity mActivityReference;
    private DatabaseReference mMessagesReference;


    ChatManager(Activity ctx) {
        mActivityReference = ctx;
        String threadId = mActivityReference.getIntent().getStringExtra("thread_id");
        mMessagesReference = FirebaseDatabase.getInstance().getReference().child("threads")
                .child(threadId).child("messages").getRef();
        mMessagesReference.addChildEventListener(new ChatChildEventListener(this));
    }

    /**
     * Adds a message into the array adapter, displaying it on the screen.
     * @param content of the message
     * @param author of the message
     * @param timestamp - when the message was created
     */
    void displayMessage(String content, String author, long timestamp) {
        Message newMessage = new Message(content, timestamp, author);
        ((ChatActivity) mActivityReference).displayChatMessage(newMessage);
    }

    /**
     * Sends a message to Firebase, triggering onChildAdded which will update the array adapter
     * @param newMessage to be posted to Firebase
     */
    void sendToFirebase(Message newMessage){
        DatabaseReference newMessagesRef = mMessagesReference.push();
        newMessagesRef.setValue(newMessage);
    }


} // end class ChatManager
