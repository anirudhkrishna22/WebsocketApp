package com.example.webSocket_app.client;

import com.example.webSocket_app.Message;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ClientGUI extends JFrame implements MessageListener{
    // This is a container. Same like a div in JS. This panel will be displayed in the left side of the UI
    private JPanel connectedUsersPanel, messagePanel;
    private MyStompClient myStompClient;
    private String username;
    private JScrollPane messagePanelScrollPane;

    public ClientGUI(String username) throws ExecutionException, InterruptedException {
        super("User: " + username);
        this.username = username;
        myStompClient = new MyStompClient(this, username);

        // setting dimensions of swing window
        setSize(1218, 685);
        setLocationRelativeTo(null);
        // below stuffs are to make the main window exit after a confirm pop up.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(ClientGUI.this, "Are you sure you want to close this?", "Exit", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION){
                    // disconnect user before disposing the window when clicked yes in the prompt window
                    myStompClient.disconnectUser(username);
                    ClientGUI.this.dispose();
                }
            }
        });

        getContentPane().setBackground(Utilities.PRIMARY_COLOR);
        addGuiComponents();
    }

    private void addGuiComponents(){
        addConnectedUsersComponents();
        addChatComponents();
    }

    private void addConnectedUsersComponents(){
        connectedUsersPanel = new JPanel();
        connectedUsersPanel.setBorder(Utilities.addPadding(10, 10, 10, 10));
        connectedUsersPanel.setLayout(new BoxLayout(connectedUsersPanel, BoxLayout.Y_AXIS));
        connectedUsersPanel.setBackground(Utilities.SECONDARY_COLOR);
        connectedUsersPanel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel connectedUsersLabel = new JLabel("Connected users");
        connectedUsersLabel.setFont(new Font("Inter", Font.BOLD, 18));
        connectedUsersLabel.setForeground(Utilities.TEXT_COLOR);

        // Everywhere we are using border layout because that's what will be ideal for our chat application (Search for images for border layout in java swing)
        connectedUsersPanel.add(connectedUsersLabel);

        add(connectedUsersPanel, BorderLayout.WEST);
    }

    private void addChatComponents(){
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Utilities.TRANSPARENT_COLOR);

        messagePanelScrollPane = new JScrollPane(messagePanel);
        messagePanelScrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        messagePanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messagePanelScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        messagePanelScrollPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                revalidate();
                repaint();
            }
        });

        chatPanel.add(messagePanelScrollPane, BorderLayout.CENTER);

        //messagePanel.add(createChatMessageComponent(new Message("Anirudh", "hello world!!")));

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(Utilities.addPadding(10, 10, 10, 10));
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Utilities.TRANSPARENT_COLOR);

        JTextField inputField = new JTextField();

        // add key event listener to check for enter key type, and then get that string input and then clear the input text field, then display the message in chat message.
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    String input = inputField.getText();

                    // if empty message, just return from this function
                    if(input.isEmpty()){
                        return;
                    }
                    inputField.setText("");

                    //messagePanel.add(createChatMessageComponent(new Message("Anirudh", input)));
                    // need to refresh the GUI, otherwise message won't appear in chat window
                    repaint();
                    revalidate();

                    myStompClient.sendMessage(new Message(username, input));
                }
            }
        });

        inputField.setBackground(Utilities.SECONDARY_COLOR);
        inputField.setForeground(Utilities.TEXT_COLOR);
        inputField.setBorder(Utilities.addPadding(0, 10, 0, 10));
        inputField.setFont(new Font("Inter", Font.PLAIN, 16));
        inputField.setPreferredSize(new Dimension(inputPanel.getWidth(), 50));
        inputPanel.add(inputField, BorderLayout.CENTER);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.CENTER);
    }

    private JPanel createChatMessageComponent(Message message){
        JPanel chatMessage = new JPanel();
        chatMessage.setBackground(Utilities.TRANSPARENT_COLOR);
        chatMessage.setLayout(new BoxLayout(chatMessage, BoxLayout.Y_AXIS));
        chatMessage.setBorder(Utilities.addPadding(20, 20, 10, 20));

        JLabel usernameLabel = new JLabel(message.getUser());
        usernameLabel.setFont(new Font("Inter", Font.BOLD, 18));
        usernameLabel.setForeground(Utilities.TEXT_COLOR);
        chatMessage.add(usernameLabel);

        JLabel messageLabel = new JLabel(message.getMessage());
        messageLabel.setFont(new Font("Inter", Font.PLAIN, 18));
        messageLabel.setForeground(Utilities.TEXT_COLOR);
        chatMessage.add(messageLabel);

        return chatMessage;
    }

    @Override
    public void onMessageReceived(Message message) {
        messagePanel.add(createChatMessageComponent(message));
        revalidate();
        repaint();

        // always place scroll bar till the end. This creates something like automatic scroll
        messagePanelScrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
    }

    @Override
    public void onActiveUsersUpdated(ArrayList<String> users) {
        if(connectedUsersPanel.getComponents().length >= 2){
            connectedUsersPanel.remove(1);
        }

        JPanel userListPanel = new JPanel();
        userListPanel.setBackground(Utilities.TRANSPARENT_COLOR);
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));

        for(String user:users){
            JLabel username = new JLabel();
            username.setText(user);
            username.setForeground(Utilities.TEXT_COLOR);
            username.setFont(new Font("Inter", Font.BOLD, 16));
            userListPanel.add(username);
        }
        connectedUsersPanel.add(userListPanel);
        revalidate();
        repaint();
    }
}
