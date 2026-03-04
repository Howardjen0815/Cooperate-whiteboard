/**
 * The CanvasPanel class store the canvas and define the action provide to the user
 * @version	1.0
 * @author Yu-Han Jen, 1508398, YJEN@student.unimelb.edu.au
 */
package Client;

import DrawAction.DrawAction;
import RemoteInterface.ServerInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class CanvasPanel extends JPanel {
    private final List<DrawAction> commands; //store all the action in the commands
    private ServerInterface server;  // let srever send the drawing to all user include the clients
    private String username;
    private int startX, startY;
    private int currentX, currentY;
    private boolean isDrawing = false;
    private String currentTool = "freedraw"; //default set the freedraw
    private Color currentColor = Color.BLACK;//my default I will like to set it as black
    private int thickness = 2;
    private String textToDraw = "";
    private String currentFontName = "SansSerif";   // Default
    private int    currentFontSize = 14;


    public CanvasPanel() {
        this.commands = new ArrayList<>();
        setBackground(Color.WHITE);
        this.currentTool = "freedraw";//default is the freedraw
        addMouseListener(new MouseAdapter() {
            //only non text can move directly
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    server.broadcastWhoDraw(username, true);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
                if (!currentTool.equals("text")) {
                    startX = e.getX();
                    startY = e.getY();
                    isDrawing = true;
                    return;
                }
                int x = e.getX();
                int y = e.getY();

                JTextField textField = new JTextField();
                textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
                textField.setBounds(x, y, 150, 24);
                setLayout(null); //
                add(textField);
                textField.requestFocusInWindow();

                textField.addActionListener(evt -> {
                    String text = textField.getText().trim();
                    if (!text.isEmpty()) {
                        DrawAction action = new DrawAction("text", x, y, x, y, currentColor, thickness, false);
                        action.text = text;
                        action.fontName  = currentFontName;
                        action.fontSize  = currentFontSize;
                        addCommand(action);
                        repaint();

                        if (server != null) {
                            try {
                                server.sendAllUser(action);
                            } catch (RemoteException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    remove(textField);
                    repaint();
                    try {
                        server.broadcastWhoDraw(username, false);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                });
                textField.addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusLost(java.awt.event.FocusEvent evt) {
                        remove(textField);
                        repaint();
                        try {
                            server.broadcastWhoDraw(username, false);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                revalidate();
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!"text".equals(currentTool)) {//let user and manager input text can have the green background color
                    try {
                        server.broadcastWhoDraw(username, false);
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                               }
                      }
                if (!isDrawing) return;
                isDrawing = false;
                currentX = e.getX();
                currentY = e.getY();

                DrawAction action;
                switch (currentTool) {
                    case "freedraw":
                        action = new DrawAction("line", startX, startY, currentX, currentY, currentColor, thickness, false);
                        break;
                    case "rect":
                        action = new DrawAction("rect", startX, startY, currentX, currentY, currentColor, thickness, false);
                        break;
                    case "oval":
                        action = new DrawAction("oval", startX, startY, currentX, currentY, currentColor, thickness, false);
                        break;
                    case "line":
                        action = new DrawAction(currentTool, startX, startY, currentX, currentY, currentColor, thickness, false);
                        break;
                    case "triangle":
                        action = new DrawAction("triangle", startX, startY, currentX, currentY, currentColor, thickness, false);
                        break;
                    case "erase":
                        action = new DrawAction("line", startX, startY, currentX, currentY, Color.WHITE, thickness, true);
                        break;
                    case "text":
                        action = new DrawAction("text", startX, startY, startX, startY, currentColor, thickness, false);
                        action.text = textToDraw;
                        break;
                    default:
                        return;
                }
                addCommand(action);
                repaint();
                // Broadcast to all user
                if (server != null) {
                    try {
                        System.out.println("[" + username + "] sendAllUser");
                        server.sendAllUser(action);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        //used for freedraw and erase, it can draw and clean the whiteboard where the mouse move
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int newX = e.getX();
                int newY = e.getY();

                if (isDrawing && (currentTool.equals("freedraw") || currentTool.equals("erase"))) {
                    Color colorToUse = currentTool.equals("erase") ? Color.WHITE : currentColor;
                    boolean isEraser = currentTool.equals("erase");
                    DrawAction action = new DrawAction("line", startX, startY, newX, newY, colorToUse, thickness, isEraser);
                    addCommand(action);
                    repaint();
                    try {
                        server.sendAllUser(action);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                    startX = newX;
                    startY = newY;
                } else {
                    currentX = newX;
                    currentY = newY;
                    repaint();
                }
            }
        });
    }
    public void setServer(ServerInterface server, String username) {
        this.server = server;
        this.username = username;
    }

    public void setTool(String tool) { this.currentTool = tool; }
    public void setColor(Color color) { this.currentColor = color; }
    public void setThickness(int thickness) { this.thickness = thickness; }
    public void setText(String text) { this.textToDraw = text; }

    public void addCommand(DrawAction cmd) { commands.add(cmd); }
    public void setCommands(List<DrawAction> newCommands) { commands.clear(); commands.addAll(newCommands); }
    public void setFontName(String name) { this.currentFontName = name; }
    public void setFontSize(int size) { this.currentFontSize = size; }
    public String getFontName() { return currentFontName; }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D draw2d = (Graphics2D) g;
        for (DrawAction cmd : commands) {
            draw2d.setStroke(new BasicStroke(cmd.thickness));
            draw2d.setColor(cmd.isEraser ? Color.WHITE : new Color(cmd.r, cmd.g, cmd.b));
            switch (cmd.type) {
                case "line": draw2d.drawLine(cmd.x1, cmd.y1, cmd.x2, cmd.y2);
                break;
                case "rect": draw2d.drawRect(Math.min(cmd.x1, cmd.x2), Math.min(cmd.y1, cmd.y2), Math.abs(cmd.x2 - cmd.x1), Math.abs(cmd.y2 - cmd.y1));
                break;
                case "oval": draw2d.drawOval(Math.min(cmd.x1, cmd.x2), Math.min(cmd.y1, cmd.y2), Math.abs(cmd.x2 - cmd.x1), Math.abs(cmd.y2 - cmd.y1));
                break;
                case "triangle":
                    draw2d.drawPolygon(new int[]{cmd.x1, cmd.x2, (cmd.x1 + cmd.x2) / 2}, new int[]{cmd.y1, cmd.y1, cmd.y2}, 3);
                    break;
                case "text":
                    draw2d.setFont(new Font(cmd.fontName, Font.PLAIN, cmd.fontSize));
                    draw2d.drawString(cmd.text, cmd.x1, cmd.y1);
                    break;
            }
        }
        //let the user can drag the mouse, and show the actual line of graph
        if (isDrawing && !currentTool.equals("erase")) {
            draw2d.setStroke(new BasicStroke(thickness));
            draw2d.setColor(currentColor);

            switch (currentTool) {
                case "line":
                    draw2d.drawLine(startX, startY, currentX, currentY);
                    break;
                case "rect":
                    draw2d.drawRect(Math.min(startX, currentX), Math.min(startY, currentY),
                            Math.abs(currentX - startX), Math.abs(currentY - startY));
                    break;
                case "oval":
                    draw2d.drawOval(Math.min(startX, currentX), Math.min(startY, currentY),
                            Math.abs(currentX - startX), Math.abs(currentY - startY));
                    break;
                case "triangle":
                    int[] xs = {startX, currentX, (startX + currentX) / 2};
                    int[] ys = {startY, startY, currentY};
                    draw2d.drawPolygon(xs, ys, 3);
                    break;
            }
        }

    }
}
