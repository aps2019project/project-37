package controller.menu;

import controller.Controller;
import controller.GameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum CommandTypeShopMenu {
    EXIT(0),
    SHOW_COLLECTION(1),
    SEARCH(2),
    SEARCH_COLLECTION(3),
    BUY(4),
    SELL(5),
    SHOW(6),
    HELP(7);


    int commandIndex;

    CommandTypeShopMenu(int commandIndex) {
        this.commandIndex = commandIndex;
    }

    public static CommandTypeShopMenu getCommandType(int commandIndex) {
        for (CommandTypeShopMenu commandType : values()) {
            if (commandType.commandIndex == commandIndex) {
                return commandType;
            }
        }
        return null;
    }
}

public class ShopMenu extends Menu {
    private static final int NUMBER_OF_COMMANDS = 8;
    private Pattern[] commandPatterns = new Pattern[NUMBER_OF_COMMANDS];

    ShopMenu(Controller controller) {
        super(controller);
        initCommandPatterns();
    }

    private void initCommandPatterns() {
        String[] commandRegexes = new String[NUMBER_OF_COMMANDS];
        commandRegexes[0] = "^exit$";
        commandRegexes[1] = "^show collection$";
        commandRegexes[2] = "^search (\\w+-*)+$";
        commandRegexes[3] = "^search collection (\\w+-*)+$";
        commandRegexes[4] = "^buy (\\w+-*)+$";
        commandRegexes[5] = "^sell (\\w+_){2}\\d+$";
        commandRegexes[6] = "^show$";
        commandRegexes[7] = "^help$";
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            this.commandPatterns[i] = Pattern.compile(commandRegexes[i]);
        }
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        CommandTypeShopMenu commandType = getCommandType(command);
        switch (commandType) {
            case EXIT:
                showMessage("\nYou've entered " + getParentMenu().getClass().getSimpleName() + "\n");
                return getParentMenu();
            case SHOW_COLLECTION:
                callShowCollectionFromController();
                break;
            case SEARCH: {
                String name = extractLastWord(command);
                callSearchInShopFromController(name);
                break;
            }
            case SEARCH_COLLECTION: {
                String name = extractLastWord(command);
                callSearchInCollectionFromController(name);
                break;
            }
            case BUY: {
                String id = extractLastWord(command);
                callBuyFromController(id);
                break;
            }
            case SELL: {
                String id = extractLastWord(command);
                callSellFromController(id);
                break;
            }
            case SHOW:
                callShowShopFromController();
                break;
            case HELP:
                printListOfCommands();
                break;
        }
        return this;
    }

    private CommandTypeShopMenu getCommandType(String command) {
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            Matcher matcher = commandPatterns[i].matcher(command);
            if (matcher.find()) {
                return CommandTypeShopMenu.getCommandType(i);
            }
        }
        throw new GameException("Invalid command!");
    }

    private void callShowCollectionFromController() {
        getController().showCollection();
    }

    private void callSearchInShopFromController(String name) {
        getController().searchInShop(name);
    }

    private void callSearchInCollectionFromController(String name) {
        getController().searchInCollection(name);
    }

    private void callBuyFromController(String name) {
        getController().buy(name);
    }

    private void callSellFromController(String id) {
        getController().sell(id);
    }

    private void callShowShopFromController() {
        getController().showShop();
    }

    private String extractLastWord(String command) {
        String[] strings = command.split(" ");
        return strings[strings.length - 1];
    }

    private void printListOfCommands() {
        getController().showMessage(getListOfCommands());
    }

    private String getListOfCommands() {
        return "\nShop Menu\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- exit\n" +
                "2- show collection\n" +
                "3- search [item name | card name]\n" +
                "4- search collection [item name | card name]\n" +
                "5- buy [item name | card name] \n" +
                "6- sell [item id | card id] \n" +
                "7- show \n" +
                "8- help \n";
    }
}
