package game;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game implements Serializable{
    
    int p_turn;

    int max_n_player;
    
    Color extra_col;
    
    Deck deck;

    List<Player> players;

    boolean finish ;
    
    boolean reverse;

    public Game(){

        max_n_player = 10;
        p_turn = 0;

        deck = new Deck();
        deck.Init_Deck();
        deck.shuffle(); //deck shuffling

        extra_col = Color.NONE;

        players = new ArrayList<Player>();

        finish = false;
        reverse = false;

    }

    public Card popCard(){
        return deck.pop();
    }

    public Color getExtraCol(){
        return extra_col;
    }
    
    public int nextPlayer(){
        
        int p_next;
        
        if(!reverse) p_next = (p_turn + 1)% players.size();
        else{
            if(p_turn == 0) p_next = players.size()-1;
            else p_next = p_turn-1;
        }
        
        return p_next;
    }

     public Color chooseColor(){

        Color chosen = Color.NONE;

        System.out.println("Choose a color: <g> GREEN, <y> YELLOW, <b> BLUE, <r> RED");

        while(chosen == Color.NONE) {
            Scanner scan = new Scanner(System.in);
            String s = scan.next();
            System.out.println("INPUT: "+s);
            if (s.equals("g")) chosen = Color.GREEN;
            else if (s.equals("y")) chosen = Color.YELLOW;
            else if (s.equals("b")) chosen = Color.BLUE;
            else if (s.equals("r")) chosen = Color.RED;
        }

        return chosen;

    }

    public void playCard(Player p, Card c){

        deck.card2Table(c); //he put it on the table
        execEffect(deck.last_c.getAction());

    }
    
     public void execEffect(Action a){ //apply effect
        
        Player p = players.get(p_turn);
        int p_next = nextPlayer();
        Player next = players.get(p_next);
        
        if (extra_col != Color.NONE) extra_col = Color.NONE; //reset extra color (from DRAW4 or WILD) if a new card has been played
        
        switch(a){
                
            case DRAW2://pesca 2 carte
                
                for(int i = 0; i < 2; i++) next.card2Hand(deck.pop());
                p_turn = nextPlayer();
                break;
                
            case DRAW4:    //pesca 4 carte + cambia colore
                for(int i = 0; i < 4; i++) next.card2Hand(deck.pop());
                p_turn = nextPlayer();
                extra_col = chooseColor();
                break;
                
            case REVERSE:     //inverti giro di mano
                reverse = !reverse;
                break;
                
            case SKIP:       //il successivo salta un turno
                p_turn = nextPlayer();
                break;
                
            case WILD: //cambia colore
                extra_col = chooseColor();
                break;
                
            default:
                //do nothing
        }
        
    }

    public boolean addPlayer(Player p) {

        boolean output = false;

        if((players.size() + 1) <= max_n_player){

            players.add(p);
            output = true;

        }

        if(output){
            System.out.println("Actual players are:");
            for(int i = 0; i < players.size(); i++){
                System.out.println("\tPlayer IP "+ players.get(i).getIp()+" UUID "+players.get(i).getUuid());
            }
        }

        return output;
    }

    public int getNPlayer() {
        return players.size();
    }

    public int getMaxNPlayer(){
        return max_n_player;
    }

    public int voteStart(Player p) {
        int i = players.indexOf(p);
        players.get(i).startPlaying();

        return i; //return the player index in the game state
    }

    public boolean checkAllPlaying() {

        boolean out = false;
        int num_p = players.size();
        int started = 0;

        for(int i = 0; i < num_p; i++){
            if(players.get(i).isPlaying()) started++;
        }

        if((started == num_p) && (num_p > 1)) out = true;

        return out;

    }

    public boolean isFinish() { return finish; }

    public Card getLastCard() {
        return deck.last_c;
    }

    public void card2Table(Player p, Card c) {
        System.out.println(p.getName()+" played "+c.serializeCard());
        playCard(p,c);
    }

    public void setupGame(){

        //each player picks 7 card from the deck, one by one
        for(int i = 0; i < 7; i++){

            for(int y = 0; y < players.size(); y++){
                players.get(y).card2Hand(deck.pop());
            }
        }

        //put a card on the table
        deck.card2Table(deck.pop());
    }

    public List<Card> getPHand(Player p){

        int i = players.indexOf(p);
        return players.get(i).getHand();
    }

    /*
    public static void main(String[] args){
        
        deck.Init_Deck();
        
        Player p0 = new Player("Mauro", 0);
        Player p1 = new Player("Graziana", 1);
        //Player p2 = new Player("Luix", 2);
        //Player p3 = new Player("Geronimo", 3);
        //Player p4 = new Player("Topo Gigio", 4);
        //Player p5 = new Player("Jerry Scotti", 5);
        
        players.add(p0); players.add(p1);
        //players.add(p2); players.add(p3);
        //players.add(p4); players.add(p5);
        
        deck.shuffle(); //deck shuffling

        
        //each player picks 7 card from the deck, one by one
        
        for(int i = 0; i < 7; i++){
            
            for(int y = 0; y < n_player; y++){
                players.get(y).card2Hand(deck.pop());
            }
            
        }
        
        //put another card on the table
        deck.card2Table(deck.pop());

        
        boolean finish = false;
        
        while(!finish){

            System.out.print("---- "+p_turn+" ---- ");
            System.out.print("(extra : "+extra_col+ ") Current card: ");
            deck.last_c.printCard();
            System.out.println(" Cards remaining "+deck.n_current);



            Player cur = players.get(p_turn);
            
            cur.printPlayer();
        
            boolean done = false;
            boolean pass = false;
            int card_i = -1;
        
            while(!done) {

                //current player choose the card to play

                cur.printHand();

                Scanner scan = new Scanner(System.in);

                if(!pass) System.out.println("<p> to PICKUP a card from the Deck");
                else System.out.println("<e> to END your turn");

                if (!scan.hasNextInt()) {



                    if (!pass && scan.next().equals("p")) {
                        Card pu = deck.pop(); //pickup a card
                        System.out.println("pu ");
                        cur.card2Hand(pu);
                        pass = true;
                    } else if (pass && scan.next().equals("e")) {
                        done = true;
                    }

                } else {

                    card_i = scan.nextInt();

                    if(cur.playCard(card_i, deck.last_c, extra_col) && (card_i != -1)){

                        Card thrown = cur.throwCard(card_i);
                        deck.card2Table(thrown); //he put it on the table
                        System.out.print("y ");
                        thrown.printCard();
                        execEffect(deck.last_c.getAction());
                        done = true;

                    }else{
                        System.out.println("n ");
                    }
                }

            }
            
            System.out.println();
            cur.printPlayer();
            System.out.println("*******************");
            
            if(cur.getNumCards() == 0) finish = true;
            
            p_turn = nextPlayer(); // choose next player
            
        }
        
        System.out.println("Cards remaining: "+deck.n_current);
        
    }*/
}