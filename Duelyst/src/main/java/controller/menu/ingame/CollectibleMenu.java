package controller.menu.ingame;

import controller.Controller;
import controller.GameException;
import controller.menu.Menu;

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
            "^show info$",
            "^use$",
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
                getController().useCollectibleItem();
                break;
            case SHOW_MENU:
                showMessage(menuHelp());
                break;
            case EXIT:
                getController().deselectItem();
                showMessage("\nyou've entered game menu\n");
                return getParentMenu();
        }
        return this;
    }


    private String menuHelp() {
        return "\nIn Game Menu\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- show info\n" +
                "2- use\n" +
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

}
