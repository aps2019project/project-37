package controller.menu.ingame;

import controller.Controller;
import controller.GameException;
import controller.menu.Menu;
import model.game.GraveYard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraveyardMenu extends Menu {

    enum GraveyardType{
        SHOW_INFO(0),
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

    public GraveyardMenu(Controller controller) {
        super(controller);
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        match(command);
        switch (type){
            case SHOW_INFO:
                break;
            case SHOW_CARDS:
                break;
            case SHOW_MENU:
                break;
            case EXIT:
                break;
        }
        return this;
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
}
