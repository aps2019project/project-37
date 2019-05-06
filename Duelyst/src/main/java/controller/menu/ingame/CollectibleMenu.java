package controller.menu.ingame;

import controller.Controller;
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

    public CollectibleMenu(Controller controller) {
        super(controller);
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        return null;
    }
}
