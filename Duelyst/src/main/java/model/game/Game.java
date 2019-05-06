package model.game;

import controller.Constants;
import controller.GameException;
import model.*;
import model.buffs.Buff;

import java.util.ArrayList;
import java.util.Optional;

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
        selectedCell=
                Optional.ofNullable(getCellByCardId(cardId))
                .filter(cell -> cell.getCard().getAccount().equals(currentPlayer.getAccount()))
                .orElseThrow(() -> new GameException("This card is not yours!"));
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
    public void useSpecialPower(int x, int y){
        Hero soldier = (Hero) selectedCell.getCard();
        //....
    }
    public ArrayList<Cell> getTargetCellsByRange(Buff buff, Cell pointedCell){
        ArrayList<Cell> cells = new ArrayList<>();
        if(buff.getRange().equals(RangeType.ALL_BOARD)){
            cells = getTargetCellsAllBoard();
        }
        else if (buff.getRange().equals(RangeType.AROUND8)){
            cells = getTargetCellsAROUND8(pointedCell);
        }
        //other Types should be added ....
        return cells;
    }
    private ArrayList<Cell> getTargetCellsAllBoard(){
        ArrayList<Cell> cells = new ArrayList<>();
        for(int i = 0; i < board.size(); i++){
            for(int j = 0; j<board.get(i).size(); j++){
                cells.add(board.get(i).get(j));
            }
        }
        return cells;
    }
    private ArrayList<Cell> getTargetCellsAROUND8(Cell pointedCell){
        ArrayList<Cell> cells = new ArrayList<>();
        int leftX = pointedCell.getX() - 1;
        int upY = pointedCell.getY() - 1;
        int rightX = pointedCell.getX() + 1;
        int downY = pointedCell.getY() + 1;
        leftX = leftX < 0 ? 0 : leftX;
        upY = upY < 0 ? 0 : upY;
        rightX = rightX >= Constants.LENGTH_OF_BOARD ? Constants.LENGTH_OF_BOARD-1 : rightX;
        downY = downY >= Constants.LENGTH_OF_BOARD ? Constants.WIDTH_OF_BOARD-1 : downY;
        for (int i = upY; i <= downY ; i++){
            for(int j = leftX; j <= rightX; j++){
                if( i == pointedCell.getY() && j == pointedCell.getX()){
                    continue;
                }
                cells.add(board.get(i).get(j));
            }
        }
        return cells;
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
