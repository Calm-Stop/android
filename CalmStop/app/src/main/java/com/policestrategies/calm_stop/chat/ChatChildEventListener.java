package com.policestrategies.calm_stop.chat;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * ChildEventListener for {@link ChatActivity}
 * @author Talal Abou Haiba
 */

class ChatChildEventListener implements ChildEventListener {

    private final String TAG = "ChatChildEventListener";

    private ChatManager mChatManager;

    ChatChildEventListener(ChatManager chatManager) {
        mChatManager = chatManager;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "In Citizen/ChatActivity, onChildAdded");

        String content = dataSnapshot.child("content").getValue().toString();
        String authorID = dataSnapshot.child("author").getValue().toString();
        long timestamp = ((long) dataSnapshot.child("timestamp").getValue());

        mChatManager.displayMessage(content, authorID, timestamp);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "postComments:onCancelled", databaseError.toException());
    }


} // end class ChatChildEventListener
