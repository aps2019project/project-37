package com.ap.duelyst;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Command {
    private String commandName;
    private Object[] parameters;
    private String token;

    public Command(String commandName, Object... parameters) {
        this.commandName = commandName;
        this.parameters = parameters;
        token = Main.token;
    }

    public String getCommandName() {
        return commandName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public Class[] getParameterTypes() {
        if(parameters == null){
            return null;
        }
        Class[] arr = new Class[parameters.length];
        return Stream.of(parameters).map(Object::getClass)
                .collect(Collectors.toList()).toArray(arr);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
