/**
 * The UserList class used to show the current user who is editiong the whiteboard
 * @version	1.0
 * @author Yu-Han Jen, 1508398, YJEN@student.unimelb.edu.au
 */
package Chat;

import RemoteInterface.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserList extends JPanel {
    private final DefaultListModel<String> listmodel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listmodel);
    private final Map<String,Boolean> drawing = new HashMap<>();

    private ServerInterface server;
    private String localUsername;
    private boolean isManager = false;

    public UserList() {
        list.setCellRenderer((jlist, value, idx, sel, focus) -> {
            JLabel l = new JLabel(value);
            boolean isDrawing = drawing.getOrDefault(value, false);

            l.setOpaque(true);
            if (isDrawing) {
                l.setBackground(new Color(76, 175, 80));  // set the background to gree when any one is drawing
                l.setForeground(Color.WHITE);
            } else {
                // if other do not drawing keep original
                l.setBackground(jlist.getBackground());
                l.setForeground(jlist.getForeground());
            }
            return l;
        });
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(150, 0));
        add(new JLabel(" Online Users ", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(list), BorderLayout.CENTER);
        //used for kick user to let my UI more clean
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem kickItem = new JMenuItem("Kick User");

        kickItem.addActionListener(e -> {
            String selected = list.getSelectedValue();
            if (selected != null && server != null && !selected.equals(localUsername)) {
                //avoid kick the wrong people
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Kick user '" + selected + "'?", "Confirm Kick", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        server.kickUser(selected);
                    } catch (RemoteException ex) {
                        System.err.println("[Client GUI] Kick failed or client already disconnected: " + ex.getMessage());
                    }
                }
            }
        });

        popupMenu.add(kickItem);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isManager) return; //only manager can use this function
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = list.locationToIndex(e.getPoint());
                    list.setSelectedIndex(row);
                    popupMenu.show(list, e.getX(), e.getY());
                }
            }
        });
    }

    public void setUsers(List<String> users) {
        SwingUtilities.invokeLater(() -> {
            listmodel.clear();
            for (String user : users) {
                listmodel.addElement(user);
            }
        });
    }
    public void setUserDrawing(String user, boolean isDrawing) {
        drawing.put(user, isDrawing);
        SwingUtilities.invokeLater(list::repaint);
    }
    public void setServer(ServerInterface server) {
        this.server = server;
    }

    public void setLocalUsername(String username) {
        this.localUsername = username;
    }

    public void setManagerMode(boolean isManager) {
        this.isManager = isManager;
    }
}
