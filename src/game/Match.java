package game;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.List;

import static java.lang.Thread.sleep;

import net.*;

public class Match{

    Game g;
    Player me;
    GameInstance instance;
    RemoteGame r_game;

    Scanner scan;
    int my_index;

    public boolean tryBind(Player p, String host){

        Registry registry;

        String name = "net.Lobby";

        boolean out = false;

        try {

            registry = LocateRegistry.getRegistry(host, 50000);
            r_game = (RemoteGame) registry.lookup(name);
            if(!r_game.addPlayer(p)) {
                System.out.println("Game is FULL!! Sorry.");
            }else{
                out = true;
            }

        } catch (Exception e) {
            System.err.println("tryBind() exception:");
            e.printStackTrace();

        }

        return out;
    }
    
    public void playTurn() throws RemoteException {

            System.out.println("********** Its your turn! *************");
            System.out.println("********** Last card " + g.getLastCard().serializeCard() + " *************");
            System.out.println("********** Extra Color is: "+g.getExtraCol()+" ************");

            boolean done = false;
            boolean pickup = false;
            int card_i = -1;

            while (!done) {

                //current player choose the card to play

                me.printHand();

                if (!pickup) System.out.println("<p> to PICKUP a card from the Deck");
                else System.out.println("<e> to END your turn");

                if (!scan.hasNextInt()) {


                    if (!pickup && scan.next().equals("p")) {
                        Card pu = g.popCard(); //pickup a card
                        System.out.println("pu ");
                        me.card2Hand(pu);
                        pickup = true;

                        //generiamo l'evento PICKUP
                        Map<String, Object> m = new HashMap<String, Object>();
                        m.put("player", my_index);
                        GameEvent e = new GameEvent(Event.PICKUP,m);
                        instance.pushEvent(e); //aggiungi questo evento alla coda

                    } else if (pickup && scan.next().equals("e")) {
                        done = true;
                    }

                } else {

                    card_i = scan.nextInt();

                    if (me.checkPlayable(card_i, g.getLastCard(), g.getExtraCol()) && (card_i != -1)) {

                        Card thrown = me.throwCard(card_i);
                        g.card2Table(me, thrown); //mette la carta sul tavolo
                        System.out.print("y ");
                        thrown.printCard();
                        done = true;

                        //generiamo l'evento THROW
                        Map<String, Object> m = new HashMap<String,Object>();
                        m.put("player", my_index);
                        m.put("card_i", card_i);
                        GameEvent e = new GameEvent(Event.THROW, m);
                        instance.pushEvent(e);

                        if((thrown.getAction() == Action.DRAW4) ||  (thrown.getAction() == Action.WILD)){
                            g.extra_col = g.chooseColor();
                        }

                        //se è cambiato extra_color, generiamo anche questo evento
                        if(g.getExtraCol() != Color.NONE){

                            Map<String, Object> m_extra = new HashMap<String,Object>();
                            m_extra.put("extra", g.getExtraCol());
                            GameEvent e_extra = new GameEvent(Event.EXTRA_COL, m_extra);
                            instance.pushEvent(e_extra);

                        }


                    } else {
                        System.out.println("n ");
                    }
                }

            }

            System.out.println();
            me.printPlayer();
            System.out.println("*******************");

            if(me.getNumCards() == 0){
                g.finish = true;
                //generiamo l'evento FINISH
                GameEvent e = new GameEvent(Event.FINISH, null);
                instance.pushEvent(e);
            }

            //p_turn = nextPlayer(); // choose next player

            g.p_turn = g.nextPlayer();

            //generiamo l'evento TURN
            Map<String, Object> m = new HashMap<String,Object>();
            m.put("next", g.p_turn);
            GameEvent e = new GameEvent(Event.TURN, m);
            instance.pushEvent(e);

    }
	
