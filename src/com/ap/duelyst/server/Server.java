package com.ap.duelyst.server;

import com.ap.duelyst.Command;
import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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

    private List<Account> getAllAccounts(){
        return Utils.getAccounts();
    }
}