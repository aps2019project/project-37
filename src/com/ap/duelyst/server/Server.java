package com.ap.duelyst.server;

import com.ap.duelyst.Command;
import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Collection;
import com.ap.duelyst.model.Shop;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.items.Item;
import com.ap.duelyst.model.items.UsableItem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    writer.println(jsonObject);
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


    public String save() throws IOException {
        Files.createDirectories(Paths.get("src/com/ap/duelyst/data"));
        FileWriter writer = new FileWriter("src/com/ap/duelyst/data/accounts.txt",
                false);
        writer.write(Utils.getGson().toJson(Utils.getAccounts()));
        writer.close();
        saveShop();
        return "accounts saved successfully";

    }

    public String saveShop() throws IOException {
        Files.createDirectories(Paths.get("src/com/ap/duelyst/data"));
        FileWriter writer = new FileWriter("src/com/ap/duelyst/data/shop.txt",
                false);
        writer.write(Utils.getGson().toJson(Utils.getShop()));
        writer.close();
        return "shop saved successfully";
    }


}
