package model.game;

import controller.Constants;
import controller.GameException;
import controller.menu.BattleMenu;
import model.Account;
import model.Deck;
import model.buffs.Buff;
import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.*;
import model.items.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Game {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Cell selectedCell;
    private List<List<Cell>> board = new ArrayList<>();
    private BattleMenu.CustomGameMode mode = BattleMenu.CustomGameMode.UNKNOWN;
    private int flagNumber;


    public static Game createGame(Account account1, Account account2, BattleMenu.CustomGameMode mode, Deck deck,
                                  int flagNumber) {
        Player player1 = new Player(account1.getUserName(), account1.getCloneDeck(), false);
        Player player2;
        if (account2 == null) {
            player2 = new Player("AI", deck, true);
        } else {
            player2 = new Player(account2.getUserName(), account2.getMainDeck(), false);
        }
        return new Game(player1, player2, flagNumber).setMode(mode);
    }

    public Game(Player player1, Player player2, int flagNumber) {
        this.player1 = player1;
        this.player2 = player2;
        this.flagNumber = flagNumber;
        initGameBoard();

    }

    private void initGameBoard() {
        for (int i = 0; i < 5; i++) {
            board.add(new ArrayList<>());
            for (int j = 0; j < 9; j++) {
                board.get(i).add(new Cell(i, j));
            }
        }
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

    public List<List<Cell>> getBoard() {
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

    public void setBoard(List<List<Cell>> board) {
        this.board = board;
    }

    public Game setMode(BattleMenu.CustomGameMode mode) {
        this.mode = mode;
        return this;
    }

    public BattleMenu.CustomGameMode getMode() {
        return mode;
    }

    public String getInfo() {
        switch (mode) {

            case KILL_ENEMY_HERO:
                return player1.getAccountName() + "'hero HP: " +"";
            case KEEP_FLAG_6_ROUNDS:
                break;
            case COLLECT_HALF_FLAGS:
                break;
            case UNKNOWN:
                break;
        }
        return "";
    }


    public int getDistance(Cell firstCell, Cell secondCell) {
        return Math.abs(firstCell.getX() - secondCell.getX()) +
                Math.abs(firstCell.getY() - secondCell.getY());
    }

 /*   public void setSelectedCellByCardId(String cardId) {
        selectedCell =
                Optional.ofNullable(getCellByCardId(cardId))
                        .filter(cell -> cell.getCard().getAccountName().equals(currentPlayer.getAccountName()))
                        .orElseThrow(() -> new GameException("This card is not yours!"));
    }

    public void moveSelectedCardTo(int x, int y) {
        Cell newCell = board.get(x).get(y);
        if (getDistance(selectedCell, newCell) <= 2) {
            if (!newCell.hasCard()) {
                newCell.setCard(selectedCell.getCard());
                selectedCell.removeCard();
                selectedCell = newCell;
            } else {
                throw new GameException("The destination has a card!");
            }
        } else {
            throw new GameException("The distance is more than 2!");
        }
    }

    public void attack(String cardId) {
        Cell opponentCell = getCellByCardId(cardId);
        Hero self = (Hero) selectedCell.getCard();
        Hero opponent = (Hero) opponentCell.getCard();
        if (self.getInGame().isArmed()) {
            if (checkRangeOfAttack(opponentCell)) {
                opponent.getInGame().decreaseHealthPoint(self.getAttackPower());
                if (checkRangeOfCounterAttack(opponentCell)) {
                    self.getInGame().decreaseHealthPoint(opponent.getAttackPower());
                }
            } else {
                throw new GameException("opponent is unavailable for attack");
            }
        } else {
            throw new GameException("You cannot move!");
        }
    }

    public void useSpecialPower(int x, int y) {
        Cell pointedCell = board.get(y).get(x);
        Hero soldier = (Hero) selectedCell.getCard();
        Spell spell = soldier.getSpecialPower();
        ArrayList<Cell> cells;
        for (Buff buff : spell.getEffects()) {
            cells = getTargetCellsByRange(buff.getRange(), pointedCell);
            if (buff.getTarget().equals(TargetType.CELL)) {
                //applyBuffOnCells(buff,cells);
                continue;
            }
            cells = filterAndGetCellsBySide(cells, buff.getSide());
            cells = filterAndGetCellsByTargetType(cells, buff.getTarget());
            //applyBuffOnSoldiersByCells(buff,cells);
        }
    }

    public void applyBuffOnCells(Buff buff, ArrayList<Cell> cells) {
        for (Cell cell : cells) {
            cell.setBuff(buff);
        }
    }

    public ArrayList<Cell> getTargetCellsByRange(RangeType range, Cell pointedCell) {
        ArrayList<Cell> cells = new ArrayList<>();
        if (range.equals(RangeType.ALL_BOARD)) {
            cells = getTargetCellsAllBoard();
        } else if (range.equals(RangeType.AROUND8)) {
            cells = getTargetCellsAround8(pointedCell);
        } else if (range.equals(RangeType.DISTANCE2)) {
            cells = getTargetCellsByDistance(pointedCell, 2);
        } else if (range.equals(RangeType.SQUARE1)) {
            cells.add(pointedCell);
        } else if (range.equals(RangeType.SQUARE2)) {
            cells = getTargetCellsBySquareSize(pointedCell, 2);
        } else if (range.equals(RangeType.SQUARE3)) {
            cells = getTargetCellsBySquareSize(pointedCell, 3);
        }
        return cells;
    }

    public ArrayList<Cell> filterAndGetCellsBySide(ArrayList<Cell> cells, SideType side) {
        if (side.equals(SideType.ENEMY)) {
            return filterAndGetCellsHasEnemy(cells);
        } else {
            return filterAndGetCellsHasSelf(cells);
        }
    }

    public ArrayList<Cell> filterAndGetCellsByTargetType(ArrayList<Cell> cells, TargetType target) {
        ArrayList filteredCells = new ArrayList(cells);
        for (Cell cell : cells) {
            if (cell.getClass().equals(Hero.class) && target.equals(TargetType.HERO)) {
                filteredCells.add(cell);
            } else if (cell.getClass().equals(Minion.class) && target.equals(TargetType.MINION)) {
                filteredCells.add(cell);
            } else if (cell.getCard() instanceof Hero && target.equals(TargetType.HERO_MINION)) {
                filteredCells.add(cell);
            }
        }
        return filteredCells;
    }

    private ArrayList<Cell> filterAndGetCellsHasEnemy(ArrayList<Cell> cells) {
        ArrayList<Cell> filteredCells = new ArrayList<>(cells);
        for (Cell cell : cells) {
            if (cell.getCard().getAccountName().equals(currentPlayer.getAccountName())) {
                filteredCells.remove(cell);
            }
        }
        return filteredCells;
    }

    private ArrayList<Cell> filterAndGetCellsHasSelf(ArrayList<Cell> cells) {
        ArrayList<Cell> filteredCells = new ArrayList<>();
        for (Cell cell : cells) {
            if (cell.getCard().getAccountName().equals(currentPlayer.getAccountName())) {
                filteredCells.add(cell);
            }
        }
        return filteredCells;
    }

    private ArrayList<Cell> getTargetCellsAllBoard() {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.get(i).size(); j++) {
                cells.add(board.get(i).get(j));
            }
        }
        return cells;
    }

    private ArrayList<Cell> getTargetCellsAround8(Cell pointedCell) {
        ArrayList<Cell> cells = new ArrayList<>();

        int leftX = pointedCell.getX() - 1;
        int upY = pointedCell.getY() - 1;
        int rightX = pointedCell.getX() + 1;
        int downY = pointedCell.getY() + 1;

        leftX = leftX < 0 ? 0 : leftX;
        upY = upY < 0 ? 0 : upY;

        rightX = rightX >= Constants.LENGTH_OF_BOARD ? Constants.LENGTH_OF_BOARD - 1 : rightX;
        downY = downY >= Constants.LENGTH_OF_BOARD ? Constants.WIDTH_OF_BOARD - 1 : downY;

        for (int i = upY; i <= downY; i++) {
            for (int j = leftX; j <= rightX; j++) {
                if (i == pointedCell.getY() && j == pointedCell.getX()) {
                    continue;
                }
                cells.add(board.get(i).get(j));
            }
        }
        return cells;
    }

    private ArrayList<Cell> getTargetCellsByDistance(Cell pointedCell, int distance) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = 0; i < Constants.WIDTH_OF_BOARD; i++) {
            for (int j = 0; j < Constants.LENGTH_OF_BOARD; j++) {
                if (i == pointedCell.getY() && j == pointedCell.getX()) {
                    continue;
                }
                if (getDistance(pointedCell, board.get(i).get(j)) <= distance) {
                    cells.add(board.get(i).get(j));
                }
            }
        }
        return cells;
    }

    private ArrayList<Cell> getTargetCellsBySquareSize(Cell pointedCell, int size) {
        ArrayList<Cell> cells = new ArrayList<>();

        int rightX = pointedCell.getX() + size - 1;
        int downY = pointedCell.getY() + size - 1;

        rightX = rightX >= Constants.WIDTH_OF_BOARD ? Constants.WIDTH_OF_BOARD - 1 : rightX;
        downY = downY >= Constants.LENGTH_OF_BOARD ? Constants.LENGTH_OF_BOARD - 1 : downY;

        for (int i = pointedCell.getY(); i <= downY; i++) {
            for (int j = pointedCell.getX(); j <= rightX; j++) {
                if (i == pointedCell.getY() && j == pointedCell.getX()) {
                    continue;
                }
                cells.add(board.get(i).get(j));
            }
        }
        return cells;
    }

    public void nextTurn() {
        if (currentPlayer.equals(player1)) {
            setCurrentPlayer(player2);
        } else {
            setCurrentPlayer(player1);
        }
        if (getCurrentPlayer().isAI()) {
            playAI();
        }
    }

    public void playAI() {
        //Run some random possible moves for the current player;
        nextTurn();
    }

    public boolean checkRangeOfAttack(Cell opponentCell) {
        Hero self = (Hero) selectedCell.getCard();
        int distance = getDistance(selectedCell, opponentCell);
        if (self.getAttackType().equals(AttackType.MELEE)) {
            if (distance == 1) {
                return true;
            }
        }
        if (self.getAttackType().equals(AttackType.RANGED)) {
            if (distance <= self.getRange() && distance > 1) {
                return true;
            }
        }
        if (self.getAttackType().equals(AttackType.HYBRID)) {
            if (distance <= self.getRange()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkRangeOfCounterAttack(Cell opponentCell) {
        Hero opponent = (Hero) opponentCell.getCard();
        int distance = getDistance(selectedCell, opponentCell);
        if (opponent.getAttackType().equals(AttackType.MELEE)) {
            if (distance == 1) {
                return true;
            }
        }
        if (opponent.getAttackType().equals(AttackType.RANGED)) {
            if (distance <= opponent.getRange() && distance > 1) {
                return true;
            }
        }
        if (opponent.getAttackType().equals(AttackType.HYBRID)) {
            if (distance <= opponent.getRange()) {
                return true;
            }
        }
        return false;
    }

    public Cell getCellByCardId(String id) {
        for (List<Cell> row : board) {
            for (Cell cell : row) {
                if (cell.getCard().idEquals(id)) {
                    return cell;
                }
            }
        }
        throw new GameException("Card with this id is not on the board!");
    }*/

}
