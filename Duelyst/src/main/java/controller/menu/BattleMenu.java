package controller.menu;

import controller.Controller;
import model.game.Game;

public class BattleMenu extends Menu {

    BattleMenu(Controller controller) {
        super(controller);
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        return this;
    }
}
