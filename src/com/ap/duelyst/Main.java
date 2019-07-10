package com.ap.duelyst;

import com.ap.duelyst.controller.Constants;
import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.menu.*;
import com.ap.duelyst.controller.menu.ingame.InGameBattleMenu;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.view.battle.BattleController;
import com.ap.duelyst.view.menus.*;
import com.ap.duelyst.view.customize.CustomCardController;
import com.ap.duelyst.view.menus.CollectionController;
import com.ap.duelyst.view.menus.FirstMenuController;
import com.ap.duelyst.view.menus.SecondMenuController;
import com.ap.duelyst.view.menus.ShopController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@SuppressWarnings("Duplicates")
public class Main extends Application {
    public static String token;
    public static String userName;
    public static PrintWriter writer;
    public static Scanner scanner;

    private FXMLLoader firstMenuLoader;
    private FXMLLoader secondMenuLoader;
    private FXMLLoader shopLoader;
    private FXMLLoader collectionLoader;
    private FXMLLoader battleMenuLoader;
    private FXMLLoader customLoader;
    private FirstMenuController firstMenuController;
    private SecondMenuController secondMenuController;
    private ShopController shopController;
    private CollectionController collectionController;
    private BattleMenuController battleMenuController;
    private BattleController battleController;
    private static Menu currentMenu;
    private static MenuManager menuManager;
    private static Controller controller;
    private Scene loginPageScene;
    private Scene mainMenuScene;
    private Scene shopScene;
    private Scene collectionScene;
    private Scene customScene;
    private boolean custom = false;
    private Scene battleScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        firstMenuLoader =
                new FXMLLoader(getClass().getResource("view/menus/firstMenu.fxml"));
        secondMenuLoader =
                new FXMLLoader(getClass().getResource("view/menus/secondMenu.fxml"));
        shopLoader =
                new FXMLLoader(getClass().getResource("view/menus/shop.fxml"));
        collectionLoader =
                new FXMLLoader(getClass().getResource("view/menus/collection.fxml"));
        battleMenuLoader =
                new FXMLLoader(getClass().getResource("view/menus/battleMenu.fxml"));
        customLoader =
                new FXMLLoader(getClass().getResource("view/customize/customCard.fxml"));
        loginPageScene = makeLogInPageScene();
        mainMenuScene = makeMainMenuScene(primaryStage);
        shopScene = makeShopScene();
        collectionScene = makeCollectionScene();
        battleScene = makeBattleMenuScene();
        customScene = makeCustomScene(primaryStage);
        updateMenu(primaryStage);
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!custom && menuManager.getCurrentMenu() != null) {
                    if (!currentMenu.equals(menuManager.getCurrentMenu())) {
                        updateMenu(primaryStage);
                    }
                }
            }
        }.start();
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }

    private Scene makeCustomScene(Stage stage) throws IOException {
        Parent root = customLoader.load();
        ((CustomCardController) customLoader.getController()).setEventHandler(event -> {
            custom = false;
            stage.setScene(mainMenuScene);
            stage.setTitle("Game Of Cards: Main Menu");
        });

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight() - 30);
        scene.setCursor(new ImageCursor(new Image(Utils.getPath(
                "mouse_auto.png"))));
        scene.getStylesheets().add("com/ap/duelyst/Bugatti.css");
        return scene;
    }

    private void updateMenu(Stage stage) {
        currentMenu = menuManager.getCurrentMenu();
        if (currentMenu instanceof LoginPage) {
            firstMenuController.setLoginPage((LoginPage) currentMenu);
            stage.setScene(loginPageScene);
            stage.setTitle("Game Of Cards: Log in");
        } else if (currentMenu instanceof MainMenu) {
            battleController = null;
            secondMenuController.setMainMenu((MainMenu) currentMenu);
            secondMenuController.update();
            stage.setScene(mainMenuScene);
            stage.setTitle("Game Of Cards: Main Menu");
        } else if (currentMenu instanceof ShopMenu) {
            shopController.setShopMenu((ShopMenu) currentMenu);
            shopController.update();
            stage.setScene(shopScene);
            stage.setTitle("Game Of Cards: Shop");
        } else if (currentMenu instanceof CollectionMenu) {
            collectionController.setCollectionMenu((CollectionMenu) currentMenu);
            collectionController.loadMainDeckOnTable();
            collectionController.update();
            stage.setScene(collectionScene);
            stage.setTitle("Game Of Cards: Collection");
        } else if (currentMenu instanceof BattleMenu) {
            battleMenuController.setBattleMenu((BattleMenu) currentMenu);
            battleMenuController.update();
            stage.setScene(battleScene);
            stage.setTitle("Game Of Cards : Battle Menu");
        } else if (currentMenu instanceof InGameBattleMenu) {
            Scene inGameBattleScene = null;
            try {
                inGameBattleScene = makeBattleScene();
            } catch (IOException ignored) {
            }
            battleController.setMenu((InGameBattleMenu) currentMenu);
            stage.setScene(inGameBattleScene);
            stage.setTitle("Game Of Cards : Battle");
        }
    }

    private Scene makeLogInPageScene() throws IOException {
        Parent root = firstMenuLoader.load();
        firstMenuController = firstMenuLoader.getController();
        firstMenuController.setMenuManager(menuManager);
        firstMenuController.setLoginPage((LoginPage) currentMenu);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight() - 30);
        scene.getStylesheets().add("com/ap/duelyst/FirstMenu.css");
        scene.setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
        return scene;
    }

    private Scene makeMainMenuScene(Stage stage) throws IOException {
        Parent root = secondMenuLoader.load();
        secondMenuController = secondMenuLoader.getController();
        secondMenuController.setMenuManager(menuManager);
        secondMenuController.setEvent(event -> {
            custom = true;
            stage.setScene(customScene);
            stage.setTitle("Game Of Cards: Custom Card");
        });
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight() - 30);
        scene.getStylesheets().add("com/ap/duelyst/SecondMenu.css");
        scene.setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
        return scene;
    }

    private Scene makeShopScene() throws IOException {
        Parent root = shopLoader.load();
        shopController = shopLoader.getController();
        shopController.setMenuManager(menuManager);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight() - 30);
        scene.getStylesheets().add("com/ap/duelyst/shop.css");
        scene.setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
        return scene;
    }

    private Scene makeCollectionScene() throws IOException {
        Parent root = collectionLoader.load();
        collectionController = collectionLoader.getController();
        collectionController.setMenuManager(menuManager);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight() - 30);
        scene.getStylesheets().add("com/ap/duelyst/collection.css");
        scene.setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
        return scene;
    }

    private Scene makeBattleMenuScene() throws IOException {
        Parent root = battleMenuLoader.load();
        battleMenuController = battleMenuLoader.getController();
        battleMenuController.setMenuManager(menuManager);

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight() - 30);
        scene.getStylesheets().add("com/ap/duelyst/BattleMenu.css");
        scene.setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
        return scene;
    }

    private Scene makeBattleScene() throws IOException {
        FXMLLoader battleLoader =
                new FXMLLoader(getClass().getResource("view/battle/battle.fxml"));
        Parent root = battleLoader.load();
        battleController = battleLoader.getController();
        battleController.setManager(menuManager);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight() - 30);
        scene.getStylesheets().add("com/ap/duelyst/Bugatti.css");
        scene.setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
        return scene;
    }

    public static void main(String[] args) throws IOException {
        ControllerThread thread = new ControllerThread();
        controller = thread.getController();
        thread.setDaemon(true);
        thread.start();
        Socket socket = new Socket(Constants.ip, 8080);
        writer = new PrintWriter(socket.getOutputStream(), true);
        scanner = new Scanner(socket.getInputStream());
        menuManager = new MenuManager(null);
        currentMenu = menuManager.getCurrentMenu();
        launch(args);
    }
}

class ControllerThread extends Thread {
    private Controller controller;

    ControllerThread() {
        controller = new Controller();
    }

    @Override
    public void run() {
        controller.start();
    }

    public Controller getController() {
        return controller;
    }
}
