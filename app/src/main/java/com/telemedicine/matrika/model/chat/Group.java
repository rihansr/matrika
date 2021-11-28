package com.telemedicine.matrika.model.chat;

import com.telemedicine.matrika.model.other.Status;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Group implements Serializable {

    /**
     * Static Fields
     **/
    public static String    ID = "id";
    public static String    USERS = "users";
    public static String    LAST_MESSAGE = "lastMessage";
    public static String    SEEN_BY = "lastMessageSeenBy";

    /**
     * Group Params
     **/
    private String          id;
    private List<String>    users;
    private Message         lastMessage;
    private HashMap<String, Status>  lastMessageSeenBy;

    public Group() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public HashMap<String, Status> getLastMessageSeenBy() {
        return lastMessageSeenBy;
    }

    public void setLastMessageSeenBy(HashMap<String, Status> lastMessageSeenBy) {
        this.lastMessageSeenBy = lastMessageSeenBy;
    }
}
