package com.ap.duelyst;

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
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

@SuppressWarnings("Duplicates")
public class Main_test extends Application {
    private FXMLLoader firstMenuLoader;
    private FXMLLoader secondMenuLoader;
    private FXMLLoader shopLoader;
    private FXMLLoader collectionLoader;
    private FXMLLoader battleMenuLoader;
    private FXMLLoader customLoader;
    private FXMLLoader battleLoader;
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
    private Scene inGameBattleScene;
    boolean custom = false;
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
        battleLoader =
                new FXMLLoader(getClass().getResource("view/battle/battle.fxml"));

        customLoader = new FXMLLoader(getClass().getResource("view/customize" +
                "/customCard.fxml"));
        loginPageScene = makeLogInPageScene();
        mainMenuScene = makeMainMenuScene(primaryStage);
        shopScene = makeShopScene();
        collectionScene = makeCollectionScene();
        battleScene = makeBattleMenuScene();
        customScene = makeCustomScene(primaryStage);
        inGameBattleScene = makeBattleScene();

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
    }

    private Scene makeCustomScene(Stage stage) throws IOException {
        Parent root = customLoader.load();
        ((CustomCardController) customLoader.getController()).setController(controller);
        ((CustomCardController) customLoader.getController()).setEventHandler(event -> {
            custom = false;
            stage.setScene(mainMenuScene);
        });
        Scene scene = new Scene(root, 1830, 1000);
        scene.setCursor(new ImageCursor(new Image(Utils.getPath(
                "mouse_auto.png"))));
        scene.getStylesheets().add("com/ap/duelyst/Bugatti.css");
        return scene;
    }

    private void updateMenu(Stage stage) {
        currentMenu = menuManager.getCurrentMenu();
        if(currentMenu instanceof LoginPage){
            firstMenuController.setLoginPage((LoginPage) currentMenu);
            stage.setScene(loginPageScene);
            stage.setTitle("Game Of Cards: Log in");
        }else if(currentMenu instanceof MainMenu){
            secondMenuController.setMainMenu((MainMenu) currentMenu);
            secondMenuController.update();
            stage.setScene(mainMenuScene);
            stage.setTitle("Game Of Cards: Main Menu");
        }else if(currentMenu instanceof ShopMenu){
            shopController.setShopMenu((ShopMenu) currentMenu);
            shopController.update();
            stage.setScene(shopScene);
            stage.setTitle("Game Of Cards: Shop");
        }else if(currentMenu instanceof CollectionMenu){
            collectionController.setCollectionMenu((CollectionMenu) currentMenu);
            collectionController.loadMainDeckOnTable();
            collectionController.update();
            stage.setScene(collectionScene);
            stage.setTitle("Game Of Cards: Collection");
        }else if(currentMenu instanceof BattleMenu){
            battleMenuController.setBattleMenu((BattleMenu) currentMenu);
            battleMenuController.update();
            stage.setScene(battleScene);
            stage.setTitle("Game Of Cards : Battle Menu");
        }else if(currentMenu instanceof InGameBattleMenu){
            battleController.setGame(controller);
            stage.setScene(inGameBattleScene);
            stage.setTitle("Game Of Cards : Battle");
        }
        stage.getScene().setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
    }
    private Scene makeLogInPageScene() throws IOException{
        Parent root = firstMenuLoader.load();
        firstMenuController = firstMenuLoader.getController();
        firstMenuController.setMenuManager(menuManager);
        firstMenuController.setLoginPage((LoginPage) currentMenu);
        firstMenuController.setController(controller);
        Scene scene = new Scene(root, 1830, 1000);
        scene.getStylesheets().add("com/ap/duelyst/FirstMenu.css");
        return scene;
    }
    private Scene makeMainMenuScene(Stage stage) throws IOException{
        Parent root = secondMenuLoader.load();
        secondMenuController = secondMenuLoader.getController();
        secondMenuController.setMenuManager(menuManager);
        secondMenuController.setController(controller);
        secondMenuController.setEvent(event -> {
            custom = true;
            stage.setScene(customScene);
        });
        Scene scene = new Scene(root, 1830, 1000);
        scene.getStylesheets().add("com/ap/duelyst/SecondMenu.css");
        scene.setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
        return scene;
    }
    private Scene makeShopScene() throws IOException{
        Parent root = shopLoader.load();
        shopController = shopLoader.getController();
        shopController.setMenuManager(menuManager);
        shopController.setController(controller);
        Scene scene = new Scene(root, 1830, 1000);
        scene.getStylesheets().add("com/ap/duelyst/shop.css");
        return scene;
    }
    private Scene makeCollectionScene() throws IOException{
        Parent root = collectionLoader.load();
        collectionController = collectionLoader.getController();
        collectionController.setMenuManager(menuManager);
        collectionController.setController(controller);
        Scene scene = new Scene(root, 1830, 1000);
        scene.getStylesheets().add("com/ap/duelyst/collection.css");
        return scene;
    }
    private Scene makeBattleMenuScene() throws IOException{
        Parent root = battleMenuLoader.load();
        battleMenuController= battleMenuLoader.getController();
        battleMenuController.setMenuManager(menuManager);
        battleMenuController.setController(controller);
        Scene scene = new Scene(root, 1830, 1000);
        scene.getStylesheets().add("com/ap/duelyst/BattleMenu.css");
        return scene;
    }
    private Scene makeBattleScene() throws IOException{
        Parent root = battleLoader.load();
        battleController= battleLoader.getController();
        //battleController.setMenuManager(menuManager);
        Scene scene = new Scene(root, 1830, 1000);
        scene.getStylesheets().add("com/ap/duelyst/Bugatti.css");
        return scene;
    }
    public static void main(String[] args) {
        ControllerThread thread = new ControllerThread();
        controller = thread.getController();
        thread.setDaemon(true);
        thread.start();
        menuManager = controller.getMenuManager();
        currentMenu = menuManager.getCurrentMenu();
        launch(args);
    }
}
class ControllerThread extends Thread{
    private Controller controller;
    ControllerThread(){
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
