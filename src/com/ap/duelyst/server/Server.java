package com.ap.duelyst.server;

import com.ap.duelyst.Command;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.BattleMenu;
import com.ap.duelyst.model.*;
import com.ap.duelyst.model.Collection;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.model.game.Cell;
import com.ap.duelyst.model.game.Game;
import com.ap.duelyst.model.game.GraveYard;
import com.ap.duelyst.model.game.Player;
import com.ap.duelyst.model.items.CollectableItem;
import com.ap.duelyst.model.items.Item;
import com.ap.duelyst.model.items.UsableItem;
import com.ap.duelyst.view.GameEvents;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        }
    }
}


@SuppressWarnings("Duplicates")
class ClientHandler extends Thread {
    private String token = null;
    private Account account;
    private Gson gson;
    private Socket socket;
    private Scanner reader;
    private PrintWriter writer;
    private Game game;

    ClientHandler(Socket socket) {
        this.socket = socket;
        this.gson = Utils.getGson();
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("new client added");
    }

    @Override
    public void run() {
        Command command;
        Method method;
        String input;
        while (true) {
            try {
                input = reader.nextLine();
                System.out.println(input);
                command = gson.fromJson(input, new TypeToken<Command>() {
                }.getType());
                method = getClass().getDeclaredMethod(command.getCommandName(),
                        command.getParameterTypes());
                if (command.getCommandName().equals("loginGUI")) {
                    method.invoke(this, command.getParameters());
                    token = generateToken();
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("token", token);
                    writer.println(gson.toJson(jsonObject));
                } else if (command.getCommandName().equals("createAccountGUI")) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("resp",
                            "account created successfully");
                    method.invoke(this, command.getParameters());
                    writer.println(gson.toJson(jsonObject));
                } else if (token != null && token.equals(command.getToken())) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("resp",
                            gson.toJson(method.invoke(this,
                                    command.getParameters())));
                    writer.println(gson.toJson(jsonObject));
                } else {
                    throw new GameException("authentication failed");
                }
            } catch (NoSuchMethodException | IllegalAccessException
                    | InvocationTargetException | GameException e) {
                e.printStackTrace();
                JsonObject jsonObject = new JsonObject();
                if (e instanceof InvocationTargetException) {
                    jsonObject
                            .addProperty("error", ((InvocationTargetException) e)
                                    .getTargetException().getMessage());
                } else {
                    jsonObject.addProperty("error", e.getMessage());
                }
                writer.println(jsonObject);
            }
        }
    }

    private String generateToken() {
        return Base64.getEncoder()
                .encodeToString((account.getUserName() + new Date().getTime()).getBytes());
    }

    private void loginGUI(String userName, String password) {
        if (!Utils.hasAccount(userName)) {
            throw new GameException("No account with this username");
        }
        if (account != null && account.getUserName().equals(userName)) {
            throw new GameException("You are already logged in");
        }
        Account account = Utils.getAccountByUsername(userName);
        if (account.getPassword().equals(password)) {
            this.account = account;
        } else {
            throw new GameException("Password is wrong!");
        }
    }

    private void createAccountGUI(String userName, String password) {
        if (Utils.hasAccount(userName)) {
            throw new GameException("There is an account with this username!");
        } else {
            Account account = new Account();
            account.setUserName(userName);
            account.setPassword(password);
            Utils.add(account);
        }
    }

    private String logout() {
        if (account == null) {
            throw new GameException("You are not logged in!");
        } else {
            account = null;
            token = null;
            return "logout successful";
        }
    }

    private List<Account> getAllAccounts() {
        return Utils.getAccounts();
    }

    private Shop getShop() {
        return Utils.getShop();
    }

    private long getMoney() {
        return account.getBudget();
    }

    private Collection getCollection() {
        return account.getCollection();
    }

    private String validateDeck() {
        if (account.getMainDeck() != null && account.getMainDeck().isValid()) {
            return "deck is valid";
        }
        throw new GameException("deck is invalid");
    }

    public String addCustomCard(String cardJson, String className) throws ClassNotFoundException {
        Card card = (Card) gson.fromJson(cardJson, Class.forName(className));
        account.getCollection().add(copyWithNewId(card));
        Utils.getShop().add(card);
        return "card added successfully";
    }


    private Card copyWithNewId(Card card) {
        String id = generateNewId(card);
        try {
            Card copiedCard = card.clone();
            copiedCard.setId(id);
            copiedCard.setAccountName(account.getUserName());
            return copiedCard;
        } catch (CloneNotSupportedException ignored) {
        }
        return null;
    }

    private Item copyWithNewId(UsableItem usableItem) {
        String id = generateNewId(usableItem);
        try {
            UsableItem copiedItem = (UsableItem) usableItem.clone();
            copiedItem.setId(id);
            copiedItem.setAccountName(account.getUserName());
            return copiedItem;
        } catch (CloneNotSupportedException ignored) {
        }
        return null;
    }


    private String generateNewId(Card newCard) {
        int index = 1;
        index += account.getCollection().getCards().stream()
                .filter(card -> card.nameEquals(newCard.getName()))
                .count();
        return account.getUserName() + "_" + newCard.getName() + "_" + index;
    }

    private String generateNewId(Item newItem) {
        int index = 1;
        index += account.getCollection().getUsableItems().stream()
                .filter(card -> card.nameEquals(newItem.getName()))
                .count();
        return account.getUserName() + "_" + newItem.getName() + "_" + index;
    }

    public Object buyAndReturn(String objectJson, String className) throws ClassNotFoundException {
        Object object = gson.fromJson(objectJson, Class.forName(className));
        if (object instanceof Card) {
            return buyAndReturnCard((Card) object);
        } else if (object instanceof UsableItem) {
            return buyAndReturnUsableItem((UsableItem) object);
        }
        return null;
    }

    private Card buyAndReturnCard(Card card) {
        account.decreaseBudget(card.getPrice());
        Card newCard = copyWithNewId(card);
        account.getCollection().add(newCard);
        Card c = (Card) getShop().getObjectByName(card.getName());
        c.decreaseCount();
        if (c.getCount() == 0) {
            Utils.getShop().remove(c);
        }
        return newCard;
    }

    private Item buyAndReturnUsableItem(UsableItem usableItem) {
        account.decreaseBudget(usableItem.getPrice());
        Item newUsableItem = copyWithNewId(usableItem);
        account.getCollection().add(newUsableItem);
        UsableItem item = (UsableItem) getShop().getObjectByName(usableItem.getName());
        item.decreaseCount();
        if (item.getCount() == 0) {
            Utils.getShop().remove(item);
        }
        return newUsableItem;
    }

    public String sellGUI(String objectJson, String className) throws ClassNotFoundException {
        Object object = gson.fromJson(objectJson, Class.forName(className));
        if (object instanceof Card) {
            sell((Card) object);
        } else if (object instanceof UsableItem) {
            sell((UsableItem) object);
        }
        return "sell successful";
    }


    private void sell(Card card) {
        account.getCollection().remove(card);
        account.getDecks().forEach(deck -> deck.remove(card));
        account.increaseBudget(card.getPrice());
        try {
            ((Card) getShop().getObjectByName(card.getName())).increaseCount();
        } catch (GameException e) {
            e.printStackTrace();
            card.setCount(1);
            getShop().add(card);
        }
    }

    private void sell(UsableItem usableItem) {
        account.getCollection().remove(usableItem);
        account.getDecks().forEach(deck -> deck.remove(usableItem));
        account.increaseBudget(usableItem.getPrice());
        try {
            ((UsableItem) getShop().getObjectByName(usableItem.getName())).increaseCount();
        } catch (GameException e) {
            e.printStackTrace();
            usableItem.setCount(1);
            getShop().add(usableItem);
        }
    }


    private String save() throws IOException {
        Files.createDirectories(Paths.get("src/com/ap/duelyst/data"));
        FileWriter writer = new FileWriter("src/com/ap/duelyst/data/accounts.txt",
                false);
        writer.write(Utils.getGson().toJson(Utils.getAccounts()));
        writer.close();
        saveShop();
        return "accounts saved successfully";

    }

    private String saveShop() throws IOException {
        Files.createDirectories(Paths.get("src/com/ap/duelyst/data"));
        FileWriter writer = new FileWriter("src/com/ap/duelyst/data/shop.txt",
                false);
        writer.write(Utils.getGson().toJson(Utils.getShop()));
        writer.close();
        return "shop saved successfully";
    }

    private List<Deck> getAllDecks() {
        List<Deck> decks = account.getDecks();
        if (decks == null || decks.isEmpty()) {
            return new ArrayList<>();
        }
        return decks;
    }

    private Deck getMainDeck() {
        if (account.getMainDeck() == null) {
            throw new GameException("no main deck");
        }
        return account.getMainDeck();
    }

    private String setMainDeck(String deckString) {
        account.setMainDeck(gson.fromJson(deckString, Deck.class));
        return "Deck " + account.getMainDeck().getName() + " selected successfully";
    }

    public String createDeck(String name) {
        if (account.hasDeck(name)) {
            throw new GameException("You have a deck with this name!");
        } else {
            account.createDeck(name);
        }
        return "Deck created successfully";
    }

    public String addToDeck(String objectJson, String className, String deckName) throws ClassNotFoundException {
        Object object = gson.fromJson(objectJson, Class.forName(className));
        account.getDeck(deckName).add(object);
        if (account.getDeck(deckName).equals(account.getMainDeck())) {
            account.getMainDeck().add(object);
        }
        return "card/item added successfully";
    }

    public String removeFromDeck(String id, String deckName) throws ClassNotFoundException {
        account.getDeck(deckName).removeById(id);
        if (account.getDeck(deckName).equals(account.getMainDeck())) {
            account.getMainDeck().removeById(id);
        }
        return "card/item removed successfully";
    }


    public String importDeck(String name) {
        try {
            Files.createDirectories(Paths.get("src/com/ap/duelyst/data"));
            FileReader reader =
                    new FileReader("src/com/ap/duelyst/data/" + name + ".txt");
            int c = reader.read();
            StringBuilder deckString = new StringBuilder();
            while (c != -1) {
                deckString.append((char) c);
                c = reader.read();
            }
            Deck deck = Utils.getGson().fromJson(deckString.toString(),
                    new TypeToken<Deck>() {
                    }.getType());
            if (account.hasDeck(deck.getName())) {
                throw new GameException("You already have that deck!");
            } else {
                for (Card card : deck.getCards()) {
                    if (!account.getCollection().hasCardById(card.getId())) {
                        throw new GameException("you dont have the cards in your " +
                                "collection");
                    }
                }
                for (Item item : deck.getItems()) {
                    if (!account.getCollection().hasUsableItemById(item.getId())) {
                        throw new GameException("you dont have the items in your " +
                                "collection");
                    }
                }
            }
            account.getDecks().add(deck);
            return "deck imported successfully";
        } catch (IOException e) {
            e.printStackTrace();
            throw new GameException("deck not found");
        }
    }

    public String exportDeck(String deckName) {
        try {
            Deck deck = account.getDeck(deckName);
            Files.createDirectories(Paths.get("src/com/ap/duelyst/data"));
            FileWriter writer = new FileWriter(
                    "src/com/ap/duelyst/data/" + deckName + ".txt",
                    false);
            writer.write(Utils.getGson().toJson(deck));
            writer.close();
            return "deck exported successfully";
        } catch (IOException e) {
            e.printStackTrace();
            return "failed to export deck";
        }
    }


    public Deck createDeck(Double deckNumber) throws CloneNotSupportedException {

        List<Card> cards = new ArrayList<>();
        Item item = null;

        switch (deckNumber.intValue()) {
            case 1:
                addCard(cards, "white-beast", "persian-archer", "transoxianain-spear-man",
                        "transoxianain-trench-raider", "transoxianain-trench-raider",
                        "black-beast", "one-eyed-giant"
                        , "poisonous-snake", "gigantic-snake", "White-wolf", "high" +
                                "-witch", "nane-sarma", "siavash",
                        "beast-arjang", "total-disarm", "lightning-bolt", "all-disarm",
                        "all-poison", "dispel",
                        "sacrifice", "kings-guard");
                item = getItem("wisdom-throne");
                break;
            case 2:
                addCard(cards, "zahhak", "persian-swordsman", "persian-spear-man",
                        "persian-hero",
                        "transoxianain-sling-man", "transoxianain-prince", "giant-rock" +
                                "-thrower", "giant-rock-thrower",
                        "fire-dragon", "panther", "ghost", "giv", "iraj", "giant-king",
                        "aria-dispel", "empower",
                        "god-strength", "poison-lake", "madness", "health-with-benefit"
                        , "kings-guard");
                item = getItem("soul-eater");
                break;
            case 3:
                addCard(cards, "arash", "persian-commander", "transoxianain-archer",
                        "transoxianain-spy",
                        "giant-rock-thrower", "cavalry-beast", "cavalry-beast", "fierce" +
                                "-lion", "wolf", "witch", "wild" +
                                "-pig",
                        "piran", "bahman", "big-giant", "hellfire", "all-disarm",
                        "dispel", "power-up", "all-power",
                        "all-attack", "weakening");
                item = getItem("terror-hood");
                break;
        }

        Deck deck = new Deck("AI_deck" + deckNumber);
        for (Card card : cards) {
            deck.add(card);
        }
        deck.add(item);
        return deck;
    }

    private void addCard(List<Card> cards, String... names) throws CloneNotSupportedException {
        for (String name : names) {
            Card card = ((Card) Utils.getShop().getObjectByName(name)).clone();
            String id = generateId(cards, card);
            card.setId(id);
            card.setAccountName("AI");
            cards.add(card);
        }
    }

    private Item getItem(String name) throws CloneNotSupportedException {
        Item item = ((Item) Utils.getShop().getObjectByName(name)).clone();
        item.setId("AI_Player_" + name + "_1");
        return item;
    }

    private String generateId(List<Card> cards, Card card) {
        int index = 1;
        index += cards.stream().filter(card1 -> card.nameEquals(card1.getName())).count();
        return "AI_Player_" + card.getName() + "_" + index;
    }

    private void createGame(String storyLevelString, Double flagNumber)
            throws CloneNotSupportedException {
        BattleMenu.StoryLevel storyLevel =
                BattleMenu.StoryLevel.valueOf(storyLevelString);
        BattleMenu.CustomGameMode mode =
                BattleMenu.CustomGameMode.getMode(storyLevel.value);
        Deck deck = createDeck(Double.valueOf(storyLevel.value));
        game = Game.createGame(account, null, mode, deck, flagNumber.intValue(),
                Integer.valueOf(storyLevel.value) * 500);
        setGameListener();
        game.startGame();
    }

    private void createGame(Double deckNumber, String modeString,
                            Double flagNumber) throws CloneNotSupportedException {
        Deck deck = createDeck(deckNumber);
        game = Game.createGame(account, null,
                BattleMenu.CustomGameMode.valueOf(modeString), deck,
                flagNumber.intValue(), 1000);
        setGameListener();
        game.startGame();
    }

    private void setGameListener() {
        game.setEvents(new GameEvents() {
            @Override
            public void nextRound(List<Hero> inGameCards) {

            }

            @Override
            public void gameEnded(String result) {

            }

            @Override
            public void insert(Card card, int x, int y) {

            }

            @Override
            public void move(Card card, int oldX, int oldY, int finalI, int finalJ) {

            }

            @Override
            public void attack(Card attacker, Card attacked) {

            }

            @Override
            public void specialPower(Card card, int finalI, int finalJ) {

            }

            @Override
            public void useCollectable(Item item) {

            }

            @Override
            public void startGame() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("resp", "game started");
                writer.println(jsonObject);
            }

        });
    }


    private List<Card> getInBoardCards() {
        return game.getInBoardCards().stream()
                .map(hero -> (Card) hero).collect(Collectors.toList());
    }

    private Hero getCardAt(int x, int y) {
        return game.getCardAt(x, y);
    }

    private List<List<Cell>> getBoard() {
        return game.getBoard();
    }

    private List<Player> getPlayers() {
        return game.getPlayers();
    }

    private List<int[]> getCellsInRange(Double x, Double y, Double range) {
        return game.getCellsInRange(x.intValue(), y.intValue(), range.intValue());
    }

    private List<int[]> getNeighbours(Double x, Double y) {
        return game.getNeighbours(x.intValue(), y.intValue());
    }

    private Player getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    private GraveYard getGraveYard() {
        return game.getGraveYard();
    }

    private boolean checkRoad(Double x1, Double y1, Double x2, Double y2) {
        return game.checkRoad(x1.intValue(), y1.intValue(), x2.intValue(), y2.intValue());
    }


    private void insert(String cardId, Double x, Double y) {
        Card card = game.getCard(cardId);
        game.insert(card, x.intValue(), y.intValue());
    }


    private void useSpecialPower(String cardId, Double x, Double y) {
        Card card = game.getCard(cardId);
        game.useSpecialPower(card, x.intValue(), y.intValue());
    }

    private void useCollectable(String id) {
        CollectableItem collectableItem = game.getCurrentPlayer().getCollectableItems()
                .stream().filter(item -> item.idEquals(id))
                .findFirst().orElseThrow(() -> new GameException("no such collectable"));
        game.useCollectable(collectableItem);
    }

    private void attack(String attackerId, String attackedId) {
        Card card = game.getCard(attackerId);
        game.attack(card, attackedId, true);
    }

    private void move(String cardId, Double x, Double y) {
        Card card = game.getCard(cardId);
        game.move(card, x.intValue(), y.intValue());
    }

    private void endTurn() {
        game.endTurn();
    }
}
