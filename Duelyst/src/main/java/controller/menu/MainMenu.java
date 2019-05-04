package controller.menu;

import controller.Controller;
import controller.GameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum CommandTypeMainMenu {
    COLLECTION(0),
    SHOP(1),
    BATTLE(2),
    EXIT(3),
    HELP(4);

    int commandIndex;

    CommandTypeMainMenu(int commandIndex) {
        this.commandIndex = commandIndex;
    }

    public static CommandTypeMainMenu getCommandType(int commandIndex) {
        for (CommandTypeMainMenu commandType : values()) {
            if (commandType.commandIndex == commandIndex) {
                return commandType;
            }
        }
        return null;
    }
}

public class MainMenu extends Menu {
    private CollectionMenu collectionMenu;
    private BattleMenu battleMenu;
    private ShopMenu shopMenu;
    private static final int NUMBER_OF_COMMANDS = 5;
    private Pattern[] commandPatterns = new Pattern[NUMBER_OF_COMMANDS];

    MainMenu(Controller controller) {
        super(controller);
        collectionMenu = new CollectionMenu(controller);
        battleMenu = new BattleMenu(controller);
        shopMenu = new ShopMenu(controller);
        collectionMenu.setParentMenu(this);
        battleMenu.setParentMenu(this);
        shopMenu.setParentMenu(this);
        initCommandPatterns();
    }

    private void initCommandPatterns() {
        String[] commandRegexes = new String[NUMBER_OF_COMMANDS];
        commandRegexes[0] = "^enter collection$";
        commandRegexes[1] = "^enter shop$";
        commandRegexes[2] = "^enter battle$";
        commandRegexes[3] = "^exit$";
        commandRegexes[4] = "^help$";
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            this.commandPatterns[i] = Pattern.compile(commandRegexes[i]);
        }
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        CommandTypeMainMenu commandType = getCommandType(command);
        switch (commandType) {
            case COLLECTION:
                showMessage("\nYou've entered CollectionMenu\n");
                return collectionMenu;
            case SHOP:
                showMessage("\nYou've entered ShopMenu\n");
                return shopMenu;
            case BATTLE:
                showMessage("\nYou've entered BattleMenu\n");
                return battleMenu;
            case EXIT:
                return getParentMenu();
            case HELP:
                printListOfCommands();
                break;
        }
        return this;
    }

    private CommandTypeMainMenu getCommandType(String command) {
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            Matcher matcher = commandPatterns[i].matcher(command);
            if (matcher.find()) {
                return CommandTypeMainMenu.getCommandType(i);
            }
        }
        throw new GameException("Invalid command!");
    }

    private void printListOfCommands() {
        getController().showMessage(getListOfCommands());
    }

    private String getListOfCommands() {
        return "\nMain Menu\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- enter collection\n" +
                "2- enter shop\n" +
                "3- enter battle\n" +
                "4- exit\n" +
                "5- help\n";
    }
}
