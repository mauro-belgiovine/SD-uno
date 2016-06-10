package ui;

import game.Match;

/**
 * Created by mauro on 10/06/16.
 */
public class UI {

    static Match my_match;

    public static void main(String[] args){
        my_match = new Match();
        my_match.startClient(args);
    }

}
