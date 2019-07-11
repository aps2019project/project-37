package com.ap.duelyst.server;

import com.ap.duelyst.Command;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.BattleMenu;
import com.ap.duelyst.model.*;
import com.ap.duelyst.model.Collection;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Minion;
import com.ap.duelyst.model.cards.Spell;
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
import io.joshworks.restclient.http.HttpResponse;
import io.joshworks.restclient.http.Unirest;
import javafx.application.Application;
import javafx.application.Platform;
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
import java.util.stream.Collectors;

public class Server extends Application {
    private FXMLLoader shopServerLoader;
    static ShopServerController shopServerController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        shopServerLoader =
                new FXMLLoader(getClass().getResource("shopServer.fxml"));
        Scene shopServerScene = makeShopServerScene();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<String> names = Thread.getAllStackTraces().keySet().stream()
                        .filter(thread -> thread instanceof ClientHandler)
                        .filter(thread -> ((ClientHandler) thread).getAccount() != null)
                        .map(thread -> ((ClientHandler) thread).getAccount().getUserName())
                        .collect(Collectors.toList());
                Platform.runLater(() -> shopServerController.updateUsersList(names));
            }
        }, 1000, 1000);
        primaryStage.setScene(shopServerScene);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        new GameServer().start();
        new ChatRoomServer().start();
        launch(args);
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


