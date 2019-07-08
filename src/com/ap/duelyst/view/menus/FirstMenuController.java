package com.ap.duelyst.view.menus;

import com.ap.duelyst.Command;
import com.ap.duelyst.Main;
import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.LoginPage;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.view.DialogController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
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
import java.util.List;
import java.util.ResourceBundle;

public class FirstMenuController implements Initializable {
    public VBox dialogContainer;
    public HBox dialog;
    public Label dialogText;
    public VBox leadBoardContainer;
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
    public TableView<Account> leaderBoardTable;
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
    private DialogController dialogController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();
        dialogController = new DialogController(stackPane, dialog, dialogText,
                dialogContainer);

    }

    private void setAllBackgrounds() {
        this.greenButtonNormalPath = Utils.getPath("button_confirm.png");
        this.greenButtonNormalPath = Utils.getPath("button_confirm.png");
        this.greenButtonGlowPath = Utils.getPath("button_confirm_glow.png");

        this.blueButtonNormalPath = Utils.getPath("button_secondary.png");
        this.blueButtonGlowPath = Utils.getPath("button_secondary_glow.png");

        String back = Utils.getPath("chapter18_background@2x.jpg");
        stackPane.setStyle("-fx-background-image: url(' " + back + "')");
        stackPane.setId("stackPane");

        setStyle(gameLogo, leaderBoardButton, blueButtonNormalPath, logOutButton,
                exitButton, saveButton, null);
        mainMenuButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");

        accountButton.setStyle("-fx-background-image: url(' " + greenButtonNormalPath + "')");
        logInButton.setStyle("-fx-background-image: url(' " + greenButtonNormalPath +
                "')");
    }

    static void setStyle(ImageView gameLogo, Button leaderBoardButton,
                         String blueButtonNormalPath, Button logOutButton,
                         Button exitButton, Button saveButton, Button shopButton) {
        String titleOfGame = Utils.getPath("game_logo.png");
        gameLogo.setImage(new Image(titleOfGame));

        leaderBoardButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");
        logOutButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath +
                "')");
        exitButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");
        saveButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");
        if (shopButton != null) {
            shopButton.setStyle("-fx-background-image: url(' " + blueButtonNormalPath + "')");
        }
    }

    private void setAllActions() {
        logInButton.setOnAction(o -> {
            if (!userNameText.getText().isEmpty() && !passwordText.getText().isEmpty()) {
                try {
                    Command command = new Command("loginGUI",
                            userNameText.getText(), passwordText.getText());
                    Main.writer.println(new Gson().toJson(command));
                    JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                            .getAsJsonObject();
                    if (resp.get("token") != null) {
                        Main.token = resp.get("token").getAsString();
                        dialogController.showDialog("login successful");
                        Main.userName = userNameText.getText();
                        userNameLabel.setText("username: " + Main.userName);
                    }
                    if (resp.get("error") != null) {
                        dialogController.showDialog(resp.get("error").getAsString());
                    }
                } catch (JsonSyntaxException e) {
                    dialogController.showDialog(e.getMessage());
                }
                userNameText.setText("");
                passwordText.setText("");
            } else {
                dialogController.showDialog("fill all fields");
            }
        });
        accountButton.setOnAction(o -> {
            if (!userNameText.getText().isEmpty() && !passwordText.getText().isEmpty()) {
                try {
                    Command command = new Command("createAccountGUI",
                            userNameText.getText(), passwordText.getText());
                    Main.writer.println(new Gson().toJson(command));
                    JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                            .getAsJsonObject();
                    if (resp.get("error") != null) {
                        dialogController.showDialog(resp.get("error").getAsString());
                    } else {
                        dialogController.showDialog(resp.get("resp").getAsString());
                    }
                } catch (JsonSyntaxException e) {
                    dialogController.showDialog(e.getMessage());
                }
                userNameText.setText("");
                passwordText.setText("");
            } else {
                dialogController.showDialog("fill all fields");
            }
        });
        exitButton.setOnAction(e -> {
            Runtime.getRuntime().exit(0);
        });
        logOutButton.setOnAction(o -> {
            try {
                Command command = new Command("logout");
                Main.writer.println(new Gson().toJson(command));
                JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                        .getAsJsonObject();
                if (resp.get("error") != null) {
                    dialogController.showDialog(resp.get("error").getAsString());
                } else {
                    userNameLabel.setText("");
                    userNameText.setText("");
                    passwordText.setText("");
                    dialogController.showDialog(resp.get("resp").getAsString());
                    Main.userName = null;
                    Main.token = null;
                }
            } catch (Exception e) {
                dialogController.showDialog(e.getMessage());
            }
        });
        leaderBoardButton.setOnAction(e -> {
            updateLeaderBoard();
        });
        leadBoardContainer.setOnKeyPressed(ke -> {
            KeyCode keyCode = ke.getCode();
            if (keyCode.equals(KeyCode.ESCAPE)) {
                leadBoardContainer.setVisible(false);
            }
        });
        leadBoardContainer.setOnMouseClicked(event -> leadBoardContainer.setVisible(false));
        mainMenuButton.setOnAction(o -> {
            if (Main.userName != null) {
                menuManager.setCurrentMenu(loginPage.getMainMenu());
            } else {
                dialogController.showDialog("please log in");
            }
            userNameText.setText("");
            passwordText.setText("");
        });

        saveButton.setOnAction(o -> {
            try {
                Command command = new Command("save");
                Main.writer.println(new Gson().toJson(command));
                JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                        .getAsJsonObject();
                if (resp.get("resp") != null) {
                    dialogController.showDialog(resp.get("resp").getAsString());
                } else {
                    dialogController.showDialog(resp.get("error").getAsString());
                }

            } catch (Exception e) {
                dialogController.showDialog(e.getMessage());
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

    public void updateLeaderBoard() {
        ObservableList<Account> accounts = FXCollections.observableArrayList();
        Command command = new Command("getAllAccounts");
        Main.writer.println(new Gson().toJson(command));
        JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                .getAsJsonObject();
        if (resp.get("error") != null) {
            dialogController.showDialog(resp.get("error").getAsString());
            return;
        } else {
            List<Account> accounts1 =
                    Utils.getGson().fromJson(resp.get("resp").getAsString(),
                            new TypeToken<List<Account>>() {
                            }.getType());
            accounts.addAll(accounts1);
        }
        TableColumn<Account, String> usernameColumn = new TableColumn<>("User Name");
        usernameColumn.setMinWidth(150);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<Account, Integer> winsColumn = new TableColumn<>("Wins");
        winsColumn.setMinWidth(150);
        winsColumn.setCellValueFactory(new PropertyValueFactory<>("wins"));
        leaderBoardTable.getColumns().clear();
        leaderBoardTable.setItems(accounts);
        leaderBoardTable.getColumns().add(usernameColumn);
        leaderBoardTable.getColumns().add(winsColumn);
        leadBoardContainer.setVisible(true);

    }

    private void setBlueButtonGlowOnMouseMoving(Button button) {
        button.setOnMouseEntered(e -> {
            setButtonBackground(button, blueButtonGlowPath);
        });
        button.setOnMouseExited(e -> {
            setButtonBackground(button, blueButtonNormalPath);
        });
    }

    private void setGreenButtonGlowOnMouseMoving(Button button) {
        button.setOnMouseEntered(e -> {
            setButtonBackground(button, greenButtonGlowPath);
        });
        button.setOnMouseExited(e -> {
            setButtonBackground(button, greenButtonNormalPath);
        });
    }

    private void setButtonBackground(Button button, String backgroundPath) {
        button.setStyle("-fx-background-image: url(' " + backgroundPath + "')");
    }

}
