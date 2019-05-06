package controller.menu.ingame;

import controller.Controller;
import controller.menu.Menu;

public class CardMenu extends Menu {

    enum CardType {
        MOVE_TO(0),
        ATTACK(1),
        ATTACK_COMBO(2),
        SPECIAL_POWER(3),
        SHOW_MENU(4),
        EXIT(5);

        int commandIndex;

        CardType(int commandIndex) {
            this.commandIndex = commandIndex;
        }

        public static CardType getCommandType(int commandIndex) {
            for (CardType commandType : values()) {
                if (commandType.commandIndex == commandIndex) {
                    return commandType;
                }
            }
            return null;
        }

    }

    public CardMenu(Controller controller) {
        super(controller);
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        return null;
    }
}
