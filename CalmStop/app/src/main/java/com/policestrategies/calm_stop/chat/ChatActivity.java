package com.policestrategies.calm_stop.chat;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.policestrategies.calm_stop.R;

import static java.lang.System.currentTimeMillis;

public class ChatActivity extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter mChatArrayAdapter;
    private ListView mListView;
    private EditText mChatText;
    private Button mButtonSend;

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mMessagesReference;


    private String mCurrentUserID;
    private String mContent;
    private String mAuthorID;
    private String mThreadID;
    private String mTimestamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mButtonSend = (Button) findViewById(R.id.button_send);
        mListView = (ListView) findViewById(R.id.listView1);

        mChatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        mListView.setAdapter(mChatArrayAdapter);

//Default Message object values to be passed to Message constructor
        mChatText = (EditText) findViewById(R.id.chat_text);
        mTimestamp = Long.toString(System.currentTimeMillis());
        mCurrentUserID = mAuthorID = mThreadID = "01";

//Obtaining the user's data from firebase
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
//FIREBASE CODE WORKS ONLY IF SUCCESSFULLY RETRIEVED USER
//PROGRAM SENDS MESSAGES TO ADAPTER WITHOUT STORING IT OTHERWISE
        if (user != null) {
//IF DATABASE ACCESSIBLE:
            //Set References to thread and user
            mCurrentUserID = user.getUid();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference();

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
//END DATABASE CODE
        }
//END FIREBASE CODE

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //setting newMessage fields

                mTimestamp = Long.toString(currentTimeMillis());
                mContent = mChatText.getText().toString();
                //The context for generating threadID has yet to be designed or implemented
                //creating new message from set fields
                Message newMessage = new Message(mContent,
                        mTimestamp, mThreadID, mCurrentUserID);
                if (user != null) {
                    sendToFirebase(newMessage);
                } else {
                    sendChatMessage();
                }
            }
        });
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

//SEND MESSAGE TO ADAPTER
    private boolean sendChatMessage(){
        Log.v(TAG, "Sending message.");
        Message newMessage = new Message(mContent,
                mTimestamp, mThreadID, mAuthorID);
        mChatArrayAdapter.add(newMessage);
        mChatText.setText("");
        return true;
    }

//SEND MESSAGE TO FIREBASE:
    //A message is sent to firebase, triggering onChildEvent.
    //chatArrayAdapter will update only on firebase change.
    //This change will only occur on the following:
    //1. a message is received from firebase
    //2. a message is sent to firebase
    //in no other case should chatarrayadapter receive messages
    private void sendToFirebase(Message newMessage){
        //if user_ID == authorID, set side = false; else set side equal true
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //push a new message onto message tree:
            DatabaseReference newMessagesRef = mMessagesReference.push(); //push a unique message ID string
            newMessagesRef.setValue(newMessage); //store corresponding message content under the key
        }
    }
}