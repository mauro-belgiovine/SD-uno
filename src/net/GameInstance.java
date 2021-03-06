package net;

import game.Card;
import game.Color;
import game.Game;
import game.Player;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by mauro on 26/05/16.
 */
public class GameInstance implements RemoteGame {

    Game g;
    Queue<GameEvent> update_queue;

    boolean started;

    public GameInstance(){
        update_queue = new LinkedList<GameEvent>();
        started = false;
    }

    //REMOTE METHODS

    public void initGame(){
        g = new Game();
    }

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

    public Card remotePop() throws RemoteException{ //TODO - non serve?
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

    public void setState(Game state) throws RemoteException{
        g = state;
    }

    public void pushEvent(GameEvent e) throws RemoteException{
        update_queue.add(e); //save locally the event received
    }

    public GameEvent popEvent(){
        return update_queue.poll();
    }

    public Queue<GameEvent> getUpdates() {
        return update_queue;
    }

    public void clearUpdates(){
        update_queue.clear();
    }

    public void sendUpdates(Queue<GameEvent> q) throws RemoteException{
        update_queue = q;
    }

}
