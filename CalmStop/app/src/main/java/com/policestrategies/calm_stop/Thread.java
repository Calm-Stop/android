package com.policestrategies.calm_stop;

public class Thread {
//FIELDS
    private Message[] Messages;
    private int numMessages=0;
    private int timestamp; //stores time at which thread was created/instantiated
    private int threadID;
    private int citizenID;
    private int officerID;

//CONSTRUCTOR
    public Thread(int timestamp, int threadID, int citizenID, int officerID) {
        this.timestamp = timestamp;
        this.threadID = threadID;
        this.citizenID = citizenID;
        this.officerID = officerID;
    }

//GETTERS AND SETTERS
    public  Message[] getMessages() { return Messages; }
    public void setMessages(Message[] messages) { Messages = messages; }

    public int getTimestamp() { return timestamp; }
    public void setTimestamp(int timestamp) { this.timestamp = timestamp; }

    public int getOfficerID() { return officerID; }
    public void setOfficerID(int officerID) { this.officerID = officerID; }

    public int getThreadID() { return threadID; }
    public void setThreadID(int threadID) { this.threadID = threadID; }

    public int getCitizenID() { return citizenID; }
    public void setCitizenID(int citizenID) { this.citizenID = citizenID; }

//INSERTING INTO MESSAGE ARRAY:
    public void insertMessage(Message message) {
        //if there isn't enough space to insert into the Messages[] array
        //instantiate the Messages[] array and make sure there's enough space on the next array slot
        //The size of the message array is 8 at minimum (if not null) and is expanded in multiples of 2
        if (Messages == null) {
            Messages = new Message[8];
        } else if (numMessages == Messages.length) {
            Message[] newMessages = new Message[numMessages*2];
            for (int i = 0; i < numMessages; i++) {
                newMessages[i] = Messages[i];
            }
            Messages = newMessages;
        }
        //insert into the Messages[] array
        Messages[numMessages] = message;
        numMessages++;
    }

} // end class Message
