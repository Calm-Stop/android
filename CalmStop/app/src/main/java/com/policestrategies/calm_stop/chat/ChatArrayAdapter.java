package com.policestrategies.calm_stop.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.policestrategies.calm_stop.R;

import java.util.ArrayList;

class ChatArrayAdapter extends ArrayAdapter<Message> {

    private String mCurrentUserId;
    private ArrayList<Message> mMessageList;

    ChatArrayAdapter(Context context, int textViewResourceId, String currentUserId) {
        super(context, textViewResourceId);
        mMessageList = new ArrayList<>();
        mCurrentUserId = currentUserId;
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
        View row = inflateRow(convertView, parent);
        setUpChatView(row, position);
        return row;
    }

    private View inflateRow(View row, ViewGroup parent) {
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_message, parent, false);
        }
        return row;
    }

    private boolean isSameAuthor(Message message) {
        return message.getAuthor().equalsIgnoreCase(mCurrentUserId);
    }

    private void setUpChatView(View row, int position) {
        Message chatMessage = getItem(position);
        if (chatMessage == null) {
            return;
        }

        TextView chatTextView;
        LinearLayout chatMessageContainer;

        chatTextView = (TextView) row.findViewById(R.id.chat_message_textview);
        chatMessageContainer = (LinearLayout) row.findViewById(R.id.chat_message_linear_layout);

        boolean sameAuthor = isSameAuthor(chatMessage);
        chatTextView.setText(chatMessage.getContent());
        chatMessageContainer.setGravity(!sameAuthor ? Gravity.LEFT : Gravity.RIGHT);
        chatTextView.setBackgroundResource(!sameAuthor ? R.drawable.rect_fgreen :
                R.drawable.rect_fyellow);
    }

} // end class ChatArrayAdapter