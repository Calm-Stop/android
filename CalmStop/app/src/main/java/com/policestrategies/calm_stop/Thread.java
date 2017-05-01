package com.policestrategies.calm_stop;
/*
 * Usage:
 * (NOTE: All constructor fields must be retrieved beforehand
 *  e.g. timestamp is an integer time variable based on UNIX time stamp)
 * 1) Create thread:
 *  Thread thread = new Thread(timestamp(int), threadID(int), citizenID(int), officerID(int);
 * 2) Generate a ChatMessage object each time user types in content to the messager:
 *  ChatMessage message = new ChatMessage(content(String), timestamp(String), authorID(String), threadID(String), messageID(String));
 * 3) Insert into thread as messages are generated:
 *  thread.insertMessage(message(ChatMessage));
 */
public class Thread {
//FIELDS
    private ChatMessage[] chatMessages;
    private int numMessages=0;
    private int timestamp; //stores time at which thread was created/instantiated
    private String threadID;
    private String citizenID;
    private String officerID;

//CONSTRUCTOR
    public Thread(int timestamp, String threadID, String citizenID, String officerID) {
        this.timestamp = timestamp;
        this.threadID = threadID;
        this.citizenID = citizenID;
        this.officerID = officerID;
    }

//GETTERS AND SETTERS
    public  ChatMessage[] getChatMessages() { return chatMessages; }
    public void setChatMessages(ChatMessage[] chatMessages) { this.chatMessages = chatMessages; }

    public int getTimestamp() { return timestamp; }
//    public void setTimestamp(int timestamp) { this.timestamp = timestamp; }

    public String getOfficerID() { return officerID; }
//    public void setOfficerID(String officerID) { this.officerID = officerID; }

    public String getThreadID() { return threadID; }
//    public void setThreadID(String threadID) { this.threadID = threadID; }

    public String getCitizenID() { return citizenID; }
//    public void setCitizenID(String citizenID) { this.citizenID = citizenID; }

//INSERTING INTO MESSAGE ARRAY:
    public void insertMessage(ChatMessage chatMessage) {
        //if there isn't enough space to insert into the chatMessages[] array
        //instantiate the chatMessages[] array and make sure there's enough space on the next array slot
        //The size of the chatMessage array is 8 at minimum (if not null) and is expanded in multiples of 2
        if (chatMessages == null) {
            chatMessages = new ChatMessage[8];
        } else if (numMessages == chatMessages.length) {
            ChatMessage[] newChatMessages = new ChatMessage[numMessages*2];
            for (int i = 0; i < numMessages; i++) {
                newChatMessages[i] = chatMessages[i];
            }
            chatMessages = newChatMessages;
        }
        //insert into the chatMessages[] array
        chatMessages[numMessages] = chatMessage;
        numMessages++;
    }

} // end class ChatMessage
