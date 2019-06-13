package com.ap.duelyst.controller.menu;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum CommandTypeLoginPage {
    CREATE_ACCOUNT(0),
    LOGIN(1),
    SHOW_LEAD_BOARD(2),
    SAVE(3),
    LOGOUT(4),
    HELP(5),
    EXIT(6);

    int commandIndex;

    CommandTypeLoginPage(int commandIndex) {
        this.commandIndex = commandIndex;
    }

    public static CommandTypeLoginPage getCommandType(int commandIndex) {
        for (CommandTypeLoginPage commandType : values()) {
            if (commandType.commandIndex == commandIndex) {
                return commandType;
            }
        }
        return null;
    }
}

public class LoginPage extends Menu {
    private static final int NUMBER_OF_COMMANDS = 7;
    private MainMenu mainMenu;
    private Pattern[] commandPatterns = new Pattern[NUMBER_OF_COMMANDS];

    public LoginPage(Controller controller) {
        super(controller);
        setParentMenu(null);
        mainMenu = new MainMenu(controller);
        mainMenu.setParentMenu(null);
        initCommandPatterns();
    }

    private void initCommandPatterns() {
        String[] commandRegexes = new String[NUMBER_OF_COMMANDS];
        commandRegexes[0] = "^create account \\w+$";
        commandRegexes[1] = "^login \\w+$";
        commandRegexes[2] = "^show leaderboard$";
        commandRegexes[3] = "^save$";
        commandRegexes[4] = "^logout$";
        commandRegexes[5] = "^help$";
        commandRegexes[6] = "^exit$";

        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            this.commandPatterns[i] = Pattern.compile(commandRegexes[i]);
        }
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        CommandTypeLoginPage commandType = getCommandType(command);
        switch (commandType) {
            case CREATE_ACCOUNT:
                callCreateAccountFromController(command);
                break;
            case LOGIN:
                callLoginFromController(command);
                return mainMenu;
            case SHOW_LEAD_BOARD:
                callShowLeaderBoardFromController();
                break;
            case SAVE:
                callSaveFromController();
                break;
            case LOGOUT:
                callLogOutFromController();
                break;
            case HELP:
                printListOfCommands();
                break;
            case EXIT:
                return getParentMenu();
        }
        return this;
    }

    private CommandTypeLoginPage getCommandType(String command) {
        for (int i = 0; i < NUMBER_OF_COMMANDS; i++) {
            Matcher matcher = commandPatterns[i].matcher(command);
            if (matcher.find()) {
                return CommandTypeLoginPage.getCommandType(i);
            }
        }
        throw new GameException("Invalid command!");
    }

    private void callCreateAccountFromController(String command) {
        String userName = extractUserName(command);
        getController().createAccount(userName);
    }

    private void callLoginFromController(String command) {
        String userName = extractUserName(command);
        getController().login(userName);
    }

    private void callShowLeaderBoardFromController() {
        getController().showLeaderBoard();
    }

    private void callSaveFromController() {
        getController().save();
    }

    private void printListOfCommands() {
        getController().showMessage(getListOfCommands());
    }

    private void callLogOutFromController() {
        getController().logout();
    }

    private String getListOfCommands() {
        return "\nAccount\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- create account [user name]\n" +
                "2- login [user name]\n" +
                "3- show leaderboard\n" +
                "4- save\n" +
                "5- log out\n" +
                "6- exit\n" +
                "7- help\n";
    }

    private String extractUserName(String command) {
        String[] strings = command.split(" ");
        return strings[strings.length - 1];
    }
}
