package net;

import game.Card;
import game.Color;
import game.Game;
import game.Player;

import java.rmi.RemoteException;

/**
 * Created by mauro on 26/05/16.
 */
public class GameInstance implements RemoteGame {

    Game g;

    boolean started = false;

    public GameInstance(){
        g = new Game();
    }

    //REMOTE METHODS

    public boolean addPlayer(Player p) throws RemoteException {
        return g.addPlayer(p);
    }

    public int getNPlayer() throws RemoteException{
        return g.getNPlayer();
    }

    public int voteStart(Player p) throws RemoteException{
        return g.voteStart(p);
    }

    public boolean checkAllPlaying() throws RemoteException{

        boolean out = false;

        if(g.checkAllPlaying()){ //if all players want to play, give 'em their first hand

            if(!started){
                g.setupGame();
                started = true;
            }
            out = true;
        }

        return out;

    }

    public boolean isFinish() throws RemoteException{ return g.isFinish(); }

    public Card getLastCard() throws RemoteException{
        return g.getLastCard();
    }

    public Card remotePop() throws RemoteException{

        return g.popCard();

    }

    public void card2Table(Player p, Card c) throws RemoteException{
        g.card2Table(p,c);
    }

    public Color getExtraCol() throws RemoteException{
        return g.getExtraCol();
    }

    public Game getState() throws RemoteException{
        return g;
    }

}
