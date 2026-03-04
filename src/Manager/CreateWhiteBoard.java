/**
 * Used for manager to create the white board, and have advanced feature
 * @author  Yu-Han Jen (1508398) <yjen@student.unimelb.edu.au>
 * @version 1.0
 */
package Manager;

import Chat.ChatPanel;
import Chat.UserList;
import Client.CanvasPanel;
import Client.ClientServant;
import Client.WhiteBoardClient;
import Server.WhiteboardServerServant;
import Chat.Message;
import RemoteInterface.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;

import static Checker.Checker.checkArgsValid;

public class CreateWhiteBoard {
    public static void main(String[] args) {
        String serverPort = checkArgsValid(args);
        String serverIP   = args[0];
        String username   = args[2];
        ServerInterface server;
        try {
            try {
                server = (ServerInterface) Naming.lookup("rmi://" + serverIP + ":" + serverPort + "/Whiteboard");
                System.out.println("Found existing whiteboard server – manager cannot create a duplicate.");
                JOptionPane.showMessageDialog(null,
                        "A whiteboard on this port is already running.\n" +
                                "Please launch the client instead.",
                        "Server Exists", JOptionPane.WARNING_MESSAGE);
                return;
            } catch (Exception lookupFail) {
                System.out.println("No whiteboard found, creating new one…");
            }

            // 2) Create register
            try {
                LocateRegistry.createRegistry(Integer.parseInt(serverPort));
                System.out.println("RMI registry created on port " + serverPort);
            } catch (RemoteException regExists) {
                System.out.println("RMI registry already exists on port " + serverPort);
            }

            // 3) Server initail
            JFrame frame = new JFrame("Whiteboard – Manager Mode");
            frame.setSize(1100, 800);
            frame.setLocationRelativeTo(null);
            CanvasPanel canvasPanel = new CanvasPanel();
            UserList    userPanel   = new UserList();
            WhiteBoardClient clientLogic = new WhiteBoardClient(username, true, canvasPanel, frame);
            clientLogic.setUserListPanel(userPanel);
            ClientServant managerServant = new ClientServant(clientLogic);

            // Rebind to server
            server = new WhiteboardServerServant(username, managerServant);
            Naming.rebind("rmi://" + serverIP + ":" + serverPort + "/Whiteboard", server);
            System.out.println("Whiteboard server created and bound.");

            // Chat Panel
            ChatPanel chatPanel = new ChatPanel(server, username);
            clientLogic.setChatPanel(chatPanel);

            // Register client
            server.registerClient(username, managerServant);
            canvasPanel.setServer(server, username);
            userPanel.setServer(server);
            userPanel.setLocalUsername(username);
            userPanel.setManagerMode(true);
            clientLogic.setServer(server);

            // 4) Close Window
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            final ServerInterface srv = server;
            frame.addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(WindowEvent e) {
                    //still need to add the pop ip window to avoid user forget to save
                    int opt = JOptionPane.showConfirmDialog(frame,
                            "Have you already saved the whiteboard?\nYes  → leave immediately ;  No  → return.",
                            "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (opt != JOptionPane.YES_OPTION) return;

                    try {
                        srv.shutdownBoard(username);
                        JOptionPane.showMessageDialog(frame,
                                "Whiteboard has been closed and all users disconnected.",
                                "Close Successful", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) { ex.printStackTrace(); }
                    System.exit(0);
                }
            });

            // Layout
            frame.add(canvasPanel, BorderLayout.CENTER);
            frame.add(userPanel,   BorderLayout.EAST);
            frame.add(chatPanel,   BorderLayout.SOUTH);

            //Toolbar
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

            // Menu (File)
            JMenuBar bar = new JMenuBar();
            JMenu file   = new JMenu("File");
            JMenuItem newOpt   = new JMenuItem("New");
            JMenuItem openOpt  = new JMenuItem("Open");
            JMenuItem saveOpt  = new JMenuItem("Save");
            JMenuItem saveAsOpt= new JMenuItem("Save As");
            JMenuItem closeOpt = new JMenuItem("Close");
            file.add(newOpt); file.add(openOpt); file.add(saveOpt); file.add(saveAsOpt); file.addSeparator(); file.add(closeOpt);
            bar.add(file);
            frame.setJMenuBar(bar);

            //Menu handlers
            newOpt.addActionListener(e -> { try { srv.createNewWhiteBoard(username);} catch (RemoteException ex){ex.printStackTrace();} });
            openOpt.addActionListener(e -> {
                JFileChooser ch = new JFileChooser();
                if (ch.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION){
                    try { srv.loadWhiteboard(ch.getSelectedFile().getAbsolutePath()); } catch (RemoteException ex){ex.printStackTrace();}
                }
            });
            //used for save option
            saveOpt.addActionListener(e -> {
                try {
                    srv.saveOrUpdateCanvas();
                    JOptionPane.showMessageDialog(frame,"Canvas saved.","Saved",JOptionPane.INFORMATION_MESSAGE);
                } catch (RemoteException ex) {
                    if (ex.getMessage()!=null && ex.getMessage().contains("No existing save path")) {
                        saveAsOpt.doClick();
                    } else {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame,"Save failed: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            //used for save as option
            saveAsOpt.addActionListener(e -> {
                JFileChooser ch = new JFileChooser();
                if (ch.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION){
                    String path = ch.getSelectedFile().getAbsolutePath();
                    if (!path.endsWith(".json")) path += ".json";
                    try {
                        srv.saveNewWhiteboard(path);
                        JOptionPane.showMessageDialog(frame,"Canvas saved to:\n"+path,"Save As",JOptionPane.INFORMATION_MESSAGE);
                    } catch (RemoteException ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame,"Failed to save:\n"+ex.getMessage(),"Save Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            closeOpt.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

            // Load chat history
            List<Message> history = server.getChatHistory();
            for (Message m : history) {
                clientLogic.receiveMessage(m.sender, m.content);
            }
            frame.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "error:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}