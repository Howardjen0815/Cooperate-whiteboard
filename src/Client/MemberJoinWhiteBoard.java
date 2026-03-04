/**
 * Used for member who want to join the whiteboard, this class do not have any advance feature
 * @author  Yu-Han Jen (1508398) <yjen@student.unimelb.edu.au>
 * @version 1.0
 */

package Client;

import Chat.ChatPanel;
import Chat.UserList;
import DrawAction.DrawAction;
import Chat.Message;
import RemoteInterface.ServerInterface;
import Exception.*;
import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.util.List;

import static Checker.Checker.checkArgsValid;

public class MemberJoinWhiteBoard {
    public static void main(String[] args) {
        String serverPort = checkArgsValid(args);
        String serverIP = args[0];
        String username = args[2];

        try {
            final ServerInterface server = (ServerInterface) Naming.lookup("rmi://" + serverIP + ":" + serverPort + "/Whiteboard");
            System.setProperty("sun.java2d.metal", "false");//my computer gpu has some problem, so I need to set this
            JFrame frame = new JFrame("Whiteboard - Joined as " + username);
            frame.setSize(1100, 800);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    try {
                        server.leaveWhiteBoard(username);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0);
                }
            });

            CanvasPanel canvasPanel = new CanvasPanel();
            canvasPanel.setServer(server, username);
            frame.add(canvasPanel, BorderLayout.CENTER);

            UserList userPanel = new UserList();
            frame.add(userPanel, BorderLayout.EAST);

            ChatPanel chatPanel = new ChatPanel(server, username);
            frame.add(chatPanel, BorderLayout.SOUTH);

            WhiteBoardClient clientLogic = new WhiteBoardClient(username, false, canvasPanel, frame);
            clientLogic.setChatPanel(chatPanel);
            clientLogic.setUserListPanel(userPanel);
            clientLogic.setServer(server);

            ClientServant client = new ClientServant(clientLogic);

            try {
                server.requestJoinWB(username, client);
                System.out.println("Joined successfully.");
            } catch (UserNameExistException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Username Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } catch (JoinRejectException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Join Rejected by Manager", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An unexpected error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            server.registerClient(username, client);

            List<DrawAction> canvas = server.getCanvas();
            clientLogic.applyFullCanvas(canvas);
            List<Message> history = server.getChatHistory();
            for (Message m : history) {

                chatPanel.displayHistoryMessage(m.sender, m.content);
            }

            JPanel toolbar = new JPanel();
            String[] tools = {"freedraw","line", "rect", "oval", "triangle", "erase", "text"};
            String[] fontChoices = {"SansSerif", "Serif", "Calibri"};
            JComboBox<String> fontBox = new JComboBox<>(fontChoices);
            fontBox.setEnabled(false);
            fontBox.setSelectedItem(canvasPanel.getFontName());
            fontBox.addActionListener(e -> {
                canvasPanel.setFontName((String) fontBox.getSelectedItem());
                canvasPanel.repaint();
            });
            for (String tool : tools) {
                JButton btn = new JButton(tool);
                btn.addActionListener(e -> {
                    canvasPanel.setTool(tool);
                    fontBox.setEnabled("text".equals(tool));
                });
                toolbar.add(btn);
            }
            toolbar.add(new JLabel(" | Font:"));
            toolbar.add(fontBox);
            JButton colorBtn = new JButton("Color");
            colorBtn.addActionListener(e -> {
                Color chosen = JColorChooser.showDialog(frame, "Choose Color", Color.BLACK);
                if (chosen != null) canvasPanel.setColor(chosen);
            });
            toolbar.add(colorBtn);

            JComboBox<Integer> thicknessBox = new JComboBox<>(new Integer[]{1,3,5,10,15,30,50});
            thicknessBox.addActionListener(e -> canvasPanel.setThickness((Integer) thicknessBox.getSelectedItem()));
            toolbar.add(new JLabel("Thickness:"));
            toolbar.add(thicknessBox);

            JTextField textField = new JTextField(10);
            textField.addActionListener(e -> canvasPanel.setText(textField.getText()));

            frame.add(toolbar, BorderLayout.NORTH);

            frame.setVisible(true);

        } catch (java.rmi.ConnectException ce) {
            System.err.println("Please ask your manager, the whiteboard is created or not, and check the port and IP address");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
