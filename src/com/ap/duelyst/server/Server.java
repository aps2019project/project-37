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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Server extends Application{
    private FXMLLoader shopServerLoader;
    private Scene shopServerScene;
    public static ShopServerController shopServerController;
    private static ArrayList<Account> onlineAccounts = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) throws Exception {
        shopServerLoader =
                new FXMLLoader(getClass().getResource("shopServer.fxml"));
        shopServerScene = makeShopServerScene();
        primaryStage.setScene(shopServerScene);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        new GameServer().start();
        new ChatRoomServer().start();
        launch(args);
    }

    public static ArrayList<Account> getOnlineAccounts() {
        return onlineAccounts;
    }

    private Scene makeShopServerScene() throws IOException {
        Parent root = shopServerLoader.load();
        shopServerController = shopServerLoader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("com/ap/duelyst/server/ShopServer.css");
        scene.setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
        return scene;
    }

}


class GameServer extends Thread{
    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ChatRoomServer extends Thread{
    ServerSocket serverSocket;
    static ArrayList<DataOutputStream> clientWriters;
    ChatRoomServer() throws IOException {
        serverSocket = new ServerSocket(8200);
        clientWriters = new ArrayList<>();
    }
    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
                clientWriters.add(writer);
                new ChatRoomHandler(socket, writer).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ChatRoomHandler extends Thread{
    Socket socket;
    DataOutputStream clientWriter;
    ChatRoomHandler(Socket socket, DataOutputStream clientWriter){
        this.clientWriter = clientWriter;
        this.socket = socket;
        System.out.println("new chatroom added");
    }

    @Override
    public void run() {
        DataInputStream reader;
        try {

            reader = new DataInputStream(socket.getInputStream());
            String message;
            while (true) {
                message = reader.readUTF();
                System.out.println(message);
                for (DataOutputStream clientWriter : ChatRoomServer.clientWriters) {
                    if (!clientWriter.equals(this.clientWriter)) {
                        clientWriter.writeUTF(message);
                    }
                }
            }
        } catch (IOException e) {
            ChatRoomServer.clientWriters.remove(clientWriter);
            e.printStackTrace();
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
            }
            catch (NoSuchElementException ex){
                Server.getOnlineAccounts().remove(account);
                Server.shopServerController.updateUsersList();
            }
            catch (NoSuchMethodException | IllegalAccessException
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
    private String getUserName(){
        return account.getUserName();
    }
    private void loginGUI(String userName, String password) {
        if (!Utils.hasAccount(userName)) {
            throw new GameException("No account with this username");
        }
        if (account != null) {
            throw new GameException("You are already logged in");
        }
        for (Account onlineAccount : Server.getOnlineAccounts()) {
            if(onlineAccount.getUserName().equals(userName)){
                throw new GameException("Other client with this user name is logged in");
            }
        }
        Account account = Utils.getAccountByUsername(userName);
        if (account.getPassword().equals(password)) {
            this.account = account;
            Server.getOnlineAccounts().add(account);
            Platform.runLater(() -> Server.shopServerController.updateUsersList());
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
            Server.getOnlineAccounts().remove(account);
            Platform.runLater(() -> Server.shopServerController.updateUsersList());
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
        Platform.runLater(() -> Server.shopServerController.updateShopTable());
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
        Platform.runLater(() -> Server.shopServerController.updateShopTable());
        return newUsableItem;
    }

    public String sellGUI(String objectJson, String className) throws ClassNotFoundException {
        Object object = gson.fromJson(objectJson, Class.forName(className));
        if (object instanceof Card) {
            sell((Card) object);
        } else if (object instanceof UsableItem) {
            sell((UsableItem) object);
        }
        Platform.runLater(() -> Server.shopServerController.updateShopTable());
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
            public void nextRound() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "nextRound");
                writer.println(gson.toJson(jsonObject));
            }

            @Override
            public void gameEnded(String result) {

            }

            @Override
            public void insert(String cardId, int x, int y) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "insert");
                jsonObject.addProperty("cardId", cardId);
                jsonObject.addProperty("x", x);
                jsonObject.addProperty("y", y);
                writer.println(gson.toJson(jsonObject));
            }

            @Override
            public void move(String id, int oldX, int oldY, int finalI, int finalJ) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "move");
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("oldX", oldX);
                jsonObject.addProperty("oldY", oldY);
                jsonObject.addProperty("finalI", finalI);
                jsonObject.addProperty("finalJ", finalJ);
                writer.println(gson.toJson(jsonObject));
            }

            @Override
            public void attack(String attackerId, String attackedId) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "attack");
                jsonObject.addProperty("attackerId", attackerId);
                jsonObject.addProperty("attackedId", attackedId);
                writer.println(gson.toJson(jsonObject));
            }

            @Override
            public void specialPower(String cardId, int finalI, int finalJ) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "specialPower");
                jsonObject.addProperty("cardId", cardId);
                jsonObject.addProperty("finalI", finalI);
                jsonObject.addProperty("finalJ", finalJ);
                writer.println(gson.toJson(jsonObject));

            }

            @Override
            public void useCollectable(String itemId) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "useCollectable");
                writer.println(gson.toJson(jsonObject));
            }


            @Override
            public void startGame() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("resp", "game started");
                writer.println(gson.toJson(jsonObject));
            }

            @Override
            public void error(String message) {

            }

        });
    }

    private List<List<Cell>> getBoard() {
        return game.getBoard();
    }

    private List<Player> getPlayers() {
        return game.getPlayers();
    }

    private Object[] getCellsInRange(Double x, Double y, Double range) {
        return game.getCellsInRange(x.intValue(), y.intValue(), range.intValue()).toArray();
    }

    private Object[] getNeighbours(Double x, Double y) {
        return game.getNeighbours(x.intValue(), y.intValue()).toArray();
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
