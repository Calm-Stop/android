package com.policestrategies.calm_stop.chat;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.policestrategies.calm_stop.R;

import java.util.ArrayList;

class ChatArrayAdapter extends ArrayAdapter<Message> {

    private ArrayList<Message> mMessageList;

    ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mMessageList = new ArrayList<>();
    }

    public void add(Message message) {
        super.add(message);
        mMessageList.add(message);
    }

    public int getCount() {
        return mMessageList.size();
    }

    public Message getItem(int index) {
        return mMessageList.get(index);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        String currentUserID = user.getUid();
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
        }
        LinearLayout singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        Message messageObj = getItem(position);
        boolean right = messageObj.getAuthorID().equalsIgnoreCase(currentUserID);
        TextView chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(messageObj.getContent());
        chatText.setBackgroundResource(!right ? R.drawable.rect_fgreen : R.drawable.rect_fyellow);
        singleMessageContainer.setGravity(!right ? Gravity.LEFT : Gravity.RIGHT);
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}