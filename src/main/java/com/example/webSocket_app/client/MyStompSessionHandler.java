package com.example.webSocket_app.client;

import com.example.webSocket_app.Message;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

// This class will handle what will happen after user connects or disconnects with the websocket. It basically manages the websocket connection
public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private String username;
    private MessageListener messageListener;

    public MyStompSessionHandler(MessageListener messageListener, String username){
        this.messageListener = messageListener;
        this.username = username;
    }

    // This method is going to be used once the user has been successfully connected to the websocket server.
    // So after we connect to our websocket server, we want to automatically subscribe to the /topic/messages route. This end point is where the websocket server will broadcast all the messages to the connected users.
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        try {
            System.out.println("client connected!");
            // To achieve that, we will subscribe to that route (/topic/messages) and then instantiate a StompFrameHandler object.
            session.subscribe("/topic/messages", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return Message.class;
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    try {
                        if (payload instanceof Message) {
                            Message message = (Message) payload;
                            messageListener.onMessageReceived(message);
                            System.out.println("Received message: " + message.getUser() + ": " + message.getMessage());
                        } else {
                            System.out.println("Received unexpected payload type: " + payload.getClass());
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
            System.out.println("client subscribed to /topic/messages");

            session.subscribe("/topic/users", new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders headers) {
                    return new ArrayList<String>().getClass();
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                    try{
                        if(payload instanceof ArrayList){
                            ArrayList<String> activeUsers = (ArrayList<String>) payload;
                            messageListener.onActiveUsersUpdated(activeUsers);
                            System.out.println("received active users" + activeUsers);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            System.out.println("subscribed to /topic/users");

            session.send("/app/connect", username);
            // asking the websocket server to broadcast active users again. This is needed when a new client gets connected (due to threading issues)
            session.send("/app/request-users", "");

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.err.println("Transport error: " + exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.err.println("STOMP error: " + exception.getMessage());
        exception.printStackTrace();
    }
}
