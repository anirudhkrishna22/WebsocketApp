package com.example.webSocket_app.client;

import com.example.webSocket_app.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MyStompClient {
    private StompSession session;
    private String username;

    public MyStompClient(MessageListener messageListener, String username) throws ExecutionException, InterruptedException {
        this.username = username;

        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler sessionHandler = new MyStompSessionHandler(messageListener, username);

        String url = "ws://localhost:8080/ws";

        session = stompClient.connectAsync(url, sessionHandler).get();

        //new Scanner(System.in).nextLine();
    }

    public void sendMessage(Message message){
        try{
            session.send("/app/message", message);
            System.out.println("Message sent: " + message.getMessage());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void disconnectUser(String username){
        session.send("/app/disconnect", username);
        System.out.println("Disconnected user: " + username);
    }
}