	public void startClient(String[] args) {

		String p_name = "";
        String host = "";

        if(args.length > 1){
            p_name = args[0];
            host = args[1];
        }
        else if(args.length > 0){
        	p_name = args[0];
        }else{

            System.out.println("Usage: \n\tnet.Client <player_name> <host name>(?)");
            return;
        }

        scan = new Scanner(System.in);

        me = new Player(p_name, 777); //crea nuovo giocatore


        if(tryBind(me,host)) {

            try{

                instance = new GameInstance(); //create local instance of the game state

                my_index = -1;

                while(!me.isPlaying()){
                    System.out.println("<s> to vote for match start");

                    if(scan.next().equals("s")){
                        me.startPlaying();
                        my_index = r_game.voteStart(me);          //ok per l'avvio della partita
                    }
                }

                if(my_index < 0) {
                    System.out.println("Game error.");
                    return;
                }

                int my_port = 50000+1+my_index;

                String name = "player."+me.getUuid();

                RemoteGame stub = (RemoteGame) UnicastRemoteObject.exportObject(instance, my_port);
                // Bind the remote object's stub in the registry
                Registry reg = LocateRegistry.createRegistry(my_port);
                reg.bind(name, stub);

                while(!r_game.checkAllPlaying()){
                    System.out.println("Waiting for other players...");
                    System.out.println("There are "+r_game.getNPlayer()+" connected to this game");
                    sleep(1000);
                }

                g = r_game.getState();

                me.setHand(g.getPHand(me)); //prendo le mie carte dallo stato del gioco

                System.out.println("my actual hand is ");
                me.printHand();

                do{

                    if(g.p_turn == my_index){   //se tocca a me,
                        playTurn();             //gioco il mio turno
                        sendUpdates();
                    }

                    System.out.println("WAITING FOR THE TURNING POINT...");
                    sleep(1000);

                    //prelevo gli eventi
                    GameEvent e;
                    do{
                        e = instance.popEvent();
                        if(e != null){
                            System.out.println("ho ricevuto un evento: "+e.toString());
                            execEvent(e);
                        }
                    }while(e != null);

                } while(!g.isFinish());

            } catch (Exception e) {
                System.err.println("in-game exception:");
                e.printStackTrace();

            }
        }

        scan.close();
		
	}

    private void execEvent(GameEvent e) {

        int p_index;
        Player p;

        switch(e.event){

            case PICKUP:
                //il giocatore indicato deve pescare una carta dal mazzo
                p_index = (Integer) e.params.get("player");
                p = g.players.get(p_index);
                Card pu = g.popCard(); //pesca una carta
                p.card2Hand(pu);
                break;

            case THROW:
                //il giocatore indicato deve mettere sul tavolo la carta
                p_index = (Integer) e.params.get("player");
                p = g.players.get(p_index);
                Card thrown = p.throwCard( (Integer) e.params.get("card_i") );
                g.card2Table(p, thrown); //mette la carta sul tavolo
                break;

            case EXTRA_COL:
                //si cambia l'extra_col (DRAW4 o WILD)
                g.extra_col = (Color) e.params.get("extra");
                break;

            case TURN:
                //setta il prossimo giocatore
                g.p_turn = (Integer) e.params.get("next");
                break;

            case FINISH:
                //la partita è finita è ha vinto il giocatore corrente (g.p_turn)
                g.finish = true;
                System.out.println("PARTITA FINITA! Vincitore: "+g.players.get(g.p_turn).getName());
        }

    }


    private void sendUpdates() throws RemoteException {

        instance.setState(g); //settiamo lo stato attuale nell'interfaccia remota

        for(int i = 0; i < g.getNPlayer(); i++){

            if(i != my_index){ //mandiamo a tutti, tranne a me stesso

                String name = "player." + g.players.get(i).getUuid();
                try {

                    Registry registry = LocateRegistry.getRegistry(g.players.get(i).getIp(), 50000+1+i);
                    r_game = (RemoteGame) registry.lookup(name);

                    Queue<GameEvent> queue = instance.getUpdates();
                    r_game.sendUpdates(queue); //invia la lista degli eventi ad ogni client

                } catch (Exception e) {
                    System.err.println("tryBind() exception:");
                    e.printStackTrace();
                }


            }
        }

        instance.clearUpdates(); //rimuoviamo dalla nostra coda gli eventi inviati

    }

    public int getPturn(){ return g.getPturn();}
    public Deck getDeck(){ return g.getDeck();}
    public int playerNum() { return g.playerNum();}
    public Color getExtraColor(){return g.getExtraColor(); }
    public void setExtraColor(Color c) { g.setExtraColor(c);}
    public void nextRound(){g.nextRound();}
    public List<Player> getPlayers() {return g.getPlayers();}
    public void execEffect(Action a){ g.execEffect(a);}
    public Player getMe(){return me;}


}
