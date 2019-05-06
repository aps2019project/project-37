package controller.menu.ingame;

import controller.Controller;
import controller.GameException;
import controller.menu.Menu;
import model.game.GraveYard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraveyardMenu extends Menu {
    enum GraveyardType{
        SHOW_CARD_INFO(0),
        SHOW_CARDS(1),
        SHOW_MENU(2),
        EXIT(3);

        int commandIndex;

        GraveyardType(int commandIndex) {
            this.commandIndex = commandIndex;
        }

        public static GraveyardType getCommandType(int commandIndex) {
            for (GraveyardType commandType : values()) {
                if (commandType.commandIndex == commandIndex) {
                    return commandType;
                }
            }
            return null;
        }
    }

    private String[] matchers = {
            "^game info (\\w+_){2}\\d+$",
            "^show cards$",
            "^show menu$",
            "^exit$"
    };

    private GraveyardType type;
    private GraveYard graveYard;

    public GraveyardMenu(Controller controller) {
        super(controller);
    }

    public void setGraveYard(GraveYard graveYard) {
        this.graveYard = graveYard;
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        match(command);
        switch (type){
            case SHOW_CARD_INFO:
                showCardInfoInGraveYard(extractLastWord(command));
                break;
            case SHOW_CARDS:
                showAllCardsInGraveYard();
                break;
            case SHOW_MENU:
                showMessage(menuHelp());
                break;
            case EXIT:
                return getParentMenu();
        }
        return this;
    }

    private String menuHelp() {
        return "\nIn Grave Yard Menu\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- show info [card id]\n" +
                "2- show cards\n" +
                "3- show menu\n" +
                "4- exit\n";
    }

    private void match(String command) {
        for (int i = 0; i < matchers.length; i++) {
            String matcher = matchers[i];
            if (command.matches(matcher)) {
                type = GraveyardType.getCommandType(i);
                return;
            }
        }
        throw new GameException("invalid command");
    }

    private void showCardInfoInGraveYard(String cardId){
        //search among all cards in graveYard and get it
        //show its info
    }

    private void showAllCardsInGraveYard(){

    }

    private String extractLastWord(String command) {
        String[] strings = command.split(" ");
        return strings[strings.length - 1];
    }
}
