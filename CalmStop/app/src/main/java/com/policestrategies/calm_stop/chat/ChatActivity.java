package com.policestrategies.calm_stop.chat;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.policestrategies.calm_stop.R;
import com.policestrategies.calm_stop.citizen.LoginActivity;

import static java.lang.System.currentTimeMillis;

// TODO: Obtain thread id as intent extra
// TODO: Clean up remaining code
// TODO: Move pushing to firebase outside of this activity

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ChatArrayAdapter mChatArrayAdapter;
    private ListView mListView;
    private EditText mChatText;

    private DatabaseReference mMessagesReference;

    private String mUid;

    private String mThreadID;

    private ChatManager mChatManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatManager = new ChatManager(this);
        mListView = (ListView) findViewById(R.id.listView1);

        mChatArrayAdapter = new ChatArrayAdapter(getApplicationContext(),
                R.layout.activity_chat_singlemessage);

        mListView.setAdapter(mChatArrayAdapter);

        // Default Message object values to be passed to Message constructor
        mChatText = (EditText) findViewById(R.id.chat_text);
        mThreadID = "01";

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


        //Store messageID's under messageReference (messages)
        mMessagesReference = FirebaseDatabase.getInstance().getReference().child("threads")
                .child(mThreadID).child("messages").getRef();

        //ChildEventListener listens for changes to chat
        //When a Message is pushed to firebase, the onChildAdded method of childEventListener activates
        //ChildEventListener is also used for initialization of arrayadapter
        mMessagesReference.addChildEventListener(new ChatChildEventListener(mChatManager));


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
                String timestamp = Long.toString(currentTimeMillis());
                String content = mChatText.getText().toString();
                Message newMessage = new Message(content, timestamp, mUid);
                sendToFirebase(newMessage);
                mChatText.setText("");
        }
    }

    void displayChatMessage(Message message){
        mChatArrayAdapter.add(message);
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