package com.ap.duelyst.view.menus;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.menu.LoginPage;
import com.ap.duelyst.controller.menu.MainMenu;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.model.Utils;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.util.ResourceBundle;

public class SecondMenuController implements Initializable {
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
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();
    }
    private void setAllBackgrounds(){
        this.buttonNormalPath = Utils.getPath("button_primary.png");
        this.buttonGlowPath = Utils.getPath("button_primary_glow.png");

        String back = Utils.getPath("chapter9_background@2x.jpg");
        mainBox.setStyle("-fx-background-image: url(' " + back + "')");
        mainBox.setId("mainBox");
        String titleOfGame = Utils.getPath("game_logo.png");
        gameLogo.setImage(new Image(titleOfGame));

        collectionButton.setStyle("-fx-background-image: url(' " + buttonNormalPath + "')");
        shopButton.setStyle("-fx-background-image: url(' " + buttonNormalPath + "')");
        battleButton.setStyle("-fx-background-image: url(' " + buttonNormalPath + "')");
        exitButton.setStyle("-fx-background-image: url(' " + buttonNormalPath + "')");
    }
    private void setAllActions(){
        exitButton.setOnAction(e->{
            menuManager.setCurrentMenu(mainMenu.getParentMenu());
        });
        shopButton.setOnAction(e->{
            menuManager.setCurrentMenu(mainMenu.getShopMenu());
        });
        collectionButton.setOnAction(e -> {
            menuManager.setCurrentMenu(mainMenu.getCollectionMenu());
        });
        battleButton.setOnAction(e-> {
            menuManager.setCurrentMenu(mainMenu.getBattleMenu());
        });
        setButtonGlowOnMouseMoving(exitButton);
        setButtonGlowOnMouseMoving(collectionButton);
        setButtonGlowOnMouseMoving(shopButton);
        setButtonGlowOnMouseMoving(battleButton);

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
    public void update(){
        userNameLabel.setText("username : " + controller.getCurrentAccount().getUserName());
    }
    private void setButtonGlowOnMouseMoving(Button button){
        button.setOnMouseEntered(e -> {
            setButtonBackground(button,buttonGlowPath);
        });
        button.setOnMouseExited(e -> {
            setButtonBackground(button,buttonNormalPath);
        });
    }
    private void setButtonBackground(Button button, String backgroundPath){
        button.setStyle("-fx-background-image: url(' " + backgroundPath + "')");
    }
}
