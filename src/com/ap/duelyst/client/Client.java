package com.ap.duelyst.client;



import com.ap.duelyst.controller.GameException;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    static String token;
    static BufferedWriter writer;
    static BufferedReader reader;
    public static void main(String[] args) {
        try{
            Socket socket = new Socket("localhost",8000);
            DataInputStream is = new DataInputStream(socket.getInputStream());
            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
            writer = new BufferedWriter(new OutputStreamWriter(os));
            reader = new BufferedReader(new InputStreamReader(is));

            Command command = new Command("createAccountGUI","mobin","123");
            command.setToken("allow");
            Gson gson = new Gson();
            writer.write(gson.toJson(command));
            System.out.println("hi");
            writer.flush();

            command = new Command("loginGUI","mobin","123");
            command.setToken("allow");
            writer.write(gson.toJson(command));
            writer.flush();

            token = gson.fromJson(reader.readLine(),String.class);
            System.out.println(token);
        }catch (IOException ex){
            ex.printStackTrace();
        }catch (GameException ex){
            System.out.println(ex.getMessage());
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


