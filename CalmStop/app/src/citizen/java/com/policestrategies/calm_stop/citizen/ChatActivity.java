package com.policestrategies.calm_stop.citizen;

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

import com.policestrategies.calm_stop.ChatArrayAdapter;
import com.policestrategies.calm_stop.ChatMessage;
import com.policestrategies.calm_stop.R;

import static java.lang.System.currentTimeMillis;

public class ChatActivity extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference stop_reference;
    private DatabaseReference messagesReference;


    private String CurrentUserID;
    private String content;
    private String authorID;
    private String threadID;
    private String timestamp;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        buttonSend = (Button) findViewById(R.id.button_send);
        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        listView.setAdapter(chatArrayAdapter);

//Default ChatMessage object values to be passed to ChatMessage constructor
        chatText = (EditText) findViewById(R.id.chat_text);
        timestamp = Long.toString(System.currentTimeMillis());
        CurrentUserID = authorID = threadID = "01";

//Obtaining the user's data from firebase
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

//FIREBASE CODE WORKS ONLY IF SUCCESSFULLY RETRIEVED USER
//PROGRAM SENDS MESSAGES TO ADAPTER WITHOUT STORING IT OTHERWISE
        if (user != null) {
//IF DATABASE ACCESSIBLE:
            //Set References to thread and user
            CurrentUserID = user.getUid();
            databaseRef = FirebaseDatabase.getInstance().getReference();

            //Store messageID's under messageReference (messages)
            messagesReference = databaseRef.child("threads").
                    child(threadID).child("messages").getRef();

            //Obtaining & storing via reference to stops -> stopID
            //DEBUGF: stopID is currently arbitrarily assigned; not retrieved from anywhere
            stop_reference = databaseRef.child("stops")
                    .child("temp_stop_id").getRef();
            //stopID -> threadID, officerID, citizenID, time_of_stop, date_of_stop
            stop_reference.child("threadID").setValue(threadID);
            stop_reference.child("officerID").setValue(""); //DEBUGF: We currently do not retrieve this value from anywhere
            stop_reference.child("citizenID").setValue(CurrentUserID);
            stop_reference.child("date_of_stop").setValue("");
            stop_reference.child("time_of_stop").setValue("");

//ChildEventListener listens for changes to chat
//When a ChatMessage is pushed to firebase, the onChildAdded method of childEventListener activates
//ChildEventListener is also used for initialization of arrayadapter
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "In Citizen/ChatActivity, onChildAdded");

                    content = dataSnapshot.child("content").getValue().toString();
                    authorID = dataSnapshot.child("authorID").getValue().toString();
                    timestamp = dataSnapshot.child("timestamp").getValue().toString();
                    threadID = dataSnapshot.child("threadID").getValue().toString();
                    sendChatMessage();
                    chatText.setText("");
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
            };
            messagesReference.addChildEventListener(childEventListener);
//END DATABASE CODE
        }
//END FIREBASE CODE

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //setting newMessage fields

                timestamp = Long.toString(currentTimeMillis());
                content = chatText.getText().toString();
                //The context for generating threadID has yet to be designed or implemented
                //creating new message from set fields
                ChatMessage newMessage = new ChatMessage(content,
                        timestamp, threadID, CurrentUserID);
                if (user != null) {
                    sendToFirebase(newMessage);
                } else {
                    sendChatMessage();
                }
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
        ChatMessage newMessage = new ChatMessage(content,
                timestamp, threadID, authorID);
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