package net;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import game.*;

public interface RemoteGame extends Remote {

    Card remote_pop() throws RemoteException;
    boolean addPlayer(Player p) throws RemoteException;
    Card getLastCard() throws RemoteException;
    Color getExtraCol() throws RemoteException;
    void card2Table(Player p, Card c) throws RemoteException;


    public List<Card> getHand(Player p) throws RemoteException;



}
