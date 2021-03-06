package net;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Queue;

import game.*;

public interface RemoteGame extends Remote {

    int voteStart(Player p) throws RemoteException;
    int getNPlayer() throws RemoteException;
    boolean checkAllPlaying() throws RemoteException;
    Card remotePop() throws RemoteException;
    boolean addPlayer(Player p) throws RemoteException;
    Card getLastCard() throws RemoteException;
    Color getExtraCol() throws RemoteException;
    void card2Table(Player p, Card c) throws RemoteException;
    boolean isFinish() throws RemoteException;

    Game getState() throws RemoteException;
    void setState(Game state) throws RemoteException;

    void pushEvent(GameEvent e) throws RemoteException;
    void sendUpdates(Queue<GameEvent> q) throws RemoteException;


}
