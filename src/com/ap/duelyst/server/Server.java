package com.ap.duelyst.server;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    private ServerSocket serverSocket;
    Server(){
        try {
            serverSocket = new ServerSocket(8000);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public static void main(String[] args) {
        Server server = new Server();
        while (true){
            try {
                Socket socket = server.getServerSocket().accept();
                new ClientHandler(socket).start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


class ClientHandler extends Thread{
    String token = null;
    String userName;
    Gson gson;
    Socket socket;
    Controller controller;
    BufferedReader reader;
    BufferedWriter writer;
    ClientHandler(Socket socket){
        this.socket = socket;
        this.gson = new Gson();
        try {
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            writer = new BufferedWriter(new OutputStreamWriter(os));
            reader = new BufferedReader(new InputStreamReader(is));
            controller = new Controller();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("new client added");
    }
    @Override
    public void run() {
        Class clazz = controller.getClass();
        Command command;
        Method method;
        String json ;
        while (true){
            try {
                System.out.println("hi");
                json = reader.readLine();
                System.out.println(json);
                command = gson.fromJson(json,Command.class);
                method = clazz.getMethod(command.getCommandName(),command.getParameterTypes());
                if(checkToken(command)) {
                    json = gson.toJson(method.invoke(controller, command.getParameters()));
                }
                if(command.getCommandName().equals("loginGUI")){
                    token = generateToken(controller.getCurrentAccount().getUserName());
                    json = gson.toJson(token);
                }
                writer.write(json);
                writer.flush();
            }
            catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
                e.printStackTrace();
            } catch (GameException e){
                try {
                    writer.write("N");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public String generateToken(String userName){
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = dateFormat.format(date);
        System.out.println(formattedDate + userName);
        return formattedDate + userName;
    }
    public boolean checkToken(Command command){
        if(command.getToken().equals("allow")){
            if(command.getCommandName().equals("login") | command.getCommandName().equals("createAccount")){
                return true;
            }else {
                return false;
            }
        }
        String extractedUserName ;
        String token = command.getToken();
        Pattern pattern = Pattern.compile("\\d+\\:\\d+\\:\\d+");
        Matcher matcher = pattern.matcher(token);
        if(matcher.find()) {
             extractedUserName = matcher.replaceFirst("");
        }else{
            return false;
        }
        if(extractedUserName.equals(this.userName)){
            return true;
        }else {
            return false;
        }
    }
}


class Command{
    private String commandName;
    private Object parameters[];
    private String token;
    Command(String commandName, Object... parameters){
        this.commandName = commandName;
        this.parameters = parameters;
    }

    public String getCommandName() {
        return commandName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public Class[] getParameterTypes() {
        Class parameterTypes[];
        parameterTypes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterTypes[i] = parameters[i].getClass();
        }
        return parameterTypes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}


