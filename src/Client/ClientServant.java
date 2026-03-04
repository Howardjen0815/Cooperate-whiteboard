/**
 * Client-side remote object that receives callbacks from the server
 * @author  Yu-Han Jen (1508398) <yjen@student.unimelb.edu.au>
 * @version 1.0
 */
package Client;

import RemoteInterface.ClientInterface;
import DrawAction.DrawAction;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientServant extends UnicastRemoteObject implements ClientInterface {
    private final WhiteBoardClient client;

    public ClientServant(WhiteBoardClient client) throws RemoteException {
        this.client = client;
    }

    @Override
    public boolean approveJoin(String newUser) throws RemoteException {
        return client.approveJoin(newUser);
    }

    @Override
    public void receiveDraw(DrawAction cmd) throws RemoteException {
        client.applyDrawCommand(cmd);
    }

    @Override
    public void updateWhoDrawing(String username, boolean drawing) throws RemoteException {
        client.updateWhoDrawing(username, drawing);
    }

    @Override
    public void receiveCanvas(List<DrawAction> canvasState) throws RemoteException {
        client.applyFullCanvas(canvasState);
    }

    @Override
    public void receiveMessage(String sender,String msg) throws RemoteException {
        client.receiveMessage(sender, msg);
    }

    @Override
    public void updateUserList(List<String> users) throws RemoteException {
        client.updateUserList(users);
    }

    @Override
    public void forceDisconnect(String message) throws RemoteException {
        client.forceDisconnect(message);
    }

    @Override
    public void clearCanvas() throws RemoteException {
        client.clearCanvas();
    }


}