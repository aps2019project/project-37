package controller.menu.ingame;

import controller.Controller;
import controller.menu.Menu;

public class GraveyardMenu extends Menu {

    enum GraveyardType{
        SHOW_INFO(0),
        SHOW_CARDS(1),
        SHOW_MENU(2),
        EXIT(3);

        int commandIndex;

        GraveyardType(int commandIndex) {
            this.commandIndex = commandIndex;
        }

        public static GraveyardType getCommandType(int commandIndex) {
            for (GraveyardType commandType : values()) {
                if (commandType.commandIndex == commandIndex) {
                    return commandType;
                }
            }
            return null;
        }
    }

    public GraveyardMenu(Controller controller) {
        super(controller);
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        return null;
    }
}
