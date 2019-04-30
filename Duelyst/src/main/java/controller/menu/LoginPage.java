package controller.menu;

import controller.Controller;
import controller.GameException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum CommandTypeLoginPage {
    CREATE_ACCOUNT(0),
    LOGIN(1),
    SHOW_LEADERBOARD(2),
    SAVE(3),
    LOGOUT(4),
    HELP(5);

    int commandIndex;
    CommandTypeLoginPage(int commandIndex){
        this.commandIndex = commandIndex;
    }
    public static CommandTypeLoginPage getCommandType(int commandIndex){
        for(CommandTypeLoginPage commandType:values()){
            if(commandType.equals(commandIndex)){
                return commandType;
            }
        }
        return null;
    }
}
public class LoginPage extends Menu {
    private static final int NUMBER_OF_COMMANDS = 6;
    private MainMenu mainMenu;
    private Pattern commandPatterns[] = new Pattern[NUMBER_OF_COMMANDS];

    public LoginPage(Controller controller){
        super(controller);
        setParentMenu(null);
        mainMenu = new MainMenu(controller);
        mainMenu.setParentMenu(this);
        initCommandPatterns();
    }
    private void initCommandPatterns(){
        String commandRegexes[] = new String[6];
        commandRegexes[0] = "^create account \\w+\\s*$";
        commandRegexes[1] = "^login \\w+\\s*$";
        commandRegexes[2] = "^show leaderboard\\s*$";
        commandRegexes[3] = "^save\\s*$";
        commandRegexes[4] = "^logout\\s*$";
        commandRegexes[5] = "^help\\s*$";
        for(int i=0; i < NUMBER_OF_COMMANDS; i++){
            this.commandPatterns[i] = Pattern.compile(commandRegexes[i]);
        }
    }
    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        CommandTypeLoginPage commandType = getCommandType(command);
        if(commandType.equals(CommandTypeLoginPage.CREATE_ACCOUNT)) {
            callCreateAccountFromController(command);
        }else if(commandType.equals(CommandTypeLoginPage.LOGIN)) {
            callLoginFromController(command);
            return mainMenu;
        }else if(commandType.equals(CommandTypeLoginPage.SHOW_LEADERBOARD)) {
            callShowLeaderBoardFromController();
        }else if(commandType.equals(CommandTypeLoginPage.SAVE)) {
            callSaveFromController();
        }else if(commandType.equals(CommandTypeLoginPage.LOGOUT)){
                callLogOutFromController();
        }else if(commandType.equals(CommandTypeLoginPage.HELP)) {
            printListOfCommands();
        }
        return this;
    }
    private CommandTypeLoginPage getCommandType(String command) {
        int commandIndex = -1;
        for(int i=0; i < NUMBER_OF_COMMANDS; i++) {
            Matcher matcher = commandPatterns[i].matcher(command);
            if(matcher.find()){
                commandIndex = i;
                return CommandTypeLoginPage.getCommandType(commandIndex);
            }
        }
        if(commandIndex == -1){
            throw new GameException("Invalid command!");
        }
        return null;
    }
    private void callCreateAccountFromController(String command){
        String userName = extractUserName(command);
        getController().createAccount(userName);
    }
    private void callLoginFromController(String command){
        String userName = extractUserName(command);
        getController().login(userName);
    }
    private void callShowLeaderBoardFromController(){
        getController().showLeaderBoard();
    }
    private void callSaveFromController(){
        getController().save();
    }
    private void printListOfCommands(){
        getController().showMessage(getListOfCommands());
    }
    private void callLogOutFromController(){
        getController().logOut();
    }
    private String getListOfCommands(){
        StringBuilder commands = new StringBuilder();
        commands.append("Account\n");
        commands.append("-----------\n");
        commands.append("Commands:\n");
        commands.append("1- create account [user name]\n");
        commands.append("2- login [user name]\n");
        commands.append("3- show leaderboard\n");
        commands.append("4- save\n");
        commands.append("5- log out\n");
        commands.append("6- save\n");
        commands.append("7- help\n");
        return commands.toString();
    }
    private String extractUserName(String command){
        Pattern pattern = Pattern.compile("\\w+(?=\\s*)$");
        Matcher matcher = pattern.matcher(command);
        matcher.find();
        return matcher.group(0);
    }
}
