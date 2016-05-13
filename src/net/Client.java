package net;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import game.*;


public class Client {


    public static void startGame(Player p, RemoteGame r_game) throws RemoteException{


        //dare l'ok per l'avvio della partita

        Card c;

        for (int i = 0; i < 7; i++) {
            //Get initial cards
            c = r_game.remote_pop();

            System.out.println("I received card " + c.serializeCard() + " " + c);
            p.card2Hand(c);

        }

        System.out.println("my actual hand is ");
        p.printHand();

        System.out.println("********** Its your turn! *************");
        System.out.println("********** Last card "+r_game.getLastCard().serializeCard()+ " *************");

        boolean done = false;
        boolean pass = false;
        int card_i = -1;

        while(!done) {

            //prent player choose the card to play

            p.printHand();

            Scanner scan = new Scanner(System.in);

            if(!pass) System.out.println("<p> to PICKUP a card from the Deck");
            else System.out.println("<e> to END your turn");

            if (!scan.hasNextInt()) {



                if (!pass && scan.next().equals("p")) {
                    Card pu = r_game.remote_pop(); //pickup a card
                    System.out.println("pu ");
                    p.card2Hand(pu);
                    pass = true;
                } else if (pass && scan.next().equals("e")) {
                    done = true;
                }

            } else {

                card_i = scan.nextInt();

                if(p.playCard(card_i, r_game.getLastCard(), r_game.getExtraCol()) && (card_i != -1)){

                    Card thrown = p.throwCard(card_i);
                    r_game.card2Table(p, thrown); //he put it on the table
                    System.out.print("y ");
                    thrown.printCard();
                    done = true;

                }else{
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

    public static void main(String args[]) {

        String p_name = "";

        if(args.length > 0) p_name = args[0];
        else {

            System.out.println("Usage: \n\tnet.Client <player_name>");
            return;
        }

        Player p = new Player(p_name, 777);

        try {
            String name = "net.RemoteGame";
            Registry registry = LocateRegistry.getRegistry(50000);
            RemoteGame r_game = (RemoteGame) registry.lookup(name);

            if(r_game.addPlayer(p)) startGame(p, r_game); else System.out.println("Game is FULL!! Sorry.");



        } catch (Exception e) {
            System.err.println("r_pop() exception:");
            e.printStackTrace();
        }
    }

}
