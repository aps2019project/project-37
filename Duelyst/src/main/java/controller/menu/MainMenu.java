package controller.menu;

import controller.Controller;
import controller.GameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
enum CommandTypeMainMenu{
    COLLECTION(0),
    SHOP(1),
    BATTLE(2),
    EXIT(3),
    HELP(4);

    int commandIndex;
    CommandTypeMainMenu(int commandIndex){
        this.commandIndex = commandIndex;
    }
    public static CommandTypeMainMenu getCommandType(int commandIndex){
        for(CommandTypeMainMenu commandType :values()){
            if(commandType.equals(commandIndex)){
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
    private Pattern commandPatterns[] = new Pattern[NUMBER_OF_COMMANDS];

    MainMenu(Controller controller){
        super(controller);
        collectionMenu = new CollectionMenu(controller);
        battleMenu = new BattleMenu(controller);
        shopMenu = new ShopMenu(controller);
        collectionMenu.setParentMenu(this);
        battleMenu.setParentMenu(this);
        shopMenu.setParentMenu(this);
        initCommandPatterns();
    }
    private void initCommandPatterns(){
        String commandRegexes[] = new String[NUMBER_OF_COMMANDS];
        commandRegexes[0] = "^enter collection\\s*$";
        commandRegexes[1] = "^enter  shop\\w+\\s*$";
        commandRegexes[2] = "^enter battle\\s*$";
        commandRegexes[3] = "^exit\\s*$";
        commandRegexes[4] = "^help\\s*$";
        for(int i=0; i < NUMBER_OF_COMMANDS; i++){
            this.commandPatterns[i] = Pattern.compile(commandRegexes[i]);
        }
    }
    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        CommandTypeMainMenu commandType = getCommandType(command);
        if(commandType.equals(CommandTypeMainMenu.COLLECTION)) {
            return collectionMenu;
        }else if(commandType.equals(CommandTypeMainMenu.SHOP)) {
            return shopMenu;
        }else if(commandType.equals(CommandTypeMainMenu.BATTLE)) {
            return battleMenu;
        }else if(commandType.equals(CommandTypeMainMenu.EXIT)) {
            return getParentMenu();
        }else if(commandType.equals(CommandTypeMainMenu.HELP)) {
            printListOfCommands();
        }
        return this;
    }
    private CommandTypeMainMenu getCommandType(String command) {
        int commandIndex = -1;
        for(int i=0; i < NUMBER_OF_COMMANDS; i++) {
            Matcher matcher = commandPatterns[i].matcher(command);
            if(matcher.find()){
                commandIndex = i;
                return CommandTypeMainMenu.getCommandType(commandIndex);
            }
        }
        if(commandIndex == -1){
            throw new GameException("Invalid command!");
        }
        return null;
    }
    private void printListOfCommands(){
        getController().showMessage(getListOfCommands());
    }
    private String getListOfCommands(){
        String commands = "Main Menu\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- Enter collection\n" +
                "2- Enter shop\n" +
                "3- Enter battle\n" +
                "4- exit\n" +
                "5- help\n";
        return commands;
    }
}
