    /**
     * Acts as the intermediary that relays requests between the white-board server, the manager, and connected clients.
     * @author  Yu-Han Jen (1508398) <yjen@student.unimelb.edu.au>
     * @version 1.0
     */
package Server;

import Chat.Message;
import RemoteInterface.ClientInterface;
import RemoteInterface.ServerInterface;
import DrawAction.DrawAction;
import Exception.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class WhiteboardServerServant extends UnicastRemoteObject implements ServerInterface {
    private final WhiteboardServer wbServer;

    public WhiteboardServerServant(String managerUsername, ClientInterface manager) throws RemoteException {
        wbServer = new WhiteboardServer(managerUsername, manager);
    }

    @Override
    public List<Message> getChatHistory() throws RemoteException {
        return wbServer.getChatHistory();
    }

    @Override
    public void requestJoinWB(String clientName, ClientInterface client) throws RemoteException, JoinRejectException, UserNameExistException {
        wbServer.requestJoinWB(clientName, client);
    }

    @Override
    public void registerClient(String userName, ClientInterface client) throws RemoteException {
        wbServer.registerClient(userName, client);
    }

    @Override
    public void broadcastWhoDraw(String username, boolean drawing) throws RemoteException {
        wbServer.broadcastWhoDraw(username, drawing);
    }


    @Override
    public void sendAllUser(DrawAction cmd) throws RemoteException {
        wbServer.sendAllUser(cmd);
    }

    @Override
    public void leaveWhiteBoard(String username) throws RemoteException {
        wbServer.leaveWhiteBoard(username);
    }


    @Override
    public List<String> getAllUsers() throws RemoteException {
        return wbServer.getAllUsers();
    }

    @Override
    public List<DrawAction> getCanvas() throws RemoteException {
        return wbServer.getCanvas();
    }

    @Override
    public void kickUser(String userName) throws RemoteException {
        wbServer.kickUser(userName);
    }

    @Override
    public void createNewWhiteBoard(String username) throws RemoteException {
        wbServer.createNewWhiteBoard(username);
    }

    @Override
    public void saveNewWhiteboard(String filePath) throws RemoteException {
        wbServer.saveNewWhiteboard(filePath);
    }

    @Override
    public void loadWhiteboard(String fileName) throws RemoteException {
        wbServer.loadWhiteboard(fileName);
    }

    @Override
    public void sendChat(String sender, String msg) throws RemoteException {
        wbServer.sendChat(sender, msg);
    }

    @Override
    public void shutdownBoard(String managerName) throws RemoteException {
        wbServer.shutdownBoard(managerName);
    }

    @Override
    public void saveOrUpdateCanvas() throws RemoteException {
        wbServer.saveOrUpdateCanvas();
    }

}
