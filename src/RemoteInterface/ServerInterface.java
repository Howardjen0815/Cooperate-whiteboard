/**
 * Used for RMI, the server interface
 * @author  Yu-Han Jen (1508398) <yjen@student.unimelb.edu.au>
 * @version 1.0
 */
package RemoteInterface;

import Chat.Message;
import DrawAction.DrawAction;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import Exception.*;
public interface ServerInterface extends Remote {
    List<Message> getChatHistory() throws RemoteException;
    void requestJoinWB(String clientName, ClientInterface client) throws RemoteException, UserNameExistException, JoinRejectException;
    void registerClient(String userName, ClientInterface client) throws RemoteException;
    void broadcastWhoDraw(String username, boolean drawing) throws RemoteException;
    //send the drawing action to all users
    void sendAllUser(DrawAction cmd) throws RemoteException;

    void leaveWhiteBoard(String username) throws RemoteException;

    // show all the user
    List<String> getAllUsers() throws RemoteException;

    //get the current canvas
    List<DrawAction> getCanvas() throws RemoteException;

    // manager kick user
    void kickUser(String userName) throws RemoteException;
    void createNewWhiteBoard(String username) throws RemoteException;


    void saveNewWhiteboard(String filePath) throws RemoteException;

    // manager load canvas
    void loadWhiteboard(String fileName) throws RemoteException;

    void sendChat(String sender, String msg) throws RemoteException;
    void shutdownBoard(String managerName) throws RemoteException;

    void saveOrUpdateCanvas() throws RemoteException;;
}
