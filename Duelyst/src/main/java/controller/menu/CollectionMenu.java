package controller.menu;

import controller.Controller;

public class CollectionMenu extends Menu {
    CollectionMenu(Controller controller){
        super(controller);
    }
    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        return this;
    }
}
