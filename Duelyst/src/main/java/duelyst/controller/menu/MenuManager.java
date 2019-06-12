package duelyst.controller.menu;

import duelyst.controller.Controller;

public class MenuManager {
    private Menu currentMenu;

    public MenuManager(Controller controller) {
        currentMenu = new LoginPage(controller);
    }

    public Menu getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(Menu menu) {
        this.currentMenu = menu;
    }
}
