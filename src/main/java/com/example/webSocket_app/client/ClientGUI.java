package com.example.webSocket_app.client;

import com.example.webSocket_app.Message;

import java.util.concurrent.ExecutionException;

public class ClientGUI {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MyStompClient myStompClient = new MyStompClient("Anirudh");
        myStompClient.sendMessage(new Message("Anirudh", "Hello World"));
    }
}
