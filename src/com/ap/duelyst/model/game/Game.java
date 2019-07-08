package com.ap.duelyst.model.game;

import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.BattleMenu;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Deck;
import com.ap.duelyst.model.MatchHistory;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.buffs.AttackBuff;
import com.ap.duelyst.model.buffs.Buff;
import com.ap.duelyst.model.buffs.ManaBuff;
import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.traget.TargetType;
import com.ap.duelyst.model.cards.*;
import com.ap.duelyst.model.items.CollectableItem;
import com.ap.duelyst.model.items.Item;
import com.ap.duelyst.view.GameEvents;
import com.ap.duelyst.view.card.CardSprite;
import javafx.application.Platform;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class Game {
    private Player player1;
    private Player player2;
    private int round;
    private Player currentPlayer;
    private List<List<Cell>> board = new ArrayList<>();
    private BattleMenu.CustomGameMode mode;
    private int flagNumber;
    private int reward;
    private GraveYard graveYard = new GraveYard();
    private GameEvents events;


    public static Game createGame(Account account1, Account account2,
                                  BattleMenu.CustomGameMode mode, Deck deck,
                                  int flagNumber, int reward) {
        Player player1 = new Player(account1.getUserName(), account1.getCloneDeck(),
                false);
        Player player2;
        if (account2 == null) {
            player2 = new Player("AI", deck, true);
        } else {
            player2 = new Player(account2.getUserName(), account2.getMainDeck(), false);
        }
        return new Game(player1, player2, flagNumber, mode, reward);
    }

    public Game(Player player1, Player player2, int flagNumber,
                BattleMenu.CustomGameMode mode, int reward) {
        this.player1 = player1;
        this.player2 = player2;
        this.flagNumber = flagNumber;
        this.mode = mode;
        this.reward = reward;
        initGameBoard();
        List<CollectableItem> collectableItems = Utils.getShop().getCollectableItems();
        for (int i = 0, collectableItemsSize = collectableItems.size(); i < collectableItemsSize; i++) {
            CollectableItem collectableItem = collectableItems.get(i);
            collectableItem.setId("co_item_" + i);
        }
        Collections.shuffle(collectableItems);
        try {
            board.get(0).get(4)
                    .setCollectableItem((CollectableItem) collectableItems.get(0).clone());
            board.get(4).get(4)
                    .setCollectableItem((CollectableItem) collectableItems.get(1).clone());
        } catch (CloneNotSupportedException ignored) {
        }
        switch (this.mode) {

            case KEEP_FLAG_8_ROUNDS:
                board.get(2).get(4).setHasFlag(true);
                break;
            case COLLECT_HALF_FLAGS:
                addFlags(flagNumber);
                break;
        }
    }

    private void initGameBoard() {
        for (int i = 0; i < 5; i++) {
            board.add(new ArrayList<>());
            for (int j = 0; j < 9; j++) {
                board.get(i).add(new Cell(i, j));
            }
        }
    }

    private void addFlags(int flagNumber) {
        for (int[] xy : Utils.getRandomCoordinates(flagNumber / 2)) {
            board.get(xy[0]).get(xy[1]).setHasFlag(true);
            board.get(xy[0]).get(8 - xy[1]).setHasFlag(true);
        }
        if (flagNumber % 2 != 0) {
            board.get(2).get(4).setHasFlag(true);
        }
    }

    public void startGame() {
        board.get(2).get(0).addCard(player1.getHero());
        board.get(2).get(8).addCard(player2.getHero());
        Item item = player1.startGame();
        Item item1 = player2.startGame();
        currentPlayer = player1;
        if (item != null) {
            addItemBuff(item);
        }
        currentPlayer = player2;
        if (item1 != null) {
            addItemBuff(item1);
        }
        currentPlayer = player1;
        events.startGame();
        nextRound();
    }

    public void nextRound() {
        System.out.println(currentPlayer.getAccountName() + "'s turn\n");
        round++;
        getInBoardCards().stream()
                .filter(hero -> currentPlayer.hasCard(hero) && hero instanceof Minion)
                .forEach(hero -> {
                    Minion minion = (Minion) hero;
                    if (minion.getActivationTime() == ActivationTime.PASSIVE) {
                        if (minion.getSpecialPower() != null) {
                            addMinionSpecialPower(minion,
                                    minion.getSpecialPower().getEffects());
                        }
                    }
                });
        for (List<Cell> cells : board) {
            for (Cell cell : cells) {
                cell.nextRound();
            }
        }
        currentPlayer.nextRound(round);
        if (currentPlayer.isAI()) {
            playAI();
        }
        events.nextRound(getInBoardCards());
    }

    public String endTurn() {
        for (Hero allCard : getInBoardCards()) {
            if (allCard.getInGame().isHasFlag()) {
                if (player1.hasCard(allCard)) {
                    player1.addFlagTime();
                }
            }
        }

        currentPlayer = getOpponent();
        String result = checkEndGame();
        if (result != null) {
            events.gameEnded(result);
            return result;
        }
        nextRound();
        return null;
    }

    private String checkEndGame() {
        switch (mode) {

            case KILL_ENEMY_HERO:
                if (player1.getHero().getHealthPointInGame() <= 0) {
                    return endGame(player2, player1);
                } else if (player2.getHero().getHealthPointInGame() <= 0) {
                    return endGame(player1, player2);
                }
                break;
            case KEEP_FLAG_8_ROUNDS:
                if (player1.getFlagTime() >= 8) {
                    return endGame(player1, player2);
                } else if (player2.getFlagTime() >= 8) {
                    return endGame(player2, player1);
                }
                break;
            case COLLECT_HALF_FLAGS:
                int p1 = 0, p2 = 0;
                for (Hero inBoardCard : getInBoardCards()) {
                    if (player1.hasCard(inBoardCard)) {
                        if (inBoardCard.getInGame().isHasFlag()) {
                            p1++;
                        }
                    } else {
                        if (inBoardCard.getInGame().isHasFlag()) {
                            p2++;
                        }
                    }
                }
                if (p1 >= Math.ceil(flagNumber / 2f)) {
                    return endGame(player1, player2);
                } else if (p2 >= Math.ceil(flagNumber / 2f)) {
                    return endGame(player2, player1);
                }
                break;
        }
        return null;
    }

    private String endGame(Player winner, Player loser) {
        if (!winner.isAI()) {
            Utils.getAccountByUsername(winner.getAccountName()).increaseBudget(reward);
            Utils.getAccountByUsername(winner.getAccountName())
                    .addToHistory(new MatchHistory(loser.getAccountName(),
                            true, new Date()));
        }
        if (!loser.isAI()) {
            Utils.getAccountByUsername(loser.getAccountName())
                    .addToHistory(new MatchHistory(winner.getAccountName()
                            , false, new Date()));
        }
        return winner.getAccountName()
                + " has won the game and got rewarded. reward: " + reward;
    }

    public String getInGameHelp() {
        StringBuilder builder = new StringBuilder("selectable cards:\n\n");
        getInGameCards().stream()
                .filter(card -> currentPlayer.hasCard(card))
                .forEach(card -> {
                    builder.append("id: ").append(card.getId()).append("\n");
                    if (card instanceof Hero) {
                        Hero hero = ((Hero) card);
                        builder.append("\tcan move: ")
                                .append(hero.getX() >= 0 && !hero.getInGame().isMoved())
                                .append("\n");
                        builder.append("\tcan attack: ")
                                .append(hero.getX() >= 0 && !hero.getInGame().isAttacked())
                                .append("\n");
                        if (hero instanceof Minion) {
                            Minion minion = (Minion) hero;
                            builder.append("\tcan be inserted: ")
                                    .append(minion.getX() < 0
                                            && minion.getMana() <= currentPlayer.getMana())
                                    .append("\n");
                        } else {
                            builder.append("\tcan use special power: ")
                                    .append(!hero.isPassive() && !hero.isOnAttack()
                                            && hero.getSpecialPower() != null
                                            && hero.getSpecialPowerMana() < currentPlayer.getMana()
                                            && hero.getInGame().getCoolDown() <= 0)
                                    .append("\n");
                        }
                    } else {
                        Spell spell = (Spell) card;
                        builder.append("\tcan be inserted: ")
                                .append(spell.getMana() <= currentPlayer.getMana())
                                .append("\n");
                    }
                });
        builder.append("\n\nselectable collectibles:\n\n");
        for (CollectableItem collectableItem : currentPlayer.getCollectableItems()) {
            builder.append("id: ").append(collectableItem.getId()).append("\n");
        }
        return builder.toString();
    }

    public String getCardHelp(Card card) {

        StringBuilder builder = new StringBuilder();
        if (card instanceof Hero) {
            Hero hero = ((Hero) card);
            List<int[]> pos = new ArrayList<>();
            switch (hero.getAttackType()) {
                case MELEE:
                    pos = getNeighbours(hero.getX(), hero.getY());
                    break;
                case RANGED:
                    pos = getCellsInRange(hero.getX(), hero.getY(), hero.getRange());
                    for (int[] neighbour : getNeighbours(hero.getX(), hero.getY())) {
                        int index = pos.indexOf(neighbour);
                        if (index >= 0) {
                            pos.remove(index);
                        }
                    }
                    break;
                case HYBRID:
                    pos = getCellsInRange(hero.getX(), hero.getY(), hero.getRange());
                    break;
            }
            pos.removeIf(p -> board.get(p[0]).get(p[1]).getCard() == null);

            boolean canMove = hero.getX() >= 0 && !hero.getInGame().isMoved();
            builder.append("\tcan move: ")
                    .append(canMove)
                    .append("\n");
            if (canMove) {
                builder.append("\tpossible targets: ");
                for (int[] p : getCellsInRange(hero.getX(), hero.getY(), 2)) {
                    if (board.get(p[0]).get(p[1]).getCard() == null) {
                        p[0]++;
                        p[1]++;
                        builder.append(Arrays.toString(p)).append("  ");
                    }
                }
                builder.append("\n");
            }
            boolean canAttack = hero.getX() >= 0 && !hero.getInGame().isAttacked();
            builder.append("\tcan attack: ")
                    .append(canAttack)
                    .append("\n");
            if (canAttack) {
                builder.append("\tpossible targets: ");
                for (int[] p : pos) {
                    builder.append("opponent id: ")
                            .append(board.get(p[0]).get(p[1]).getCard().getId())
                            .append(",  ");
                }
                builder.append("\n");
            }

            if (hero instanceof Minion) {
                Minion minion = (Minion) hero;
                boolean canInsert =
                        minion.getX() < 0 && minion.getMana() <= currentPlayer.getMana();
                builder.append("\tcan be inserted: ")
                        .append(canInsert)
                        .append("\n");
                if (canInsert) {
                    builder.append("\tpossible targets: ");
                    getInBoardCards().stream().filter(hero1 -> currentPlayer.hasCard(hero1))
                            .forEach(hero1 -> {
                                for (int[] p : getNeighbours(hero1.getX(),
                                        hero1.getY())) {
                                    if (board.get(p[0]).get(p[1]).getCard() == null) {
                                        p[0]++;
                                        p[1]++;
                                        builder.append(Arrays.toString(p))
                                                .append("  ");
                                    }
                                }
                            });
                    builder.append("\n");
                }
            } else {
                builder.append("\tcan use special power: ")
                        .append(!hero.isPassive() && !hero.isOnAttack()
                                && hero.getSpecialPower() != null
                                && hero.getSpecialPowerMana() < currentPlayer.getMana()
                                && hero.getInGame().getCoolDown() <= 0)
                        .append("\n");
            }
        } else {
            Spell spell = (Spell) card;
            builder.append("\tcan be inserted: ")
                    .append(spell.getMana() <= currentPlayer.getMana())
                    .append("\n");
        }
        return builder.toString();
    }

    public String getInfo() {

        String info = "";
        String info1 = "team: " + player1.getAccountName() + "\n";
        info1 += "\tmana: " + player1.getMana() + "\n";
        String info2 = "team: " + player2.getAccountName() + "\n";
        info2 += "\tmana: " + player2.getMana() + "\n";
        switch (mode) {

            case KILL_ENEMY_HERO:
                info1 += "\thero HP: " + player1.getHero().getHealthPointInGame() + "\n";
                info2 += "\thero HP: " + player2.getHero().getHealthPointInGame() + "\n";
                break;
            case KEEP_FLAG_8_ROUNDS:
                info = "flag position: " + Arrays.toString(getFlagPosition()) + "\n";
                Card card =
                        board.get(getFlagPosition()[0] - 1).get(getFlagPosition()[1] - 1)
                                .getCard();
                if (card != null) {
                    if (player1.hasCard(card)) {
                        info1 += "\t has flag";
                    } else {
                        info2 += "\t has flag";
                    }
                }
                break;
            case COLLECT_HALF_FLAGS:
                info1 += "\tplayers with flag names: " + player1.getPlayersWithFlag() + "\n";
                info2 += "\tplayers with flag names: " + player2.getPlayersWithFlag() + "\n";

                break;
        }
        return info + info1 + info2;
    }

    private int[] getFlagPosition() {
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.get(i).size(); j++) {
                if (board.get(i).get(j).isHasFlag()) {
                    return new int[]{i + 1, j + 1};
                }
                Hero hero = board.get(i).get(j).getCard();
                if (hero != null && hero.getInGame().isHasFlag()) {
                    return new int[]{i + 1, j + 1};
                }
            }
        }
        return new int[]{0, 0};
    }

    public String showMinions(boolean ally) {
        Player player;
        if (ally) {
            player = currentPlayer;
        } else {
            player = getOpponent();
        }
        StringBuilder builder = new StringBuilder();
        for (Card card : getInGameCards()) {
            if (card instanceof Hero && player.hasCard(card)) {
                Hero heroMinion = (Hero) card;
                builder.append(heroMinion.getId()).append(": ").append(heroMinion.getName())
                        .append(",  health: ").append(heroMinion.getHealthPointInGame())
                        .append(",  location: ").append(heroMinion.getInGame().getPos())
                        .append(",  power: ").append(heroMinion.getAttackPowerInGame())
                        .append("\n");
            }
        }
        return builder.toString();
    }

    public String showCardInfo(String id) {
        return getCard(id).getInGameInfo();
    }

    public Card selectCard(String id) {
        Card card = getCard(id);
        if (currentPlayer.hasCard(card)) {
            return card;
        }
        throw new GameException("it's not your turn");
    }

    public boolean move(Card card, int x, int y) {

        if (!(card instanceof Hero)) {
            throw new GameException("card is not movable");
        } else {
            Hero hero = (Hero) card;
            int oldX = hero.getX();
            int oldY = hero.getY();
            if (hero.getX() < 0) {
                throw new GameException("card is not in board");
            }
            int distance = getDistance(hero.getX(), hero.getY(), x, y);
            if (checkOutOfBounds(x, y) && distance <= 2 && checkRoad(hero.getX(),
                    hero.getY(),
                    x, y)) {
                if (hero.getInGame().isMoved()) {
                    throw new GameException("card has moved before");
                }
                board.get(hero.getX()).get(hero.getY()).removeCard(false);
                CollectableItem item = board.get(x).get(y).addCard(card);
                if (item != null) {
                    currentPlayer.addCollectableItem(item);
                }
                hero.getInGame().setMoved(true);
                if (currentPlayer.isAI()) {
//                    Platform.runLater(() -> events.AIMove(hero, oldX, oldY, x, y));
                }
                return true;
            } else {
                throw new GameException("invalid target");
            }
        }
    }

    public List<CardSprite> attack(Card ally, String opponentId, boolean shouldCounter) {
        Card opponent = getInBoardCard(opponentId);
        if (opponent == null || !getOpponent().hasCard(opponent)) {
            throw new GameException("invalid card id");
        }
        if (!(ally instanceof Hero)) {
            throw new GameException("card with id: " + ally.getId() + " cant attack");
        }
        Hero allyHero = (Hero) ally;
        Hero opponentHero = (Hero) opponent;
        if (allyHero.getX() < 0) {
            throw new GameException("ally card is not in board");
        }
        if (opponentHero.getX() < 0) {
            throw new GameException("opponent card is not in board");
        }
        if (!checkRange(allyHero, opponentHero)) {
            throw new GameException("opponent minion is unavailable for attack");
        }
        if (!allyHero.getInGame().isMovable()) {
            throw new GameException("card with id: " + ally.getId() + " cant attack");
        }
        if (allyHero.getInGame().isAttacked()) {
            throw new GameException("card has attacked before");
        }

        if (allyHero instanceof Minion) {
            if (!opponentHero.isCanBeAttackedBySmallerMinions()) {
                if (allyHero.getAttackPowerInGame() < opponentHero.getAttackPowerInGame()) {
                    return new ArrayList<>();
                }
            }
        }
        allyHero.attack(opponentHero);
        allyHero.getInGame().setAttacked(true);
        for (Item item : allyHero.getInGame().getItems()) {
            if (item.getActivationTime() == ActivationTime.ON_ATTACK) {
                applyNonePassiveItem(item, allyHero, opponentHero);
            }
        }
        if (allyHero.isOnAttack()) {
            addAttackBuffs(opponentHero, allyHero.getSpecialPower().getEffects());
        }
        if (allyHero instanceof Minion) {
            Minion minion = (Minion) allyHero;
            if (minion.getActivationTime() == ActivationTime.ON_ATTACK) {
                if (minion.getSpecialPower() != null) {
                    addAttackBuffs(opponentHero, minion.getSpecialPower().getEffects());
                }
            }
        }
        if (shouldCounter) {
            if (opponentHero.getInGame().isArmed()) {
                if (checkRange(opponentHero, allyHero)) {
                    opponentHero.attack(allyHero);
                }
            }
        }
        return Arrays.asList(checkDead(allyHero), checkDead(opponentHero));


    }

    public void attackCombo(Card ally, String opponentId, String... ids) {
        if (!(ally instanceof Minion)) {
            throw new GameException("card with id: " + ally.getId() + " cant attack " +
                    "combo");
        }
        Minion allyMinion = (Minion) ally;
        if (allyMinion.getActivationTime() != ActivationTime.COMBO) {
            throw new GameException("card with id: " + allyMinion.getId() + " cant " +
                    "attack combo");
        }
        List<Minion> minions = new ArrayList<>();
        minions.add(allyMinion);
        for (String id : ids) {
            Card backup = getInBoardCard(id);
            if (!(backup instanceof Minion)) {
                throw new GameException("card with id: " + ally.getId() + " cant attack" +
                        " combo");
            }
            if (((Minion) backup).getActivationTime() != ActivationTime.COMBO) {
                throw new GameException("card with id: " + backup.getId() + " cant " +
                        "attack combo");
            }
            minions.add((Minion) backup);
        }
        for (int i = 0; i < minions.size(); i++) {
            Minion minion = minions.get(i);
            try {
                attack(minion, opponentId, i == 0);
            } catch (GameException e) {
                System.out.println(e.getMessage());
            }

        }

    }

    public String showHand() {
        StringBuilder builder = new StringBuilder();
        List<Card> cards = new ArrayList<>(currentPlayer.getHand());
        cards.add(currentPlayer.getNextCard());
        for (Card card : cards) {
            builder.append("card name: ").append(card.getName()).append(" ,  ");
        }
        return builder.toString();
    }

    public String showNextCard() {
        return currentPlayer.getNextCard().getInGameInfo();
    }

    public void useSpecialPower(Card card, int x, int y) {
        if (card.getClass() == Hero.class) {
            Hero hero = (Hero) card;
            if (hero.getSpecialPower() == null || hero.isOnAttack() || hero.isPassive()) {
                throw new GameException("hero doesnt have special power or its power is" +
                        " not controllable by player");
            }
            if (currentPlayer.getMana() < hero.getSpecialPowerMana()) {
                throw new GameException("not enough mana");
            }
            if (hero.getInGame().getCoolDown() > 0) {
                throw new GameException("cool down time is not over yet");
            }
            {
                currentPlayer.decreaseMana(hero.getSpecialPowerMana());
                hero.getInGame().setCoolDown(hero.getCoolDown());
                addHeroSpecialPower(hero.getSpecialPower().getEffects(), hero, x, y);
            }
        } else {
            throw new GameException("card's not a hero");
        }
    }

    public String insert(Card card, int x, int y) {
        if (card.getClass() == Hero.class) {
            throw new GameException("hero cant be inserted");
        }
        if (card instanceof Minion) {
            Minion minion = (Minion) card;
            if (minion.getMana() > currentPlayer.getMana()) {
                throw new GameException("not enough mana");
            }
            if (!checkOutOfBounds(x, y) || board.get(x).get(y).getCard() != null) {
                throw new GameException("invalid target");
            }
            boolean validTarget = false;
            for (Hero inBoardCard : getInBoardCards()) {
                if (currentPlayer.hasCard(inBoardCard)) {
                    if (getNeighbours(inBoardCard.getX(), inBoardCard.getY())
                            .contains(new int[]{x, y})) {
                        validTarget = true;
                        break;
                    }
                }
            }
            if (!validTarget) {
                throw new GameException("invalid target");
            }
            CollectableItem collectableItem = board.get(x).get(y).addCard(minion);
            if (collectableItem != null) {
                currentPlayer.addCollectableItem(collectableItem);
            }
            currentPlayer.decreaseMana(minion.getMana());
            if (minion.getSpecialPower() != null
                    && minion.getActivationTime() == ActivationTime.ON_SPAWN) {
                addMinionSpecialPower(minion, minion.getSpecialPower().getEffects());
            }
            for (Item item : minion.getInGame().getItems()) {
                if (item.getActivationTime() == ActivationTime.ON_SPAWN) {
                    applyNonePassiveItem(item, minion, null);
                }
            }

        } else if (card instanceof Spell) {
            Spell spell = (Spell) card;
            if (spell.getMana() > currentPlayer.getMana()) {
                throw new GameException("not enough mana");
            }
            if (!checkOutOfBounds(x, y)) {
                throw new GameException("invalid target");
            }
            addSpell(spell.getEffects(), x, y);
            currentPlayer.decreaseMana(spell.getMana());
        }
        currentPlayer.getHand().remove(card);
        return card.getName() + " with card id: " + card.getId()
                + " inserted to (" + (x + 1) + "," + (y + 1) + ")";

    }

    public String showCollectables() {
        StringBuilder builder = new StringBuilder();
        for (CollectableItem collectableItem : currentPlayer.getCollectableItems()) {
            builder.append(collectableItem.getInfo()).append("\n");
        }
        return builder.toString();
    }

    public CollectableItem selectCollectable(String id) {
        return currentPlayer.getCollectableItems().stream()
                .filter(collectableItem -> collectableItem.idEquals(id))
                .findFirst()
                .orElseThrow(() -> new GameException("collectable not found"));

    }

    public String getCollectableInfo(CollectableItem collectableItem) {
        return collectableItem.getInfo();
    }

    public void useCollectable(CollectableItem collectableItem) {
        addItemBuff(collectableItem);
        currentPlayer.removeCollectableItem(collectableItem);
    }

    private CardSprite checkDead(Hero card) {
        if (card.getHealthPointInGame() <= 0) {
            if (card instanceof Minion && card.getSpecialPower() != null) {
                for (Item item : card.getInGame().getItems()) {
                    if (item.getActivationTime() == ActivationTime.ON_SPAWN) {
                        applyNonePassiveItem(item, card, null);
                    }
                }

                if (((Minion) card).getActivationTime() == ActivationTime.ON_DEATH) {
                    addMinionSpecialPower((Minion) card,
                            card.getSpecialPower().getEffects());
                }
            }
            graveYard.add(card);
            board.get(card.getX()).get(card.getY()).removeCard(true);
            return card.getCardSprite();
        }
        return null;
    }

    private void addHeroSpecialPower(List<Buff> buffs, Hero hero, int x, int y) {
        if (!checkOutOfBounds(x, y)) {
            throw new GameException("invalid target");
        }
        for (Buff buff : buffs) {
            if (buff.getTarget() == TargetType.CELL) {
                board.get(x).get(y).addBuff(buff);
            }
            switch (buff.getRange()) {

                case ALL_BOARD:
                    List<Hero> heroMinions = getAllCards();
                    for (Hero heroMinion : heroMinions) {
                        handleSide(buff, heroMinion);
                    }
                    break;
                case ONE:
                    Hero card = board.get(x).get(y).getCard();
                    if (card == null) {
                        throw new GameException("invalid target");
                    }
                    if (!handleSide(buff, card)) {
                        throw new GameException("invalid target");
                    }
                    break;
                case SELF:
                    handleSide(buff, hero);
                    break;
                case ALL_IN_ONE_ROW:
                    for (List<Cell> cells : board) {
                        for (Cell cell : cells) {
                            if (cell.getX() == hero.getX()) {
                                if (cell.getCard() == null) {
                                    throw new GameException("invalid target");
                                }
                                if (!handleSide(buff, cell.getCard())) {
                                    throw new GameException("invalid target");
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void addMinionSpecialPower(Minion minion, List<Buff> buffs) {
        for (Buff buff : buffs) {

            switch (buff.getRange()) {

                case ALL_BOARD:
                    List<Hero> heroMinions = getAllCards();
                    for (Hero heroMinion : heroMinions) {
                        handleSide(buff, heroMinion);
                    }
                    break;
                case ONE:
                    if (buff.isRandom()) {
                        List<Hero> cards = getInBoardCards();
                        cards = cards.stream().filter(hero -> hero.getClass() != Hero.class)
                                .collect(Collectors.toList());
                        Collections.shuffle(cards);
                        if (!cards.isEmpty()) {
                            handleSide(buff, cards.get(0));
                        }
                    } else {
                        handleSide(buff, getOpponent().getHero());
                    }
                    break;
                case SELF:
                    handleSide(buff, minion);
                    break;
                case AROUND8:
                    List<int[]> ints = getCellsInRange(minion.getX(), minion.getY(), 8);
                    for (int[] pos : ints) {
                        Cell cell = board.get(pos[0]).get(pos[1]);
                        if (cell.getCard() != null) {
                            handleSide(buff, cell.getCard());
                        }
                    }
                    break;
                case AROUND8_AND_SELF:
                    ints = getCellsInRange(minion.getX(), minion.getY(), 8);
                    for (int[] pos : ints) {
                        Cell cell = board.get(pos[0]).get(pos[1]);
                        if (cell.getCard() != null) {
                            handleSide(buff, cell.getCard());
                        }
                    }
                    handleSide(buff, minion);
                    break;
                case AROUND2:
                    ints = getCellsInRange(minion.getX(), minion.getY(), 8);
                    for (int[] pos : ints) {
                        Cell cell = board.get(pos[0]).get(pos[1]);
                        if (cell.getCard() != null) {
                            handleSide(buff, cell.getCard());
                        }
                    }
                    break;
                case ALL_IN_ONE_COLUMN:
                    for (List<Cell> cells : board) {
                        for (Cell cell : cells) {
                            if (cell.getY() == minion.getY()) {
                                if (cell.getCard() != null) {
                                    handleSide(buff, cell.getCard());
                                }
                            }
                        }
                    }
                    break;
                case ALL_IN_ONE_ROW:
                    for (List<Cell> cells : board) {
                        for (Cell cell : cells) {
                            if (cell.getX() == minion.getX()) {
                                if (cell.getCard() != null) {
                                    handleSide(buff, cell.getCard());
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void addSpell(List<Buff> buffs, int x, int y) {
        for (Buff buff : buffs) {
            if (buff.getTarget() == TargetType.CELL) {
                int range = Integer.valueOf(buff.getRange().name()
                        .replaceAll("\\D+", ""));
                for (int i = 0; i < range; i++) {
                    for (int j = 0; j < range; j++) {
                        if (checkOutOfBounds(x + i, y + j)) {
                            board.get(x + i).get(y + j).addBuff(buff);
                        }
                    }
                }
            }
            switch (buff.getRange()) {

                case ALL_BOARD:
                    List<Hero> heroMinions = getAllCards();
                    for (Hero heroMinion : heroMinions) {
                        handleSide(buff, heroMinion);
                    }
                    break;
                case ONE:
                    Cell cell = board.get(x).get(y);
                    if (cell.getCard() == null) {
                        throw new GameException("invalid target");
                    }
                    if (!handleSide(buff, cell.getCard())) {
                        throw new GameException("invalid target");
                    }
                    break;
                case SQUARE2:
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            if (checkOutOfBounds(x + i, y + j)) {
                                cell = board.get(x + i).get(y + j);
                                if (cell.getCard() != null) {
                                    handleSide(buff, cell.getCard());
                                }
                            }
                        }
                    }
                    break;
                case SQUARE3:
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (checkOutOfBounds(x + i, y + j)) {
                                cell = board.get(x + i).get(y + j);
                                if (cell.getCard() != null) {
                                    handleSide(buff, cell.getCard());
                                }
                            }
                        }
                    }
                    break;
                case AROUND8:
                    List<int[]> ints = getCellsInRange(x, y, 8);
                    Collections.shuffle(ints);
                    for (int[] pos : ints) {
                        cell = board.get(pos[0]).get(pos[1]);
                        if (cell.getCard() != null) {
                            handleSide(buff, cell.getCard());
                            break;
                        }
                    }
                    break;
                case ALL_IN_ONE_COLUMN:
                    for (List<Cell> cells : board) {
                        for (Cell cell1 : cells) {
                            if (cell1.getY() == y) {
                                if (cell1.getCard() != null) {
                                    handleSide(buff, cell1.getCard());
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void addItemBuff(Item item) {
        for (Buff buff : item.getBuffs()) {
            if (buff instanceof ManaBuff) {
                currentPlayer.addManaBuff((ManaBuff) buff);
            } else {
                if (item.getActivationTime() == ActivationTime.PASSIVE) {
                    switch (buff.getRange()) {

                        case ALL_BOARD:
                            List<Hero> heroMinions = getAllCards();
                            Collections.shuffle(heroMinions);
                            switch (buff.getSide()) {
                                case ALLY:
                                    heroMinions =
                                            heroMinions.stream()
                                                    .filter(hero -> currentPlayer.hasCard(hero))
                                                    .filter(hero -> checkTarget(buff,
                                                            hero))
                                                    .collect(Collectors.toList());
                                    if (buff.getAllyAttackTypes() != null) {
                                        heroMinions = heroMinions.stream()
                                                .filter(hero -> buff.getAllyAttackTypes()
                                                        .contains(hero.getAttackType()))
                                                .collect(Collectors.toList());
                                    }
                                    for (Hero heroMinion : heroMinions) {
                                        heroMinion.getInGame().addBuff(buff);
                                        buff.applyBuff(heroMinion);
                                    }
                                    break;
                                case ENEMY:
                                    heroMinions =
                                            heroMinions.stream()
                                                    .filter(hero -> getOpponent().hasCard(hero))
                                                    .filter(hero -> checkTarget(buff,
                                                            hero))
                                                    .collect(Collectors.toList());
                                    if (buff.getEnemyAttackTypes() != null) {
                                        heroMinions = heroMinions.stream()
                                                .filter(hero -> buff.getEnemyAttackTypes()
                                                        .contains(hero.getAttackType()))
                                                .collect(Collectors.toList());
                                    }
                                    for (Hero heroMinion : heroMinions) {
                                        heroMinion.getInGame().addBuff(buff);
                                    }
                                    break;
                            }
                            break;
                        case ONE:
                            heroMinions = getInBoardCards();
                            Collections.shuffle(heroMinions);
                            switch (buff.getSide()) {
                                case ALLY:
                                    heroMinions =
                                            heroMinions.stream()
                                                    .filter(hero -> currentPlayer.hasCard(hero))
                                                    .filter(hero -> checkTarget(buff,
                                                            hero))
                                                    .collect(Collectors.toList());
                                    if (buff.getAllyAttackTypes() != null) {
                                        heroMinions = heroMinions.stream()
                                                .filter(hero -> buff.getAllyAttackTypes()
                                                        .contains(hero.getAttackType()))
                                                .collect(Collectors.toList());
                                    }
                                    if (!heroMinions.isEmpty()) {
                                        heroMinions.get(0).getInGame().addBuff(buff);
                                        buff.applyBuff(heroMinions.get(0));

                                    }
                                    break;
                                case ENEMY:
                                    heroMinions =
                                            heroMinions.stream()
                                                    .filter(hero -> getOpponent().hasCard(hero))
                                                    .filter(hero -> checkTarget(buff,
                                                            hero))
                                                    .collect(Collectors.toList());
                                    if (buff.getEnemyAttackTypes() != null) {
                                        heroMinions = heroMinions.stream()
                                                .filter(hero -> buff.getEnemyAttackTypes()
                                                        .contains(hero.getAttackType()))
                                                .collect(Collectors.toList());
                                    }
                                    if (!heroMinions.isEmpty()) {
                                        heroMinions.get(0).getInGame().addBuff(buff);
                                    }
                                    break;
                            }
                            break;
                    }
                } else {
                    if (buff.getAllyType() != null) {
                        switch (buff.getAllyType()) {
                            case HERO:
                                Hero hero = currentPlayer.getHero();
                                if (buff.getAllyAttackTypes() != null) {
                                    if (buff.getAllyAttackTypes().contains(hero.getAttackType())) {
                                        hero.getInGame().addItem(item);
                                    } else {
                                        hero.getInGame().addItem(item);
                                    }
                                }
                                break;
                            case ALL_MINIONS:
                                getAllCards().stream().filter(hero1 -> currentPlayer.hasCard(hero1))
                                        .filter(hero1 -> hero1 instanceof Minion)
                                        .forEach(hero1 -> hero1.getInGame().addItem(item));
                                break;
                        }
                    } else {
                        List<Hero> cards = getInBoardCards();
                        Collections.shuffle(cards);
                        if (buff instanceof AttackBuff) {
                            cards.stream().filter(hero -> currentPlayer.hasCard(hero))
                                    .filter(hero -> hero instanceof Minion).findFirst()
                                    .ifPresent(hero -> hero.getInGame().addItem(item));
                        } else {
                            for (Hero card : cards) {
                                card.getInGame().addItem(item);
                            }
                        }
                    }
                }
            }
        }
    }

    private void applyNonePassiveItem(Item item, Hero hero, Hero opponent) {
        for (Buff buff : item.getBuffs()) {
            switch (buff.getRange()) {
                case ONE:
                    if (buff.isRandom()) {
                        List<Hero> heroes = getInBoardCards();
                        heroes = heroes.stream().filter(hero1 -> checkTarget(buff, hero1))
                                .filter(hero1 -> {
                                    if (buff.getSide() == SideType.ALLY) {
                                        return currentPlayer.hasCard(hero1);
                                    } else {
                                        return getOpponent().hasCard(hero1);
                                    }
                                }).collect(Collectors.toList());
                        Collections.shuffle(heroes);
                        if (!heroes.isEmpty()) {
                            heroes.get(0).getInGame().addBuff(buff);
                            if (buff.getSide() == SideType.ALLY) {
                                buff.applyBuff(heroes.get(0));
                            }
                        }
                    } else {
                        switch (buff.getTarget()) {
                            case HERO:
                                getOpponent().getHero().getInGame().addBuff(buff);
                                break;
                            case MINION:
                                if (opponent instanceof Minion) {
                                    opponent.getInGame().addBuff(buff);
                                }
                                break;
                        }
                    }
                    break;
                case SELF:
                    hero.getInGame().addBuff(buff);
                    buff.applyBuff(hero);
                    break;
            }
        }
    }

    private boolean handleSide(Buff buff, Hero heroMinion) {
        switch (buff.getSide()) {
            case ALLY:
                if (currentPlayer.hasCard(heroMinion) && checkTarget(buff, heroMinion)) {
                    try {
                        Buff buff1 = buff.clone();
                        heroMinion.getInGame().addBuff(buff1);
                        buff1.applyBuff(heroMinion);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    return true;

                }
                break;
            case ENEMY:
                if (getOpponent().hasCard(heroMinion) && checkTarget(buff, heroMinion)) {
                    try {
                        heroMinion.getInGame().addBuff(buff.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    return true;

                }
                break;

            case ALL:
                if (checkTarget(buff, heroMinion)) {
                    try {
                        heroMinion.getInGame().addBuff(buff.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    return true;

                }
                break;
        }
        return false;
    }

    private boolean checkTarget(Buff buff, Card card) {
        return buff.getTarget().getType().contains(card.getClass().getSimpleName());
    }

    private void addAttackBuffs(Hero opponent, List<Buff> buffs) {
        for (Buff buff : buffs) {
            if (buff.getTarget().getType().contains(opponent.getClass().getSimpleName())) {
                opponent.getInGame().addBuff(buff);
            }
        }
    }

    private Player getOpponent() {
        if (currentPlayer == player1) {
            return player2;
        } else {
            return player1;
        }
    }

    private boolean checkRange(Hero ally, Hero opponent) {
        switch (ally.getAttackType()) {
            case MELEE:
                return getNeighbours(ally.getX(), ally.getY())
                        .contains(new int[]{opponent.getX(), opponent.getY()});
            case RANGED:
                List<int[]> ints = getCellsInRange(ally.getX(), ally.getY(),
                        ally.getRange());
                for (int[] neighbour : getNeighbours(ally.getX(), ally.getY())) {
                    int index = ints.indexOf(neighbour);
                    if (index >= 0) {
                        ints.remove(index);
                    }
                }
                return ints.contains(new int[]{opponent.getX(), opponent.getY()});
            case HYBRID:
                return getCellsInRange(ally.getX(), ally.getY(), ally.getRange())
                        .contains(new int[]{opponent.getX(),
                                opponent.getY()});
        }
        return false;
    }

    public List<int[]> getNeighbours(int x, int y) {
        List<int[]> ints = Utils.getEmptyPosList();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (checkOutOfBounds(x + i, y + j)) {
                    ints.add(new int[]{x + i, y + j});
                }
            }
        }
        return ints;
    }

    public List<int[]> getCellsInRange(int x, int y, int range) {
        List<int[]> ints = Utils.getEmptyPosList();
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (Math.abs(i) + Math.abs(j) <= range && checkOutOfBounds(x + i,
                        y + j)) {
                    ints.add(new int[]{x + i, y + j});
                }
            }
        }
        return ints;
    }

    private boolean checkOutOfBounds(int x, int y) {
        return x >= 0 && y >= 0 && x <= 4 && y <= 8;
    }

    public Card getCard(String id) {
        if (getInBoardCard(id) != null) {
            return getInBoardCard(id);
        }
        List<Card> cards = new ArrayList<>();
        cards.addAll(player1.getHand());
        cards.addAll(player2.getHand());
        cards.add(player1.getHero());
        cards.add(player2.getHero());
        return cards.stream().filter(card -> card.getId().equals(id))
                .findFirst().orElseThrow(() -> new GameException("no such card in game"));
    }

    public List<Card> getInGameCards() {
        List<Card> cards = new ArrayList<>();
        cards.addAll(getInBoardCards());
        cards.addAll(player1.getHand());
        cards.addAll(player2.getHand());
        return cards;
    }

    private List<Hero> getAllCards() {
        List<Card> cards = new ArrayList<>();
        cards.add(player1.getHero());
        cards.add(player2.getHero());
        cards.addAll(player1.getDeck().getCards());
        cards.addAll(player2.getDeck().getCards());
        return cards.stream().filter(card -> card instanceof Hero)
                .map(card -> (Hero) card).collect(Collectors.toList());
    }

    public List<Hero> getInBoardCards() {
        List<Hero> cards = new ArrayList<>();
        for (List<Cell> cells : board) {
            for (Cell cell : cells) {
                if (cell.getHeroMinion() != null) {
                    cards.add(cell.getHeroMinion());
                }
            }
        }
        return cards;
    }

    private Card getInBoardCard(String id) {
        for (List<Cell> cells : board) {
            for (Cell cell : cells) {
                if (cell.getHeroMinion() != null && cell.getHeroMinion().getId().equals(id)) {
                    return cell.getHeroMinion();
                }
            }
        }
        return null;
    }

    public boolean checkRoad(int x1, int y1, int x2, int y2) {
        boolean ans = board.get(x2).get(y2).isEmpty();
        if (Math.abs(x1 - x2) == 2) {
            ans = ans && board.get((x1 + x2) / 2).get(y2).isEmpty();
        }
        if (Math.abs(y1 - y2) == 2) {
            ans = ans && board.get(x2).get((y1 + y2) / 2).isEmpty();
        }
        return ans;
    }

    private int getDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) +
                Math.abs(y1 - y2);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GraveYard getGraveYard() {
        return graveYard;
    }

    private void playAI() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    int x = new Random().nextInt(3) - 1;
                    int y = new Random().nextInt(3) - 1;
                    switch (new Random().nextInt(5)) {
                        case 0:
                            insert(currentPlayer.getHand().get(0),
                                    currentPlayer.getHero().getX() + x,
                                    currentPlayer.getHero().getY() + y);
                            break;
                        case 1:
                            move(currentPlayer.getHero(),
                                    currentPlayer.getHero().getX() + x,
                                    currentPlayer.getHero().getY() + y);
                            break;
                        case 2:
                            attack(currentPlayer.getHero(),
                                    getOpponent().getHero().getId(),
                                    true);
                            break;
                        case 3:
                            useSpecialPower(currentPlayer.getHero(), 0, 0);
                            break;
                        case 4:
                            if (!currentPlayer.getCollectableItems().isEmpty()) {
                                addItemBuff(currentPlayer.getCollectableItems().get(0));
                            }
                    }

                } catch (GameException ignored) {
                }
            }
        }, 600);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> endTurn());
            }
        }, 1800);
    }

    public void setEvents(GameEvents events) {
        this.events = events;
    }

    public boolean isEmpty(int x, int y) {
        return board.get(x).get(y).getCard() == null;
    }

    public Hero getCardAt(int x, int y) {
        return board.get(x).get(y).getCard();
    }

    public List<Player> getPlayers() {
        return Arrays.asList(player1, player2);
    }

    public List<List<Cell>> getBoard() {
        return board;
    }
}
