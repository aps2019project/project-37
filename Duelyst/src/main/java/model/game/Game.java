package model.game;

import java.util.ArrayList;

public class Game {
    private Player player1;
    private Player player2;
    private Player turn;
    private ArrayList<ArrayList<Cell>> board = new ArrayList<>();
    Game(Player player1,Player player2){
        this.player1 = player1;
        this.player2 = player2;
    }
}
