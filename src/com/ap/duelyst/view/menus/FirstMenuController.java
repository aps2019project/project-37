package com.ap.duelyst.view.menus;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.LoginPage;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.ResourceBundle;

public class FirstMenuController implements Initializable {
    private Controller controller;
    private MenuManager menuManager;
    private LoginPage loginPage;
    public StackPane stackPane;
    public Button leaderBoardButton;
    public Button logInButton;
    public Button logOutButton;
    public Button exitButton;
    public Button accountButton;
    public Button saveButton;
    public Button mainMenuButton;
    public TableView leaderBoardTable;
    public ImageView gameLogo;
    public TextField userNameText;
    public TextField passwordText;
    public Label userNameLabel;
    public Label errorLabel;
    public VBox errorBox;
    public Button exitErrorBox;
    private String greenButtonNormalPath;
    private String greenButtonGlowPath;
    private String blueButtonNormalPath;
    private String blueButtonGlowPath;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();

    }
    private void setAllBackgrounds(){
        this.greenButtonNormalPath = Utils.getPath("button_confirm.png");
        this.greenButtonNormalPath = Utils.getPath("button_confirm.png");
        this.greenButtonGlowPath = Utils.getPath("button_confirm_glow.png");

        this.blueButtonNormalPath = Utils.getPath("button_secondary.png");
        this.blueButtonGlowPath = Utils.getPath("button_secondary_glow.png");

        String back = Utils.getPath("chapter18_background@2x.jpg");
        stackPane.setStyle("-fx-background-image: url(' " + back + "')");
        stackPane.setId("stackPane");

        String titleOfGame = Utils.getPath("game_logo.png");
        gameLogo.setImage(new Image(titleOfGame));

        leaderBoardButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");
        logOutButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");
        exitButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");
        saveButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");
        mainMenuButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");

        accountButton.setStyle("-fx-background-image: url(' " + greenButtonNormalPath + "')");
        logInButton.setStyle("-fx-background-image: url(' " + greenButtonNormalPath + "')");

        String leaderBoardBack = Utils.getPath("chapter17_preview@2x.jpg");
        leaderBoardTable.setStyle("-fx-background-image: url(' " + leaderBoardBack + "')");
    }
    private void setAllActions(){
        logInButton.setOnAction(o -> {
            if(!userNameText.getText().isEmpty() && !passwordText.getText().isEmpty()) {
                try {
                    controller.loginGUI(userNameText.getText(), passwordText.getText());
                    userNameLabel.setText("username: " + controller.getCurrentAccount().getUserName());
                } catch (GameException e) {
                    errorLabel.setText(e.getMessage());
                    errorBox.setVisible(true);
                }
            }
        });
        accountButton.setOnAction(o->{
            if(!userNameText.getText().isEmpty() && !passwordText.getText().isEmpty()) {
                try {
                    controller.createAccountGUI(userNameText.getText(), passwordText.getText());
                }catch (GameException e){
                    errorLabel.setText(e.getMessage());
                    errorBox.setVisible(true);
                }
            }
                userNameText.setText("");
                passwordText.setText("");
        });
        exitErrorBox.setOnAction(e-> errorBox.setVisible(false));
        exitButton.setOnAction(e->{
            Runtime.getRuntime().exit(0);
        });
        logOutButton.setOnAction(o ->{
            try{
                controller.logout();
                userNameLabel.setText("");
                userNameText.setText("");
                passwordText.setText("");
            }catch (GameException e){
                errorLabel.setText(e.getMessage());
                errorBox.setVisible(true);
            }
        });
        leaderBoardButton.setOnAction(e -> {
            updateLeaderBoard();
            leaderBoardTable.setVisible(true);
        });
        leaderBoardTable.setOnKeyPressed(ke ->{
            KeyCode keyCode = ke.getCode();
            if (keyCode.equals(KeyCode.ESCAPE)) {
                leaderBoardTable.setVisible(false);
            }
        });
        mainMenuButton.setOnAction(o ->{
            if(controller.getCurrentAccount() != null) {
                menuManager.setCurrentMenu(loginPage.getMainMenu());
            }else{
                errorLabel.setText("please log in!");
                errorBox.setVisible(true);
            }
            userNameText.setText("");
            passwordText.setText("");
        });

        saveButton.setOnAction(o -> {
            try {
                controller.save();
            }catch (GameException e){
                errorLabel.setText(e.getMessage());
                errorBox.setVisible(true);
            }
        });

        setBlueButtonGlowOnMouseMoving(exitButton);
        setBlueButtonGlowOnMouseMoving(saveButton);
        setBlueButtonGlowOnMouseMoving(leaderBoardButton);
        setBlueButtonGlowOnMouseMoving(logOutButton);
        setBlueButtonGlowOnMouseMoving(mainMenuButton);
        setGreenButtonGlowOnMouseMoving(logInButton);
        setGreenButtonGlowOnMouseMoving(accountButton);

    }

    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    public void setLoginPage(LoginPage loginPage) {
        this.loginPage = loginPage;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void updateLeaderBoard(){
        ObservableList<Account> accounts = FXCollections.observableArrayList(Utils.getAccounts());

        TableColumn<Account, String> usernameColumn = new TableColumn<>("User Name");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<Account, Integer> winsColumn = new TableColumn<>("Wins");
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));

        leaderBoardTable.getItems().clear();
        leaderBoardTable.getColumns().clear();
        leaderBoardTable.setItems(accounts);
        leaderBoardTable.getColumns().addAll(usernameColumn, winsColumn);
    }
    private void setBlueButtonGlowOnMouseMoving(Button button){
        button.setOnMouseEntered(e -> {
            setButtonBackground(button,blueButtonGlowPath);
        });
        button.setOnMouseExited(e -> {
            setButtonBackground(button,blueButtonNormalPath);
        });
    }

    private void setGreenButtonGlowOnMouseMoving(Button button){
        button.setOnMouseEntered(e -> {
            setButtonBackground(button,greenButtonGlowPath);
        });
        button.setOnMouseExited(e -> {
            setButtonBackground(button,greenButtonNormalPath);
        });
    }
    private void setButtonBackground(Button button, String backgroundPath){
        button.setStyle("-fx-background-image: url(' " + backgroundPath + "')");
    }

}
