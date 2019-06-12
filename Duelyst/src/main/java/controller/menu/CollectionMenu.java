package controller.menu;

import controller.Controller;
import controller.GameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum CommandTypeCollectionMenu {
    EXIT(0),
    SHOW(1),
    SEARCH(2),
    SAVE(3),
    CREATE_DECK(4),
    DELETE_DECK(5),
    ADD(6),
    REMOVE(7),
    VALIDATE_DECK(8),
    SELECT_DECK(9),
    SHOW_ALL_DECK(10),
    SHOW_DECK(11),
    HELP(12);


    int commandIndex;

    CommandTypeCollectionMenu(int commandIndex) {
        this.commandIndex = commandIndex;
    }

    public static CommandTypeCollectionMenu getCommandType(int commandIndex) {
        for (CommandTypeCollectionMenu commandType : values()) {
            if (commandType.commandIndex == commandIndex) {
                return commandType;
            }
        }
        return null;
    }
}

public class CollectionMenu extends Menu {
    private static final int NUMBER_OF_COMMANDS = 13;
    private Pattern[] commandPatterns = new Pattern[NUMBER_OF_COMMANDS];

    CollectionMenu(Controller controller) {
        super(controller);
        initCommandPatterns();
    }

    private void initCommandPatterns() {
        String[] commandRegexes = new String[NUMBER_OF_COMMANDS];
        commandRegexes[0] = "^exit$";
        commandRegexes[1] = "^show$";
        commandRegexes[2] = "^search (\\w+-*)+$";
        commandRegexes[3] = "^save$";
        commandRegexes[4] = "^create deck \\w+$";
        commandRegexes[5] = "^delete deck \\w+$";
        commandRegexes[6] = "^add \\S+ to deck \\w+$";
        commandRegexes[7] = "^remove \\S+ from deck \\w+$";
        commandRegexes[8] = "^validate deck \\w+$";
        commandRegexes[9] = "^select deck \\w+$";
        commandRegexes[10] = "^show all decks$";
        commandRegexes[11] = "^show deck \\w+$";
        commandRegexes[12] = "^help$";
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            this.commandPatterns[i] = Pattern.compile(commandRegexes[i]);
        }
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        CommandTypeCollectionMenu commandType = getCommandType(command);
        switch (commandType) {
            case EXIT:
                showMessage("\nYou've entered " + getParentMenu().getClass().getSimpleName() + "\n");
                return getParentMenu();
            case SHOW:
                callShowCollectionFromController();
                break;
            case SEARCH: {
                String name = extractLastWord(command);
                callSearchInCollectionFromController(name);
                break;
            }
            case SAVE:
                callSaveFromController();
                break;
            case CREATE_DECK: {
                String name = extractLastWord(command);
                callCreateDeckFromController(name);
                break;
            }
            case DELETE_DECK: {
                String name = extractLastWord(command);
                callDeleteDeckFromController(name);
                break;
            }
            case ADD: {
                String[] strings = extractAddRemoveProperties(command);
                callAddToDeckFromController(strings[0], strings[1]);
                break;
            }
            case REMOVE: {
                String[] strings = extractAddRemoveProperties(command);
                getController().removeFromDeck(strings[0], strings[1]);
                break;
            }
            case VALIDATE_DECK: {
                String name = extractLastWord(command);
                getController().validateDeck(name);
                break;
            }
            case SELECT_DECK: {
                String name = extractLastWord(command);
                getController().setMainDeck(name);
                break;
            }
            case SHOW_ALL_DECK:
                getController().showAllDecksInfo();
                break;
            case SHOW_DECK: {
                String name = extractLastWord(command);
                getController().showDeckInfo(name);
                break;
            }
            case HELP:
                printListOfCommands();
                break;
        }
        return this;
    }

    private void callShowCollectionFromController() {
        getController().showCollection();
    }

    private void callSearchInCollectionFromController(String name) {
        getController().searchInCollection(name);
    }

    private void callSaveFromController() {
        getController().save();
    }

    private void callCreateDeckFromController(String name) {
        getController().createDeck(name);
    }

    private void callDeleteDeckFromController(String name) {
        getController().deleteDeck(name);
    }

    private void callAddToDeckFromController(String id, String name) {
        getController().addToDeck(id, name);
    }

    private CommandTypeCollectionMenu getCommandType(String command) {
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            Matcher matcher = commandPatterns[i].matcher(command);
            if (matcher.find()) {
                return CommandTypeCollectionMenu.getCommandType(i);
            }
        }
        throw new GameException("Invalid command!");
    }

    private String extractLastWord(String command) {
        String[] strings = command.split(" ");
        return strings[strings.length - 1];
    }

    private String[] extractAddRemoveProperties(String command) {
        String[] strings = command.split(" ");
        return new String[]{strings[1], strings[4]};
    }

    private void printListOfCommands() {
        getController().showMessage(getListOfCommands());
    }

    private String getListOfCommands() {
        return "\nCollection Menu\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- exit\n" +
                "2- show \n" +
                "3- search [item name | card name]\n" +
                "4- save\n" +
                "5- create deck [deck name] \n" +
                "6- delete deck [deck name]\n" +
                "7- add [card id | item id | hero id] to deck [deck name] \n" +
                "8- remove [card id | card id | hero id] from deck [deck name]\n" +
                "9- validate deck [deck name]\n" +
                "10- select deck [deck name]\n" +
                "11- show all decks\n" +
                "12- show deck [deck name]\n" +
                "13- help\n";
    }
}
