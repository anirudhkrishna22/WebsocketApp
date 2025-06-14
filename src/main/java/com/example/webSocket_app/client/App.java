package com.example.webSocket_app.client;

import javax.swing.*;

public class App {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientGUI clientGUI = new ClientGUI("Anirudh");
                clientGUI.setVisible(true);
            }
        });
    }
}
