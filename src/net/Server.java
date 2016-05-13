package net;

import java.util.List;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import game.*;

public class Server implements RemoteGame{

    Game my_game;


    public Server(){
        my_game = new Game();
    }

    public Card remote_pop() throws RemoteException{

        Card c = my_game.pop_card();
        System.out.println("card popped "+c.serializeCard());
        return c;

    }

    public Card getLastCard() throws RemoteException{

        return my_game.getLastCard();

    }

    public void card2Table(Player p, Card c) throws RemoteException{
        System.out.println(p.getName()+" played "+c.serializeCard());
        my_game.playCard(p,c);
    }

    public Color getExtraCol() throws RemoteException{
        return my_game.getExtra_col();
    }


    public boolean addPlayer(Player p) throws RemoteException{

        boolean output = my_game.addPlayer(p);
        if(output) System.out.println(p.getName()+"(player "+my_game.getN_player()+") has been added to this game");

        return output;
    }

    public List<Card> getHand(Player p) throws RemoteException{
        return my_game.getHand(p);
    }

    public static void main(String[] args) {

        try {
            String name = "net.RemoteGame";
            Server r_deck = new Server();
            RemoteGame stub = (RemoteGame) UnicastRemoteObject.exportObject(r_deck, 50000);

            // Bind the remote object's stub in the registry
            //Registry registry = LocateRegistry.getRegistry();
            //registry.rebind(name, stub);

            Registry reg = LocateRegistry.createRegistry(50000);
            reg.bind(name, stub);

            System.out.println("net.RemoteGame bound");

        } catch (Exception e) {
            System.err.println("net.RemoteGame exception:");
            e.printStackTrace();
        }
    }

}
