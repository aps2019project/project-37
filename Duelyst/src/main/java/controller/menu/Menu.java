package controller.menu;

import controller.Controller;

abstract public class Menu {
    private Controller controller;
    private Menu parentMenu;

    public Menu(Controller controller) {
        setController(controller);
    }

    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    abstract public Menu runCommandAndGetNextMenu(String command);

    public void showMessage(String message) {
        controller.showMessage(message);
    }
}
