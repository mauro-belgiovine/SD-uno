package game;

import java.io.Serializable;
import java.util.*;

public class Deck implements Serializable{
    
    public int n_cards = 108;
    
    public Card[] cards;
    
    public List<Card> stack; // stack is a list of int, which correspond to the indexes of cards[]
    public int n_current = n_cards;
    
    public List<Card> table; //pile of cards thrown by players
    public Card last_c;

    
    public Deck(){
        
        cards = new Card[n_cards];
        stack = new ArrayList<Card>();
        table = new ArrayList<Card>();
        last_c = null;
    }
    
    public void shuffle(){
        Collections.shuffle(this.stack, new Random());
    }
    
    public Card pop() {

        if (n_current == 0){

            int table_size = table.size();
            for(int i = 0; i < (table_size-1); i++){
                Card cc = table.get(i);
                stack.add(cc);
            }
            table.clear();
            table.add(last_c);

            n_current = stack.size();
            shuffle();

        }

        n_current--;
        Card c = stack.get(n_current);
        stack.remove(n_current);

        return c;
    }
    
    public void card2Table(Card c){
        table.add(c);
        last_c = c;
    }
    
    public void Init_Deck(){
        
        //init all cards
        
        int count = 0;
        
        cards[count] = new Card(Color.YELLOW, 0, Action.NONE); // 0 card
        for(int i = 1; i <= 9; i++){
            cards[count+i] = new Card(Color.YELLOW, i, Action.NONE);   //two cards per number
            cards[count+i+9] = new Card(Color.YELLOW, i, Action.NONE);
        }
        count += 19;
        
        cards[count] = new Card(Color.BLUE, 0, Action.NONE); // 0 card
        for(int i = 1; i <= 9; i++){
            cards[count+i] = new Card(Color.BLUE, i, Action.NONE);   //two cards per number
            cards[count+i+9] = new Card(Color.BLUE, i, Action.NONE);
        }
        count += 19;
        
        cards[count] = new Card(Color.RED, 0, Action.NONE); // 0 card
        for(int i = 1; i <= 9; i++){
            cards[count+i] = new Card(Color.RED, i, Action.NONE);   //two cards per number
            cards[count+i+9] = new Card(Color.RED, i, Action.NONE);
        }
        count += 19;
        
        cards[count] = new Card(Color.GREEN, 0, Action.NONE); // 0 card
        for(int i = 1; i <= 9; i++){
            cards[count+i] = new Card(Color.GREEN, i, Action.NONE);   //two cards per number
            cards[count+i+9] = new Card(Color.GREEN, i, Action.NONE);
        }
        count += 19;
        
        cards[count++] = new Card(Color.YELLOW, -1, Action.DRAW2);
        cards[count++] = new Card(Color.YELLOW, -1, Action.DRAW2);
        cards[count++] = new Card(Color.BLUE, -1, Action.DRAW2);
        cards[count++] = new Card(Color.BLUE, -1, Action.DRAW2);
        cards[count++] = new Card(Color.RED, -1, Action.DRAW2);
        cards[count++] = new Card(Color.RED, -1, Action.DRAW2);
        cards[count++] = new Card(Color.GREEN, -1, Action.DRAW2);
        cards[count++] = new Card(Color.GREEN, -1, Action.DRAW2);
        cards[count++] = new Card(Color.YELLOW, -1, Action.SKIP);
        cards[count++] = new Card(Color.YELLOW, -1, Action.SKIP);
        cards[count++] = new Card(Color.BLUE, -1, Action.SKIP);
        cards[count++] = new Card(Color.BLUE, -1, Action.SKIP);
        cards[count++] = new Card(Color.RED, -1, Action.SKIP);
        cards[count++] = new Card(Color.RED, -1, Action.SKIP);
        cards[count++] = new Card(Color.GREEN, -1, Action.SKIP);
        cards[count++] = new Card(Color.GREEN, -1, Action.SKIP);
        cards[count++] = new Card(Color.YELLOW, -1, Action.REVERSE);
        cards[count++] = new Card(Color.YELLOW, -1, Action.REVERSE);
        cards[count++] = new Card(Color.BLUE, -1, Action.REVERSE);
        cards[count++] = new Card(Color.BLUE, -1, Action.REVERSE);
        cards[count++] = new Card(Color.RED, -1, Action.REVERSE);
        cards[count++] = new Card(Color.RED, -1, Action.REVERSE);
        cards[count++] = new Card(Color.GREEN, -1, Action.REVERSE);
        cards[count++] = new Card(Color.GREEN, -1, Action.REVERSE);
        
        
        for(int i = 0; i < 4; i++) cards[count+i] = new Card(Color.NONE, -1, Action.DRAW4);
        count += 4;
        
        for(int i = 0; i < 4; i++) cards[count+i] = new Card(Color.NONE, -1, Action.WILD);
        count += 4;
        
        //init the deck stack
        
        for(int i = 0; i < this.n_cards; i++){
            stack.add(cards[i]);
        }
    }
    

}