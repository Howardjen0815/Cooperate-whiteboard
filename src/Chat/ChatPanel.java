/**
 * The ChatPanel class used to show the message for all users
 * @version	1.0
 * @author Yu-Han Jen, 1508398, YJEN@student.unimelb.edu.au
 */
package Chat;

import RemoteInterface.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class ChatPanel extends JPanel {
    private ServerInterface server;
    private String username;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    public ChatPanel(ServerInterface server, String username) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 100));
        this.server = server;
        this.username = username;
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
        //used for sent the message
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        //content
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }
    //used for send the message through server to all users
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            chatArea.append("Me: " + text + "\n");
            inputField.setText("");
            try {
                server.sendChat(username, text);   // Broadcast the message
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }

    }
    //show the history message for user who disconnect and connect back
    public void displayHistoryMessage(String sender, String msg) {
        if (sender.equals(username)) {
            chatArea.append("Me: " + msg + "\n");
        } else {
            chatArea.append(sender + ": " + msg + "\n");
        }
    }

    public void receiveMessage(String sender, String msg) {
        if (sender.equals(username)) return;//not let your self to use this,
        chatArea.append(sender + ": " + msg + "\n");
    }
}
