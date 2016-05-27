package game;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import static java.lang.Thread.sleep;

import net.*;



public class Match{


    //TODO      ad ogni mossa bisogna salvare nello stato di gioco (r_game) le carte in possesso di ogni giocatore
    //TODO      e poi distribuire lo stato a tutti gli altri nodi, che aggiorneranno il loro stato di gioco
    //          tutti i giocatori usano la classe match per giocare, quindi hanno un r_game salvato in memoria

    static Game g;
    static Player me;
    static GameInstance instance;
    static RemoteGame r_game;

    static Scanner scan;



    public static boolean tryBind(Player p, String host){

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
    
    public static void startGame(Player p, RemoteGame r_game) throws RemoteException{

        while(!r_game.isFinish()) {

            System.out.println("my actual hand is ");
            p.printHand();

            System.out.println("********** Its your turn! *************");
            System.out.println("********** Last card " + r_game.getLastCard().serializeCard() + " *************");

            boolean done = false;
            boolean pass = false;
            int card_i = -1;

            while (!done) {

                //prent player choose the card to play

                p.printHand();

                if (!pass) System.out.println("<p> to PICKUP a card from the Deck");
                else System.out.println("<e> to END your turn");

                if (!scan.hasNextInt()) {


                    if (!pass && scan.next().equals("p")) {
                        Card pu = r_game.remotePop(); //pickup a card
                        System.out.println("pu ");
                        p.card2Hand(pu);
                        pass = true;
                    } else if (pass && scan.next().equals("e")) {
                        done = true;
                    }

                } else {

                    card_i = scan.nextInt();

                    if (p.playCard(card_i, r_game.getLastCard(), r_game.getExtraCol()) && (card_i != -1)) {

                        Card thrown = p.throwCard(card_i);
                        r_game.card2Table(p, thrown); //he put it on the table
                        System.out.print("y ");
                        thrown.printCard();
                        done = true;

                    } else {
                        System.out.println("n ");
                    }
                }

            }

            System.out.println();
            p.printPlayer();
            System.out.println("*******************");

            //if(p.getNumCards() == 0) finish = true;

            //p_turn = nextPlayer(); // choose next player
        }

    }
	
	public static void main(String[] args) {

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

                int my_index = -1;



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

                startGame(me, r_game);

            } catch (Exception e) {
                System.err.println("in-game exception:");
                e.printStackTrace();

            }
        }

        scan.close();
		
	}

}
