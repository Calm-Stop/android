package com.policestrategies.calm_stop.citizen;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
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

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.policestrategies.calm_stop.ChatArrayAdapter;
import com.policestrategies.calm_stop.ChatMessage;
import com.policestrategies.calm_stop.R;

import java.util.Iterator;

import static java.lang.System.currentTimeMillis;

public class ChatActivity extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference stop_thread_reference;
    private DatabaseReference messagesReference;
    private DatabaseReference userProfileReference;
    private DatabaseReference SingleMessageReference;
    private DatabaseReference threadsReference;


    private String CurrentUserID;

    private String key;
    private String content;
    private String authorID;
    private String threadID;
    private String timestamp;

    Intent intent;
    private String side = "right";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_chat);
        buttonSend = (Button) findViewById(R.id.button_send);
        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        listView.setAdapter(chatArrayAdapter);
//Default ChatMessage object values to be passed to ChatMessage constructor
        chatText = (EditText) findViewById(R.id.chat_text);
        Log.d(TAG, "Timestamp.");

        timestamp = Long.toString(System.currentTimeMillis());
        CurrentUserID = authorID = threadID = "000000000000";
        side = "right";

        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "Get user.");
        final FirebaseUser user = mAuth.getCurrentUser();
        Log.d(TAG, "Finished getting user.");
//FIREBASE CODE
        if (user != null) {
//IF DATABASE ACCESSIBLE:
            //Set References to thread and user
            CurrentUserID = user.getUid();
            databaseRef = FirebaseDatabase.getInstance().getReference();

            threadsReference = databaseRef.child("threads").
                    child(threadID).getRef();
            //Store messageID's under messageReference (messages)
            messagesReference = threadsReference.child("messages").getRef();

            //Storing thread id under stops -> stopID -> threadID
            stop_thread_reference = databaseRef.child("stops")
                    .child("temp_stop_id").child(threadID).getRef();
            stop_thread_reference.setValue(threadID);
            userProfileReference = databaseRef.child("citizen")
                    .child(CurrentUserID).child("profile").getRef();

//ChildEventListener listens for changes to chat
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "In Citizen/ChatActivity, onChildAdded");

//                    key = dataSnapshot.getKey();
                    content = dataSnapshot.child("content").getValue().toString();
                    authorID = dataSnapshot.child("authorID").getValue().toString();
                    side = authorID.equalsIgnoreCase(CurrentUserID) ? "left" : "right";
                    timestamp = dataSnapshot.child("timestamp").getValue().toString();
                    threadID = dataSnapshot.child("threadID").getValue().toString();
                    sendChatMessage();
                    chatText.setText("");
                    // ...
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                    //Comment newComment = dataSnapshot.getValue(Comment.class);
                    String Key = dataSnapshot.getKey();

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                    String Key = dataSnapshot.getKey();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                    //Comment movedComment = dataSnapshot.getValue(Comment.class);
                    //String commentKey = dataSnapshot.getKey();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(getBaseContext(), "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            messagesReference.addChildEventListener(childEventListener);
//            messagesReference.addValueEventListener(AttachListener());
//END DATABASE CODE
        }
//END FIREBASE CODE

        //ON KEY PRESS may not be necessary
        chatText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //return sendChatMessage();
                    return true;
                }
                return false;
            }
        });
        //ALSO SEND TO FIREBASE
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //setting newMessage fields

                timestamp = Long.toString(currentTimeMillis());
                side = "right";
                content = chatText.getText().toString();
                //The context for generating threadID has yet to be designed or implemented
                //creating new message from set fields
                ChatMessage newMessage = new ChatMessage(side, content,
                        timestamp, threadID, authorID);
                //sendToFirebase(newMessage);
                if (user != null) {
                    sendToFirebase(newMessage);
                } else {
                    sendChatMessage();
                }
                //SEND MESSAGE TO FIREBASE INSTEAD
            }
        });
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

//SEND MESSAGE TO ADAPTER
    private boolean sendChatMessage(){
        //if user_ID == authorID, set side = false; else set side equal true
        Log.v(TAG, "Sending message.");
        ChatMessage newMessage = new ChatMessage(side,
                content, timestamp, threadID, authorID);
        chatArrayAdapter.add(newMessage);
        chatText.setText("");
        return true;
    }

//SEND MESSAGE TO FIREBASE:
    //A message is sent to firebase, triggering onChildEvent.
    //chatArrayAdapter will update only on firebase change.
    //This change will only occur on the following:
    //1. a message is received from firebase
    //2. a message is sent to firebase
    //in no other case should chatarrayadapter receive messages
    private void sendToFirebase(ChatMessage newMessage){
        //if user_ID == authorID, set side = false; else set side equal true
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //push a new message onto message tree:
            DatabaseReference newMessagesRef = messagesReference.push(); //push a unique message ID string
            newMessagesRef.setValue(newMessage); //store corresponding message content under the key
        }
    }
}