package com.policestrategies.calm_stop.citizen;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.policestrategies.calm_stop.ChatArrayAdapter;
import com.policestrategies.calm_stop.ChatMessage;
import com.policestrategies.calm_stop.R;

public class ChatActivity extends Activity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private DatabaseReference threadReference;
    private DatabaseReference userProfileReference;
    private String UserID;
    private String name;

    Intent intent;
    private boolean side = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        setContentView(R.layout.activity_chat);

        buttonSend = (Button) findViewById(R.id.button_send);
        listView = (ListView) findViewById(R.id.listView1);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserID = user.getUid();
            databaseRef = FirebaseDatabase.getInstance().getReference();
            threadReference = databaseRef.child("stops").child("temp_stop_id")
                    .child("thread").getRef();
            userProfileReference = databaseRef.child("citizen")
                    .child(UserID).child("profile").getRef();
            name = userProfileReference.child("first_name").toString();
        } else {
            UserID = "Random UserID String (placeholder)";
            name = "[Sender_Name]:";
        }

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        listView.setAdapter(chatArrayAdapter);
        chatText = (EditText) findViewById(R.id.chat_text);
        chatText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
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

    private boolean sendChatMessage(){
        //if user_ID == authorID, set side = false; else set side equal true
        side = false; //output on the right side
        chatArrayAdapter.add(new ChatMessage(side,
                chatText.getText().toString(), 0, UserID, name));
        chatText.setText("");
        return true;
    }

}