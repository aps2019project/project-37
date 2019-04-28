package controller;

public class GameException extends RuntimeException {
    public GameException(String errorMessage){
        super(errorMessage);
    }
}
