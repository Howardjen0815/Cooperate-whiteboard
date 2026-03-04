/**
 * Used for RMI, the client interface
 * @author  Yu-Han Jen (1508398) <yjen@student.unimelb.edu.au>
 * @version 1.0
 */

package RemoteInterface;

import DrawAction.DrawAction;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {
    boolean approveJoin(String newUser) throws RemoteException;
    void receiveDraw(DrawAction cmd) throws RemoteException;
    void updateWhoDrawing(String username, boolean drawing) throws RemoteException;
    void receiveCanvas(List<DrawAction> canvas) throws RemoteException;

    void receiveMessage(String sender,String msg) throws RemoteException;

    void updateUserList(List<String> usernames) throws RemoteException;

    void forceDisconnect(String reason) throws RemoteException;
    void clearCanvas() throws RemoteException;

}
