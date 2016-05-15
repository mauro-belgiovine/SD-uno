package game;

import java.io.Serializable;
import java.util.*;
import overlay.*;

public class Player extends Node implements Serializable{

    private String name;
    private int id;
    private List<Card> hand;    // player's hand, containing his cards
    
    public Player(String myname, int myid){
        name = myname;
        id = myid;
        hand = new ArrayList<Card>();
    }
    
    public void printPlayer(){
        System.out.println(this.name+" "+this.id+" Cards: "+hand.size());
    }

    public String getName(){
        return name;
    }

    public List<Card> getHand(){
        return hand;
    }

    public void printHand(){
        int i = 0;
        for (Card c:hand) {

            System.out.print(i++ + " ");
            c.printCard();
        }
    }
    
    public void card2Hand(Card c){
        hand.add(c);
    }
    
    public Card throwCard(int i){
        Card c = hand.get(i);
        hand.remove(i);
        return c;
    }
    
    public int getNumCards(){
        return hand.size();
    }
    
    //TODO: usare Card invece che l'indice?
    public boolean playCard(int i, Card last, Color extra){
        // "i" is the index of my chosen card; "last" is the current card on top of the table;
        // "extra" is set in case DRAW4 or WILD is on the table
        
        boolean out = false;

        if(i < hand.size()) {

            Card c = hand.get(i);

            if(c.getColor() == Color.NONE) { // WILD or DRAW4

                out = true; //always playable

            } else if (last.getColor() != Color.NONE) { //if last card has a color

                if (c.sameColor(last) || c.sameNumber(last) || (c.sameAction(last) && (c.getAction() != Action.NONE))) {
                    out = true;
                }

            } else if (last.getColor() == Color.NONE){ //if last has no color

                if (c.isColor(extra)) out = true;

            }
        }
        
        return out;
    }

}