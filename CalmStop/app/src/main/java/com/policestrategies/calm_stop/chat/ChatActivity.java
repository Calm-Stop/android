package com.policestrategies.calm_stop.chat;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.policestrategies.calm_stop.R;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mListView;
    private EditText mChatText;
    private ChatArrayAdapter mChatArrayAdapter;

    private String mUid;
    private ChatManager mChatManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatManager = new ChatManager(this);
        mListView = (ListView) findViewById(R.id.chat_list_view);


        mListView.setAdapter(mChatArrayAdapter);

        mChatText = (EditText) findViewById(R.id.chat_text);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            mUid = firebaseAuth.getCurrentUser().getUid();
        } else {
            firebaseAuth.signOut();
            finish();
        }

        mChatArrayAdapter = new ChatArrayAdapter(getApplicationContext(),
                R.layout.chat_message, mUid);

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
                String content = mChatText.getText().toString();
                if (content.isEmpty()) {
                    return;
                }
                Message newMessage = new Message(content, System.currentTimeMillis(), mUid);
                mChatManager.sendToFirebase(newMessage);
                mChatText.setText("");
        }
    }

    void displayChatMessage(Message message){
        mChatArrayAdapter.add(message);
    }

}