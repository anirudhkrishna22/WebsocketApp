package com.example.webSocket_app.client;

import com.example.webSocket_app.Message;

import java.util.ArrayList;

public interface MessageListener {
    void onMessageReceived(Message message);
    void onActiveUsersUpdated(ArrayList<String> users);
}
