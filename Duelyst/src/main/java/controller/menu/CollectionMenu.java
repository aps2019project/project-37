package controller.menu;

import controller.Controller;
import controller.GameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum CommandTypeCollectionMenu{
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
    CommandTypeCollectionMenu(int commandIndex){
        this.commandIndex = commandIndex;
    }
    public static CommandTypeCollectionMenu getCommandType(int commandIndex){
        for(CommandTypeCollectionMenu commandType :values()){
            if(commandType.equals(commandIndex)){
                return commandType;
            }
        }
        return null;
    }
}
public class CollectionMenu extends Menu {
    private static final int NUMBER_OF_COMMANDS = 13;
    private Pattern commandPatterns[] = new Pattern[NUMBER_OF_COMMANDS];

    CollectionMenu(Controller controller){
        super(controller);
    }
    private void initCommandPatterns(){
        String commandRegexes[] = new String[NUMBER_OF_COMMANDS];
        commandRegexes[0] = "^exit\\s*$";
        commandRegexes[1] = "^show\\s*$";
        commandRegexes[2] = "^search \\w+\\s*$";
        commandRegexes[3] = "^save\\s*$";
        commandRegexes[4] = "^create deck\\w+\\s*$";
        commandRegexes[5] = "^delete deck\\w+\\s*$";
        commandRegexes[6] = "^add \\w+ to \\w+\\s*$";
        commandRegexes[7] = "^remove \\w+ from \\w+\\s*$";
        commandRegexes[8] = "^validate deck\\w+\\s*$";
        commandRegexes[9] = "^select deck\\w+\\s*$";
        commandRegexes[10] = "^show all decks\\s*$";
        commandRegexes[11] = "^show deck \\w+\\s*$";
        commandRegexes[12] = "^help\\s*$";
        for(int i=0; i < NUMBER_OF_COMMANDS; i++){
            this.commandPatterns[i] = Pattern.compile(commandRegexes[i]);
        }
    }
    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        CommandTypeCollectionMenu commandType = getCommandType(command);
        if(commandType.equals(CommandTypeCollectionMenu.EXIT)){
            return getParentMenu();
        }else if(commandType.equals(CommandTypeCollectionMenu.SHOW)){
            callShowCollectionFromController();
        }else if(commandType.equals(CommandTypeCollectionMenu.SEARCH)){
            String name = extractLastWord(command);
            callSearchInCollectionFromController(name);
        }else if(commandType.equals(CommandTypeCollectionMenu.SAVE)){
            callSaveFromController();
        }else if(commandType.equals(CommandTypeCollectionMenu.CREATE_DECK)){
            String name = extractLastWord(command);
            callCreateDeckFromController(name);
        }else if(commandType.equals(CommandTypeCollectionMenu.DELETE_DECK)){
            String name = extractLastWord(command);
            callDeleteDeckFromController(name);
        }else if(commandType.equals(CommandTypeCollectionMenu.ADD)){
            String name = extractLastWord(command);
            String id = extractIdForAddOrRemove(command);
            callAddToDeckFromController(id,name);
        }else if(commandType.equals(CommandTypeCollectionMenu.REMOVE)){
            String name = extractLastWord(command);
            String id = extractIdForAddOrRemove(command);
            getController().removeFromDeck(id, name);
        }else if(commandType.equals(CommandTypeCollectionMenu.VALIDATE_DECK)){
            String name = extractLastWord(command);
            getController().validateDeck(name);
        }else if(commandType.equals(CommandTypeCollectionMenu.SELECT_DECK)){
            String name = extractLastWord(command);
            getController().setMainDeck(name);
        }else if(commandType.equals(CommandTypeCollectionMenu.SHOW_ALL_DECK)){
            getController().showAllDecksInfo();
        }else if(commandType.equals(CommandTypeCollectionMenu.SHOW_DECK)){
            String name = extractLastWord(command);
            getController().showDeckInfo(name);
        }else if(commandType.equals(CommandTypeCollectionMenu.HELP)){
            printListOfCommands();
        }
        return this;
    }
    private void callShowCollectionFromController(){
        getController().showCollection();
    }
    private void callSearchInCollectionFromController(String name){
        getController().searchInCollection(name);
    }
    private void callSaveFromController(){
        getController().save();
    }
    private void callCreateDeckFromController(String name){
        getController().createDeck(name);
    }
    private void callDeleteDeckFromController(String name){
        getController().deleteDeck(name);
    }
    private void callAddToDeckFromController(String id, String name){
        getController().addToDeck(id, name);
    }
    private CommandTypeCollectionMenu getCommandType(String command) {
        int commandIndex = -1;
        for(int i=0; i < NUMBER_OF_COMMANDS; i++) {
            Matcher matcher = commandPatterns[i].matcher(command);
            if(matcher.find()){
                commandIndex = i;
                return CommandTypeCollectionMenu.getCommandType(commandIndex);
            }
        }
        if(commandIndex == -1){
            throw new GameException("Invalid command!");
        }
        return null;
    }
    private String extractLastWord(String command){
        Pattern pattern = Pattern.compile("\\w+(?=\\s*)$");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        return matcher.group(0);
    }
    private String extractIdForAddOrRemove(String command){
        Pattern pattern = Pattern.compile("(?<=add |remove )\\w+$");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        return matcher.group(0);
    }
    private void printListOfCommands(){
        getController().showMessage(getListOfCommands());
    }
    private String getListOfCommands(){
        String commands = "Collection Menu\n" +
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
        return commands;
    }
}
