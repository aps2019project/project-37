package com.ap.duelyst.view.menus;

import com.ap.duelyst.Main;
import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.menu.LoginPage;
import com.ap.duelyst.controller.menu.MainMenu;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.view.DialogController;
import com.ap.duelyst.view.customize.CustomCardController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SecondMenuController implements Initializable {
    public Button customButton;
    public VBox dialogContainer;
    public HBox dialog;
    public Label dialogText;
    public StackPane root;
    private Controller controller;
    private MenuManager menuManager;
    private MainMenu mainMenu;
    public ImageView gameLogo;
    public VBox mainBox;
    public Button exitButton;
    public Button shopButton;
    public Button collectionButton;
    public Button battleButton;
    public Label userNameLabel;
    private String buttonNormalPath;
    private String buttonGlowPath;
    private DialogController dialogController;
    private EventHandler<ActionEvent> event;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();
        dialogController = new DialogController(root, dialog, dialogText,
                dialogContainer);
    }

    private void setAllBackgrounds() {
        this.buttonNormalPath = Utils.getPath("button_primary.png");
        this.buttonGlowPath = Utils.getPath("button_primary_glow.png");

        String back = Utils.getPath("chapter9_background@2x.jpg");
        mainBox.setStyle("-fx-background-image: url(' " + back + "')");
        mainBox.setId("mainBox");
        FirstMenuController.setStyle(gameLogo, collectionButton, buttonNormalPath,
                shopButton, battleButton, exitButton, customButton);
    }

    private void setAllActions() {
        exitButton.setOnAction(e -> {
            menuManager.setCurrentMenu(mainMenu.getParentMenu());
        });
        shopButton.setOnAction(e -> {
            menuManager.setCurrentMenu(mainMenu.getShopMenu());
        });
        collectionButton.setOnAction(e -> {
            menuManager.setCurrentMenu(mainMenu.getCollectionMenu());
        });
        battleButton.setOnAction(e -> {
            if (controller.getCurrentAccount().getMainDeck() != null
                    && controller.getCurrentAccount().getMainDeck().isValid()) {
                menuManager.setCurrentMenu(mainMenu.getBattleMenu());
            } else {
                dialogController.showDialog("deck is not valid");
            }
        });
        setButtonGlowOnMouseMoving(exitButton);
        setButtonGlowOnMouseMoving(collectionButton);
        setButtonGlowOnMouseMoving(shopButton);
        setButtonGlowOnMouseMoving(battleButton);
        setButtonGlowOnMouseMoving(customButton);

    }

    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    public void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void update() {
        userNameLabel.setText("username : " + Main.userName);
    }

    private void setButtonGlowOnMouseMoving(Button button) {
        button.setOnMouseEntered(e -> {
            setButtonBackground(button, buttonGlowPath);
        });
        button.setOnMouseExited(e -> {
            setButtonBackground(button, buttonNormalPath);
        });
    }

    private void setButtonBackground(Button button, String backgroundPath) {
        button.setStyle("-fx-background-image: url(' " + backgroundPath + "')");
    }

    public void showCustomCard(ActionEvent e) {
        event.handle(e);
    }

    public void setEvent(EventHandler<ActionEvent> event) {
        this.event = event;
    }
}
