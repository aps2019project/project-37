package controller.menu;

import controller.Controller;
import controller.GameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum CommandTypeShopMenu{
    EXIT(0),
    SHOW_COLLECTION(1),
    SEARCH(2),
    SEARCH_COLLECTION(3),
    BUY(4),
    SELL(5),
    SHOW(6),
    HELP(7);


    int commandIndex;
    CommandTypeShopMenu(int commandIndex){
        this.commandIndex = commandIndex;
    }
    public static CommandTypeShopMenu getCommandType(int commandIndex){
        for(CommandTypeShopMenu commandType :values()){
            if(commandType.equals(commandIndex)){
                return commandType;
            }
        }
        return null;
    }
}
public class ShopMenu extends Menu {
    private static final int NUMBER_OF_COMMANDS = 8;
    private Pattern commandPatterns[] = new Pattern[NUMBER_OF_COMMANDS];

    ShopMenu(Controller controller){
        super(controller);
    }
    private void initCommandPatterns(){
        String commandRegexes[] = new String[NUMBER_OF_COMMANDS];
        commandRegexes[0] = "^exit\\s*$";
        commandRegexes[1] = "^show collection\\w+\\s*$";
        commandRegexes[2] = "^search \\w+\\s*$";
        commandRegexes[3] = "^search collection \\w+\\s*$";
        commandRegexes[4] = "^buy \\w+\\s*$";
        commandRegexes[5] = "^sell \\w+\\s*$";
        commandRegexes[6] = "^show\\s*$";
        commandRegexes[7] = "^help\\s*$";
        for(int i=0; i < NUMBER_OF_COMMANDS; i++){
            this.commandPatterns[i] = Pattern.compile(commandRegexes[i]);
        }
    }
    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        CommandTypeShopMenu commandType = getCommandType(command);
        if(commandType.equals(CommandTypeShopMenu.EXIT)) {
            return getParentMenu();
        }else if(commandType.equals(CommandTypeShopMenu.SHOW_COLLECTION)) {
            callShowCollectionFromController();
        }else if(commandType.equals(CommandTypeShopMenu.SEARCH)) {
            String name = extractLastWord(command);
            callSearchInShopFromController(name);
        }else if(commandType.equals(CommandTypeShopMenu.SEARCH_COLLECTION)) {
            String name = extractLastWord(command);
            callSearchInCollectionFromController(name);
        }else if(commandType.equals(CommandTypeShopMenu.BUY)) {
            String id = extractLastWord(command);
            callBuyFromController(id);
        }else if(commandType.equals(CommandTypeShopMenu.SELL)) {
            String id = extractLastWord(command);
            callSellFromController(id);
        }else if(commandType.equals(CommandTypeShopMenu.SHOW)){
            callShowShopFromController();
        }else if(commandType.equals(CommandTypeShopMenu.HELP)){
            printListOfCommands();
        }
        return this;
    }
    private CommandTypeShopMenu getCommandType(String command) {
        int commandIndex = -1;
        for(int i=0; i < NUMBER_OF_COMMANDS; i++) {
            Matcher matcher = commandPatterns[i].matcher(command);
            if(matcher.find()){
                commandIndex = i;
                return CommandTypeShopMenu.getCommandType(commandIndex);
            }
        }
        if(commandIndex == -1){
            throw new GameException("Invalid command!");
        }
        return null;
    }
    private void callShowCollectionFromController(){
        getController().showCollection();
    }
    private void callSearchInShopFromController(String name){
        getController().searchInShop(name);
    }
    private void callSearchInCollectionFromController(String name){
        getController().searchInCollection(name);
    }
    private void callBuyFromController(String name){
        getController().buy(name);
    }
    private void callSellFromController(String id){
        getController().sell(id);
    }
    private void callShowShopFromController(){
        getController().showShop();
    }
    private String extractLastWord(String command){
        Pattern pattern = Pattern.compile("\\w+(?=\\s*)$");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        return matcher.group(0);
    }
    private void printListOfCommands(){
        getController().showMessage(getListOfCommands());
    }
    private String getListOfCommands(){
        StringBuilder commands = new StringBuilder();
        commands.append("Shop Menu\n");
        commands.append("-----------\n");
        commands.append("Commands:\n");
        commands.append("1- exit\n");
        commands.append("2- show collection\n");
        commands.append("3- search [item name | card name]\n");
        commands.append("4- search collection [item name | card name]\n");
        commands.append("5- buy [item name | card name] \n");
        commands.append("5- sell [item id | card id] \n");
        commands.append("6- show \n");
        commands.append("7- help \n");
        return commands.toString();
    }
}
