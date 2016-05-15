package game;

import java.rmi.RemoteException;
import java.util.*; //fanculo gioco truccato, vince chi l'ha programmato
import net.RemoteGame;

public class Game implements RemoteGame{
    
    int p_turn;

    int max_n_player;
    
    Color extra_col;
    
    Deck deck;

    List<Player> players;
    
    boolean reverse = false;

    public Game(){

        max_n_player = 3;
        p_turn = 0;

        deck = new Deck();
        deck.Init_Deck();
        deck.shuffle(); //deck shuffling

        extra_col = Color.NONE;

        players = new ArrayList<Player>();

        //put a card on the table
        deck.card2Table(deck.pop());


    }

    public Card pop_card(){
        return deck.pop();
    }

    public Card getLastCard() throws RemoteException{
        return deck.last_c;
    }

    public Color getExtra_col(){
        return extra_col;
    }

    public List<Card> getHand(Player p) throws RemoteException{

        boolean no_cards = true;

        int p_index = players.indexOf(p); //index of the requesting player

        for(int n = 0; n < players.size(); n++){ //check if each player has 0 cards

            if(players.get(n).getNumCards() != 0){
                no_cards = false;
            }
        }

        if(no_cards) { //if no player has cards, 7 cards (one by one) are drawn from the deck

            for (int i = 0; i < 7; i++) {
                for (int y = 0; y < players.size(); y++) {
                    players.get(y).card2Hand(deck.pop());
                }

            }

        }


        return players.get(p_index).getHand(); // player takes his hand

    }

    public boolean addPlayer(Player p) throws RemoteException{

        boolean output = false;

        if((players.size() + 1) <= max_n_player){

            players.add(p);
            output = true;

        }
        
        if(output){
        	for(int i = 0; i < players.size(); i++){
            	System.out.println("Player IP"+ players.get(i).getIp()+" UUID "+players.get(i).getUuid());
        	}
        }

        return output;
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

    public int getN_player(){
        return players.size();
    }
    
    
    //REMOTE METHODS
    public Card remote_pop() throws RemoteException{

        Card c = pop_card();
        System.out.println("card popped "+c.serializeCard());
        return c;

    }

    public void card2Table(Player p, Card c) throws RemoteException{
        System.out.println(p.getName()+" played "+c.serializeCard());
        playCard(p,c);
    }

    public Color getExtraCol() throws RemoteException{
        return getExtra_col();
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