class GameServer extends Thread {
    @Override
    public void run() {
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-jar",
                    "/home/amin/Desktop/projects/Duelyst/src/com/ap/duelyst/server" +
                            "/database/gs-rest-service-0.1.0.jar");
            pb.directory(new File("/home/amin/Desktop/projects/Duelyst/src/com/ap" +
                    "/duelyst/server/database"));
            Process p = pb.start();
            Scanner scanner = new Scanner(p.getInputStream());
            new Thread(() -> {
                while (true) {
                    String s = scanner.nextLine();
                    System.out.println(s);
                    if (s.contains("Started Application")) {
                        Utils.loadData();
                        Platform.runLater(() -> Server.shopServerController.updateShopTable());
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(5555);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ChatRoomServer extends Thread {
    private ServerSocket serverSocket;

    ChatRoomServer() throws IOException {
        serverSocket = new ServerSocket(8200);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                new ChatRoomHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ChatRoomHandler extends Thread {
    private Socket socket;
    private DataOutputStream clientWriter;

    ChatRoomHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.clientWriter = new DataOutputStream(socket.getOutputStream());
        System.out.println("new chat room added");
    }

    @Override
    public void run() {
        DataInputStream reader;
        try {
            reader = new DataInputStream(socket.getInputStream());
            String message;
            while (true) {
                message = reader.readUTF();
                String finalMessage = message;
                Thread.getAllStackTraces().keySet().stream()
                        .filter(thread -> thread instanceof ChatRoomHandler)
                        .filter(thread -> thread != this)
                        .forEach(thread -> {
                            try {
                                ((ChatRoomHandler) thread).clientWriter.writeUTF(finalMessage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

@SuppressWarnings("Duplicates")
class ClientHandler extends Thread {
    private String token = null;
    private Account account;
    private Gson gson;
    private Scanner reader;
    private PrintWriter writer;
    private Game game;
    private boolean readyToPlay;
    private BattleMenu.CustomGameMode mode = BattleMenu.CustomGameMode.UNKNOWN;
    private static final Object object = new Object();
    private static final Object object1 = new Object();
    private static final Object object2 = new Object();
    private static final Object object3 = new Object();
    private String s;

    ClientHandler(Socket socket) {
        this.gson = Utils.getGson();
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("new client added");
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public void run() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<Game> games = Thread.getAllStackTraces().keySet().stream()
                        .filter(thread -> thread instanceof ClientHandler)
                        .map(thread -> (ClientHandler) thread)
                        .filter(thread -> thread.game != null)
                        .map(clientHandler -> clientHandler.game)
                        .collect(Collectors.toList());
                for (Game game : games) {
                    List<String> names =
                            game.getPlayers().stream().map(Player::getAccountName)
                                    .collect(Collectors.toList());
                    if (names.contains("AI")) {
                        continue;
                    }
                    List<ClientHandler> clientHandlers =
                            Thread.getAllStackTraces().keySet().stream()
                                    .filter(thread -> thread instanceof ClientHandler)
                                    .map(thread -> (ClientHandler) thread)
                                    .filter(thread -> thread.account != null)
                                    .filter(clientHandler -> names.contains(clientHandler.account.getUserName()))
                                    .collect(Collectors.toList());
                    if (clientHandlers.size() < 2) {
                        ClientHandler handler = clientHandlers.get(0);
                        handler.game = null;
                        handler.mode = null;
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("name", "gameEnded");
                        jsonObject.addProperty("result", "opponent disconnected");
                        handler.writer.println(gson.toJson(jsonObject));
                    }
                }
            }
        }, 0, 1000);
        Command command;
        Method method;
        String input;
        while (true) {
            try {
                try {
                    input = reader.nextLine();
                } catch (IndexOutOfBoundsException e) {
                    if (s == null) {
                        synchronized (object3) {
                            object3.wait();
                        }
                    }
                    input = s;
                    s = null;
                }
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
                    Object o = method.invoke(this, command.getParameters());
                    if (o != null) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("resp", gson.toJson(o));
                        writer.println(gson.toJson(jsonObject));
                    }
                } else {
                    throw new GameException("authentication failed");
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | GameException | InterruptedException e) {
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

    private String getUserName() {
        return account.getUserName();
    }

    private void loginGUI(String userName, String password) {
        if (!Utils.hasAccount(userName)) {
            throw new GameException("No account with this username");
        }
        if (account != null) {
            throw new GameException("You are already logged in");
        }
        Thread.getAllStackTraces().keySet().stream()
                .filter(thread -> thread instanceof ClientHandler)
                .filter(thread -> {
                    Account account = ((ClientHandler) thread).account;
                    return account != null && account.userNameEquals(userName);
                }).findFirst().ifPresent(thread -> {
            throw new GameException("Other client with this user name is logged in");
        });
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
        saveToDB("accounts", "accountsData", Utils.getGson().toJson(Utils.getAccounts()));
        saveShop();
        return "accounts saved successfully";

    }

    private String saveShop() throws IOException {
        Files.createDirectories(Paths.get("src/com/ap/duelyst/data"));
        FileWriter writer = new FileWriter("src/com/ap/duelyst/data/shop.txt",
                false);
        writer.write(Utils.getGson().toJson(Utils.getShop()));
        writer.close();
        saveToDB("shop", "shopData", Utils.getGson().toJson(Utils.getShop()));
        return "shop saved successfully";
    }

    private void saveToDB(String name, String key, String value) {
        HttpResponse<String> response = Unirest.post("http://localhost:8080/init_DB")
                .field("name", name)
                .asString();
        if (response.isSuccessful()) {
            System.out.println(response.getBody());
        } else {
            System.out.println("error: " + response.getBody());
        }
        response = Unirest.post("http://localhost:8080/del_from_DB")
                .field("name", name)
                .field("key", key)
                .field("value", value)
                .asString();
        if (response.isSuccessful()) {
            System.out.println(response.getBody());
        } else {
            System.out.println("error: " + response.getBody());
        }
        response = Unirest.post("http://localhost:8080/put")
                .field("name", name)
                .field("key", key)
                .field("value", value)
                .asString();
        if (response.isSuccessful()) {
            System.out.println(response.getBody());
        } else {
            System.out.println("error: " + response.getBody());
        }
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
        /*try {
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
        }*/
        HttpResponse<String> response = Unirest.post("http://localhost:8080/get")
                .field("name", "decks")
                .field("key", name)
                .asString();
        if (response.isSuccessful()) {
            Deck deck = Utils.getGson().fromJson(response.getBody(),
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
        } else {
            throw new GameException("deck not found");
        }
    }

    public String exportDeck(String deckName) {
        /*try {
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
        }*/
        Deck deck = account.getDeck(deckName);
        saveToDB("decks", deckName, Utils.getGson().toJson(deck));
        return "deck exported successfully";
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
            Card card = ((Card) Utils.getUnmodifiedShop().getObjectByName(name)).clone();
            String id = generateId(cards, card);
            card.setId(id);
            card.setAccountName("AI");
            cards.add(card);
        }
    }

    private Item getItem(String name) throws CloneNotSupportedException {
        Item item = ((Item) Utils.getUnmodifiedShop().getObjectByName(name)).clone();
        item.setId("AI_Player_" + name + "_1");
        return item;
    }

    private String generateId(List<Card> cards, Card card) {
        int index = 1;
        index += cards.stream().filter(card1 -> card.nameEquals(card1.getName())).count();
        return "AI_Player_" + card.getName() + "_" + index;
    }

    private String createGame(String storyLevelString, Double flagNumber)
            throws CloneNotSupportedException {
        BattleMenu.StoryLevel storyLevel =
                BattleMenu.StoryLevel.valueOf(storyLevelString);
        BattleMenu.CustomGameMode mode =
                BattleMenu.CustomGameMode.getMode(storyLevel.value);
        Deck deck = createDeck(Double.valueOf(storyLevel.value));
        game = Game.createGame(account, null, mode, deck, flagNumber.intValue(),
                Integer.valueOf(storyLevel.value) * 500);
        setGameListener(null);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                game.startGame();
            }
        }, 200);
        return "game created";
    }

    private String searchForOpponent(String modeString) throws InterruptedException {
        readyToPlay = true;
        mode = BattleMenu.CustomGameMode.valueOf(modeString);
        switch (mode) {

            case KILL_ENEMY_HERO:
                return lock(object);
            case KEEP_FLAG_8_ROUNDS:
                return lock(object1);
            case COLLECT_HALF_FLAGS:
                return lock(object2);
            default:
                throw new GameException("error with mode");
        }
    }

    private String lock(Object object) throws InterruptedException {
        synchronized (object) {
            List<ClientHandler> clientHandlers =
                    Thread.getAllStackTraces().keySet().stream()
                            .filter(thread -> thread instanceof ClientHandler)
                            .map(thread -> (ClientHandler) thread)
                            .filter(thread -> thread.account != null)
                            .filter(thread -> thread.game == null)
                            .filter(thread -> thread.readyToPlay)
                            .filter(thread -> thread.mode == mode)
                            .collect(Collectors.toList());
            System.out.println(clientHandlers.size());
            if (clientHandlers.size() < 2) {
                System.out.println("waiting: " + account.getUserName());
                new Thread(() -> {
                    String s = reader.nextLine();
                    if (s.equals("cancel")) {
                        synchronized (object) {
                            object.notify();
                        }
                    } else {
                        this.s = s;
                        synchronized (object3) {
                            object3.notify();
                        }
                    }
                }).start();
                object.wait();
            }
            object.notify();
            clientHandlers.stream().filter(clientHandler -> clientHandler != this)
                    .findFirst().ifPresent(handler -> {
                Game game = Game.createGame(handler.account, this.account, mode, null,
                        5, 1000);
                this.game = game;
                handler.game = game;
                setGameListener(handler);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        game.startGame();
                    }
                }, 200);
            });
            readyToPlay = false;
            System.out.println("resumed: " + account.getUserName());
            return "game created";
        }
    }

    private String createGame(Double deckNumber, String modeString,
                              Double flagNumber) throws CloneNotSupportedException {
        Deck deck = createDeck(deckNumber);
        game = Game.createGame(account, null,
                BattleMenu.CustomGameMode.valueOf(modeString), deck,
                flagNumber.intValue(), 1000);
        setGameListener(null);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                game.startGame();
            }
        }, 200);
        return "game created";
    }

    private void endGame() {
        game.endGame(account, false);
    }

    private void cheat() {
        game.endGame(account, true);
    }

    private void endGame(Account account) {
        game.endGame(account, false);
    }

    private void setGameListener(ClientHandler handler) {
        game.setEvents(new GameEvents() {
            @Override
            public void nextRound() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "nextRound");
                writer.println(gson.toJson(jsonObject));
                if (handler != null) {
                    handler.writer.println(gson.toJson(jsonObject));
                }
            }

            @Override
            public void gameEnded(String result) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "gameEnded");
                jsonObject.addProperty("result", result);
                writer.println(gson.toJson(jsonObject));
                if (handler != null) {
                    handler.writer.println(gson.toJson(jsonObject));
                }
                game = null;
                mode = null;
                if (handler != null) {
                    handler.game = null;
                    handler.mode = null;
                }
            }

            @Override
            public void insert(String cardId, int x, int y) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "insert");
                jsonObject.addProperty("cardId", cardId);
                jsonObject.addProperty("x", x);
                jsonObject.addProperty("y", y);
                writer.println(gson.toJson(jsonObject));
                if (handler != null) {
                    handler.writer.println(gson.toJson(jsonObject));
                }
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
                if (handler != null) {
                    handler.writer.println(gson.toJson(jsonObject));
                }
            }

            @Override
            public void attack(String attackerId, String attackedId) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "attack");
                jsonObject.addProperty("attackerId", attackerId);
                jsonObject.addProperty("attackedId", attackedId);
                writer.println(gson.toJson(jsonObject));
                if (handler != null) {
                    handler.writer.println(gson.toJson(jsonObject));
                }
            }

            @Override
            public void specialPower(String cardId, int finalI, int finalJ) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "specialPower");
                jsonObject.addProperty("cardId", cardId);
                jsonObject.addProperty("finalI", finalI);
                jsonObject.addProperty("finalJ", finalJ);
                writer.println(gson.toJson(jsonObject));
                if (handler != null) {
                    handler.writer.println(gson.toJson(jsonObject));
                }

            }

            @Override
            public void useCollectable(String itemId) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", "useCollectable");
                writer.println(gson.toJson(jsonObject));
                if (handler != null) {
                    handler.writer.println(gson.toJson(jsonObject));
                }
            }


            @Override
            public void startGame() {
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
