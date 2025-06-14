package com.example.webSocket_app.client;

import com.example.webSocket_app.Message;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;

public class ClientGUI extends JFrame {
    public ClientGUI(String username){
        super("User: " + username);
        setSize(1218, 685);
        setLocationRelativeTo(null);
        // below stuffs are to make the main window exit after a confirm pop up.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(ClientGUI.this, "Are you sure you want to close this?", "Exit", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION){
                    ClientGUI.this.dispose();
                }
            }
        });
    }
}
