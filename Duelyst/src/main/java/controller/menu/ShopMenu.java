package controller.menu;

import controller.Controller;
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
    ShopMenu(Controller controller){
        super(controller);
    }
    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        return this;
    }
}
