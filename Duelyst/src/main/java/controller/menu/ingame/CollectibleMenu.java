package controller.menu.ingame;

import controller.Controller;
import controller.GameException;
import controller.menu.Menu;
import model.items.CollectableItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectibleMenu extends Menu {

    enum CollectibleType{
        SHOW_INFO(0),
        USE(1),
        SHOW_MENU(2),
        EXIT(3);

        int commandIndex;

        CollectibleType(int commandIndex) {
            this.commandIndex = commandIndex;
        }

        public static CollectibleType getCommandType(int commandIndex) {
            for (CollectibleType commandType : values()) {
                if (commandType.commandIndex == commandIndex) {
                    return commandType;
                }
            }
            return null;
        }
    }

    private String[] matchers = {
            "^show info (\\w+_){2}\\d+$",
            "^use location \\(\\d+,\\d+\\)$",
            "^show menu$",
            "^exit$"
    };
    private CollectibleType type;

    public CollectibleMenu(Controller controller) {
        super(controller);
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        match(command);
        switch (type){
            case SHOW_INFO:
                getController().showCollectableItemInfo();
                break;
            case USE:
                int x = extractXForUse(command);
                int y = extractYForUse(command);
                getController().useCollectibleItem(x, y);
                break;
            case SHOW_MENU:
                showMessage(menuHelp());
                break;
            case EXIT:
                return getParentMenu();
        }
        return this;
    }


    private String menuHelp() {
        return "\nIn Game Menu\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- show info\n" +
                "2- use (x,y)\n" +
                "3- show menu\n" +
                "4- exit\n";
    }
    private void match(String command) {
        for (int i = 0; i < matchers.length; i++) {
            String matcher = matchers[i];
            if (command.matches(matcher)) {
                type = CollectibleType.getCommandType(i);
                return;
            }
        }
        throw new GameException("invalid command");
    }

    private int extractXForUse(String command){
        String[] strings = command.split(" ");
        String string = strings[strings.length - 1];
        Pattern pattern = Pattern.compile("\\d+(?=,)");
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()){
            return Integer.parseInt(matcher.group(0));
        }else{
            return -1;
        }
    }

    private int extractYForUse(String command){
        String[] strings = command.split(" ");
        String string = strings[strings.length - 1];
        Pattern pattern = Pattern.compile("(?<=,)\\d+");
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()){
            return Integer.parseInt(matcher.group(0));
        }else{
            return -1;
        }
    }

}
