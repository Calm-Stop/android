package com.policestrategies.calm_stop.chat;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.citizen.LoginActivity;

import static java.lang.System.currentTimeMillis;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter mChatArrayAdapter;
    private ListView mListView;
    private EditText mChatText;

    private DatabaseReference mMessagesReference;

    private String mUid;

    private String mContent;
    private String mAuthorID;
    private String mThreadID;
    private String mTimestamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mListView = (ListView) findViewById(R.id.listView1);

        mChatArrayAdapter = new ChatArrayAdapter(getApplicationContext(),
                R.layout.activity_chat_singlemessage);

        mListView.setAdapter(mChatArrayAdapter);

        // Default Message object values to be passed to Message constructor
        mChatText = (EditText) findViewById(R.id.chat_text);
        mAuthorID = mThreadID = "01";

        // Obtaining the user's data from firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            mUid = firebaseAuth.getCurrentUser().getUid();
        } else {
            firebaseAuth.signOut();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }


            DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();

            //Store messageID's under messageReference (messages)
            mMessagesReference = mDatabaseRef.child("threads").
                    child(mThreadID).child("messages").getRef();
//ChildEventListener listens for changes to chat
//When a Message is pushed to firebase, the onChildAdded method of childEventListener activates
//ChildEventListener is also used for initialization of arrayadapter
            mMessagesReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "In Citizen/ChatActivity, onChildAdded");

                    mContent = dataSnapshot.child("content").getValue().toString();
                    mAuthorID = dataSnapshot.child("authorID").getValue().toString();
                    mTimestamp = dataSnapshot.child("timestamp").getValue().toString();
                    sendChatMessage();
                    mChatText.setText("");
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
                    Toast.makeText(getBaseContext(), "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            });


        findViewById(R.id.button_send).setOnClickListener(this);

        mListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setAdapter(mChatArrayAdapter);

        //to scroll the list view to bottom on data change
        mChatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mListView.setSelection(mChatArrayAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_send:
                mTimestamp = Long.toString(currentTimeMillis());
                mContent = mChatText.getText().toString();
                //The context for generating threadID has yet to be designed or implemented
                //creating new message from set fields
                Message newMessage = new Message(mContent,
                        mTimestamp, mThreadID, mUid);
                    sendToFirebase(newMessage);
        }
    }

    private boolean sendChatMessage(){
        Log.v(TAG, "Sending message.");
        Message newMessage = new Message(mContent,
                mTimestamp, mThreadID, mAuthorID);
        mChatArrayAdapter.add(newMessage);
        mChatText.setText("");
        return true;
    }

    /**
     * Sends a message to Firebase, triggering onChildAdded which will update the array adapter
     * @param newMessage to be posted to Firebase
     */
    private void sendToFirebase(Message newMessage){
        DatabaseReference newMessagesRef = mMessagesReference.push(); //push a unique message ID string
        newMessagesRef.setValue(newMessage); //store corresponding message content under the key
    }
}