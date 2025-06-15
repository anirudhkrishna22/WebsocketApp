package com.example.webSocket_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {
    // used to send messages to connected clients. It abstracts the process of interacting with a message broker.
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;

    @Autowired
    public WebsocketController(SimpMessagingTemplate messagingTemplate, WebSocketSessionManager sessionManager){
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
    }

    @MessageMapping("/message")
    public void handleMessage(Message message){
        System.out.println("Received message from user: " + message.getUser() + ": " + message.getMessage());
        messagingTemplate.convertAndSend("/topic/messages", message);
        System.out.println("sent message to /topic/messages: " + message.getUser() + ": " + message.getMessage());
    }

    @MessageMapping("/connect")
    public void connectUser(String username){
        sessionManager.addUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println("User " + username + " connected!!");
    }

    @MessageMapping("/disconnect")
    public void disconnect(String username){
        sessionManager.removeUsername(username);
        sessionManager.broadcastActiveUsernames();
        System.out.println("user " + username + " got disconnected");
    }

    @MessageMapping("/request-users")
    public void requestUsers(){
        sessionManager.broadcastActiveUsernames();
        System.out.println("requesting users");
    }
}
