/**
 * Client implement the Client servant and client interface
 * @author  Yu-Han Jen (1508398) <yjen@student.unimelb.edu.au>
 * @version 1.0
 */

package Client;
import Chat.ChatPanel;
import Chat.UserList;
import RemoteInterface.ServerInterface;
import DrawAction.DrawAction;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class WhiteBoardClient {
    private final String username;
    private final boolean isManager;
    private final CanvasPanel canvasPanel;
    private ChatPanel chatPanel;
    private final JFrame window;
    private UserList userListPanel;
    private ServerInterface server;
    public WhiteBoardClient(String username, boolean isManager, CanvasPanel canvasPanel, JFrame window) {
        this.username = username;
        this.isManager = isManager;
        this.canvasPanel = canvasPanel;
        this.window = window;
    }
    //used for client connect
    public boolean approveJoin(String newUser){
        final boolean[] result = {false};
        try {
            SwingUtilities.invokeAndWait(() -> {
                int opt = JOptionPane.showConfirmDialog(null,
                        newUser + " want to join the whiteboard!",
                        "Join Request", JOptionPane.YES_NO_OPTION);
                result[0] = (opt == JOptionPane.YES_OPTION);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result[0];
    }

    public synchronized void applyDrawCommand(DrawAction cmd) {
        System.out.println("[" + username + "] Apply draw command: " + cmd);
        canvasPanel.addCommand(cmd);
        canvasPanel.repaint();
    }
    public void setUserListPanel(UserList pnl) { this.userListPanel = pnl; }
    public void applyFullCanvas(List<DrawAction> canvasState) {
        canvasPanel.setCommands(canvasState);
        canvasPanel.repaint();
    }

    public void updateUserList(List<String> users) {
        if (userListPanel != null) userListPanel.setUsers(users);
        System.out.println( username +" Online users: " + users);

    }

    public void forceDisconnect(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(window, message,
                    "Disconnected", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        });
    }
    public void receiveMessage(String sender, String msg)  {
        if (chatPanel != null) {
            chatPanel.receiveMessage(sender, msg);
        } else {
            System.out.println(sender + ": " + msg);
        }
    }
    public boolean isManager() {
        return isManager;
    }

    public String getUsername() {
        return username;
    }
    public void setServer(ServerInterface server) {
        this.server = server;
    }
    public void setChatPanel(ChatPanel chatPanel) {
        this.chatPanel = chatPanel;
    }

    public void updateWhoDrawing(String username, boolean drawing) {
        SwingUtilities.invokeLater(() -> userListPanel.setUserDrawing(username, drawing));
    }

    public void clearCanvas() {
        SwingUtilities.invokeLater(() -> {
            canvasPanel.setCommands(Collections.emptyList());
            canvasPanel.repaint();
        });
    }
}

