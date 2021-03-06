package com.ap.duelyst.controller.menu;

import com.ap.duelyst.controller.Controller;

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
