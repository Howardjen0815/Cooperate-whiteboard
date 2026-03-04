/**
 * Acts as the intermediary that relays requests between the white-board server, the manager, and connected clients.
 * @author  Yu-Han Jen (1508398) <yjen@student.unimelb.edu.au>
 * @version 1.0
 */
package Server;


import Chat.Message;
import RemoteInterface.ClientInterface;
import com.google.gson.reflect.TypeToken;
import DrawAction.DrawAction;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import Exception.*;
public class WhiteboardServer {
    private final Map<String, ClientInterface> clients = new ConcurrentHashMap<>();
    private List<DrawAction> canvas = new ArrayList<>();
    private final String managerUsername;
    private final ClientInterface managerClient;
    private final List<Message> chatLog = new ArrayList<>();
    private final Gson gson = new Gson();
    private String currentSavePath = null;

    public WhiteboardServer(String managerUsername, ClientInterface managerClient) {
        this.managerUsername = managerUsername;
        this.managerClient   = managerClient;
        clients.put(managerUsername, managerClient);
        System.out.println("[Server] Manager auto-registered: " + managerUsername);
    }


    public synchronized void sendChat(String sender, String msg) throws RemoteException {
        Message message = new Message(sender, msg);
        if (!sender.equals("System")) {
            chatLog.add(message);
        }

        for (Map.Entry<String, ClientInterface> entry : clients.entrySet()) {
            try {
                entry.getValue().receiveMessage(sender, msg);
            } catch (RemoteException ignored) {}
        }
    }
    public synchronized List<Message> getChatHistory() throws RemoteException {
        return new ArrayList<>(chatLog);
    }

    public synchronized void requestJoinWB(String clientName, ClientInterface client)
            throws RemoteException, UserNameExistException, JoinRejectException {

        if (clients.containsKey(clientName)) {
            throw new UserNameExistException(clientName);
        }

        boolean approved = managerClient.approveJoin(clientName);  // 向 Manager 詢問

        if (!approved) {
            throw new JoinRejectException(clientName);
        }

        // add to list
        clients.put(clientName, client);
        System.out.println("[Server] " + clientName + " approved and registered.");
    }

    public void shutdownBoard(String managerName) throws RemoteException {
        clients.remove(managerName);
        broadcastUserList();
        clearAllCanvas();
        for (ClientInterface c : clients.values()) {
            try {
                c.forceDisconnect("Manager has already closed the whiteboard.");

            }
        catch (Exception ignore) {}
            }
            clients.clear();
            canvas.clear();
            System.exit(0);
    }

    public void registerClient(String username, ClientInterface client) throws RemoteException {
        clients.put(username, client);
        client.receiveCanvas(canvas);
        broadcastUserList();
    }

    public synchronized void sendAllUser(DrawAction cmd) throws RemoteException {
        canvas.add(cmd);
        for (ClientInterface client : clients.values()) {
            client.receiveDraw(cmd);
        }
    }

    public List<String> getAllUsers() {
        return new ArrayList<>(clients.keySet());
    }

    public List<DrawAction> getCanvas() {
        return new ArrayList<>(canvas);
    }

    public void kickUser(String userName) throws RemoteException {
        ClientInterface client = clients.remove(userName);
        if (client != null) {
            try {
                client.forceDisconnect("You have been kicked by the manager.");
            } catch (Exception ignored) {}

            leaveWhiteBoard(userName); //let the user leave the whiteboard
        }
    }

    public void saveWhiteboard(String filePath) throws RemoteException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(canvas, writer);//save the json file
            currentSavePath = filePath;
        } catch (IOException e) {
            throw new RemoteException("Failed to save file: " + e.getMessage());
        }
    }
    //for the user who do not save whiteboard before, and create the new pathname
    public void saveNewWhiteboard(String filePath) throws RemoteException {
        if (filePath == null || filePath.trim().isEmpty()) {
            filePath = "whiteboard.json";
        }
        currentSavePath = filePath;
        saveWhiteboard(filePath);
    }
    //load the whiteboard from json to Gson
    public void loadWhiteboard(String fileName) throws RemoteException {
        try (Reader reader = new FileReader(fileName)) {
            Type listType = new TypeToken<List<DrawAction>>(){}.getType();
            canvas = gson.fromJson(reader, listType);
            broadcastNewWhiteBoard();
            currentSavePath = fileName;
        } catch (IOException e) {
            throw new RemoteException("Failed to load file: " + e.getMessage());
        }
    }
    //client leave whiteboard, this method will update the userlist, and remove the clients dictionary, all user will receive the message from the system
    public void leaveWhiteBoard(String userName) throws RemoteException {
        clients.remove(userName);
        broadcastUserList();
        sendChat("System", userName + " has left the whiteboard.");
    }
    //send the user list to all users
    private void broadcastUserList() throws RemoteException {
        List<String> userList = new ArrayList<>(clients.keySet());
        for (ClientInterface client : clients.values()) {
            client.updateUserList(userList);
        }
    }
    //for manager to create the whiteboard
    public synchronized void createNewWhiteBoard(String username)throws RemoteException {
        if (!username.equals(managerUsername)) {
            throw new RemoteException("Only the manager can clear the whiteboard.");//to avoid normal user to create the new whiteboard
        }
        canvas.clear();
        broadcastNewWhiteBoard();
        currentSavePath = null;
    }
    //when create the new whiteboard broadcast to all user
    private void broadcastNewWhiteBoard() throws RemoteException {
        for (ClientInterface client : clients.values()) {
            client.receiveCanvas(canvas);
        }
    }

    //avoid the manager forget to use save as when the whiteboard do not save before
    public synchronized void saveOrUpdateCanvas() throws RemoteException {
        if (currentSavePath == null) {
            throw new RemoteException("No existing save path. Please use 'Save As'.");
        }
        saveWhiteboard(currentSavePath);
    }

    public void broadcastWhoDraw(String username, boolean drawing) {
        for (ClientInterface c : clients.values()) {
            try { c.updateWhoDrawing(username, drawing); }
            catch (RemoteException ignored) {}
        }
    }

    public void clearAllCanvas() {
        for (ClientInterface c : clients.values()) {
            try {
                c.clearCanvas();
            } catch (RemoteException ignored) {}
        }
        canvas.clear();
    }
}