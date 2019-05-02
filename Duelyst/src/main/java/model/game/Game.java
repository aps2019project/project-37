package model.game;

import controller.GameException;
import model.AttackType;
import model.Card;
import model.Hero;

import java.util.ArrayList;

public class Game {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Cell selectedCell;
    private ArrayList<ArrayList<Cell>> board = new ArrayList<>();
    public Game(Player player1,Player player2){
        this.player1 = player1;
        this.player2 = player2;
    }

    public Player getPlayer1() {
        return player1;
    }
    public Player getPlayer2() {
        return player2;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    public ArrayList<ArrayList<Cell>> getBoard() {
        return board;
    }
    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }
    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public void setBoard(ArrayList<ArrayList<Cell>> board) {
        this.board = board;
    }
    public void setSelectedCellByCardId(String cardId) {
        selectedCell = getCellByCardId(cardId);
    }
    public void moveSelectedCardTo(int x, int y){
        Cell newCell = board.get(x).get(y);
        if(getDistance(selectedCell,newCell) <= 2){
            if(!newCell.hasCard()){
                newCell.setCard(selectedCell.getCard());
                selectedCell.removeCard();
                selectedCell = newCell;
            }else {
                throw new GameException("The destination has a card!");
            }
        }else {
            throw new GameException("The distance is more than 2!");
        }
    }
    public void attack(String cardId){
        Cell opponentCell = getCellByCardId(cardId);
        Hero self = (Hero) selectedCell.getCard();
        Hero opponent = (Hero) opponentCell.getCard();
        if(self.getInGame().isArmed()) {
            if (checkRangeOfAttack(opponentCell)) {
                opponent.getInGame().decreaseHealthPoint(self.getAttackPower());
                if(checkRangeOfCounterAttack(opponentCell)){
                    self.getInGame().decreaseHealthPoint(opponent.getAttackPower());
                }
            }else{
                throw new GameException("opponent is unavailable for attack");
            }
        }else{
            throw new GameException("You cannot move!");
        }
    }
    public void nextTurn(){
        if(currentPlayer.equals(player1)) {
            setCurrentPlayer(player2);
        }else {
            setCurrentPlayer(player1);
        }
        if(getCurrentPlayer().isAI()){
            playAI();
        }
    }
    public void playAI(){
        //Run some random possible moves for the current player;
        nextTurn();
    }
    public boolean checkRangeOfAttack(Cell opponentCell){
        Hero self = (Hero) selectedCell.getCard();
        int distance = getDistance(selectedCell, opponentCell);
        if(self.getAttackType().equals(AttackType.MELEE)){
            if(distance==1){
                return true;
            }
        }
        if(self.getAttackType().equals(AttackType.RANGED)){
            if(distance <= self.getRange() && distance > 1){
                return true;
            }
        }
        if(self.getAttackType().equals(AttackType.HYBRID)){
            if(distance <= self.getRange()){
                return true;
            }
        }
        return false;
    }
    public boolean checkRangeOfCounterAttack(Cell opponentCell){
        Hero opponent = (Hero) opponentCell.getCard();
        int distance = getDistance(selectedCell, opponentCell);
        if(opponent.getAttackType().equals(AttackType.MELEE)){
            if(distance==1){
                return true;
            }
        }
        if(opponent.getAttackType().equals(AttackType.RANGED)){
            if(distance <= opponent.getRange() && distance > 1){
                return true;
            }
        }
        if(opponent.getAttackType().equals(AttackType.HYBRID)){
            if(distance <= opponent.getRange()){
                return true;
            }
        }
        return false;
    }
    public Cell getCellByCardId(String id){
        for(ArrayList<Cell> row:board){
            for(Cell cell:row){
                if(cell.getCard().idEquals(id)){
                    return cell;
                }
            }
        }
        throw new GameException("Card with this id is not on the board!");
    }
    public int getDistance(Cell firstCell, Cell secondCell){
        return Math.abs(firstCell.getX()-secondCell.getX())+
                Math.abs(firstCell.getY()-secondCell.getY());
    }
}
