package com.ap.duelyst.view.menus;

import com.ap.duelyst.Command;
import com.ap.duelyst.Main;
import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.menu.BattleMenu;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.model.Deck;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.view.DialogController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class BattleMenuController implements Initializable {
    public TableView<StoryObject> storyTable;
    public ChoiceBox<String> customMode;
    public ChoiceBox<Integer> customFlagNumbers;
    public VBox dialogContainer;
    public HBox dialog;
    public Label dialogText;
    public HBox heroes;
    private MenuManager menuManager;
    private BattleMenu battleMenu;
    public StackPane root;
    public VBox playerModeBox;
    public VBox singlePlayerBox;
    public VBox multiPlayerBox;
    public VBox storyBox;
    public VBox customModeBox;
    private int customDeck = -1;

    private DialogController dialogController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String back = Utils.getPath("chapter10_background@2x.jpg");
        root.setStyle("-fx-background-image: url(' " + back + "')");
        dialogController = new DialogController(root, dialog, dialogText,
                dialogContainer);
    }

    public void update() {
        changeVisibility(playerModeBox);
        prepareStories();
        prepareCustom();
    }

    private void prepareStories() {
        ObservableList<StoryObject> storyObjects = FXCollections.observableArrayList();
        Hero first = createDeck(1).getHero();
        Hero second = createDeck(2).getHero();
        Hero third = createDeck(3).getHero();
        storyObjects.add(new StoryObject(500, first, "kill enemy hero", 0,
                BattleMenu.StoryLevel.ONE));
        storyObjects.add(new StoryObject(1000, second, "keep flags 8 rounds", 1,
                BattleMenu.StoryLevel.TWO));
        storyObjects.add(new StoryObject(1500, third, "collect half flags", 7,
                BattleMenu.StoryLevel.THREE));

        TableColumn<StoryObject, String> modeColumn = new TableColumn<>("mode");
        modeColumn.setCellValueFactory(new PropertyValueFactory<>("mode"));
        modeColumn.setMinWidth(300);

        TableColumn<StoryObject, VBox> heroColumn = new TableColumn<>("enemy hero");
        heroColumn.setCellValueFactory(new PropertyValueFactory<>("hero"));
        heroColumn.setMaxWidth(300);

        TableColumn<StoryObject, String> rewardColumn = new TableColumn<>("reward");
        rewardColumn.setCellValueFactory(new PropertyValueFactory<>("reward"));
        rewardColumn.setMinWidth(200);

        storyTable.setItems(storyObjects);
        storyTable.getColumns().clear();
        storyTable.getColumns().add(modeColumn);
        storyTable.getColumns().add(heroColumn);
        storyTable.getColumns().add(rewardColumn);
    }

    private void prepareCustom() {
        customMode.getItems().setAll(
                "kill enemy hero",
                "keep flags 8 rounds",
                "collect half flags");
        customMode.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.equals("collect half flags")) {
                        customFlagNumbers.setDisable(false);
                    } else {
                        customFlagNumbers.setDisable(true);
                    }
                });
        customMode.setValue(customMode.getItems().get(0));

        customFlagNumbers.getItems().addAll(5, 6, 7, 8, 9, 10);
        customFlagNumbers.setValue(7);

        heroes.getChildren().clear();
        customDeck = -1;
        for (int i = 1; i <= 3; i++) {
            Hero hero = createDeck(i).getHero();
            hero.makeCardSprite();
            hero.getCardSprite().play();
            ImageView imageView = hero.getImageView();
            Label name = new Label(hero.getName());
            name.setStyle("-fx-text-fill: white; -fx-font-weight: normal; " +
                    "-fx-font-size: 16");
            VBox vBox = new VBox(16, imageView, name);
            vBox.setMinHeight(220);
            vBox.setPadding(new Insets(0, 0, 48, 0));
            vBox.setAlignment(Pos.BOTTOM_CENTER);
            vBox.setCursor(Cursor.HAND);
            int finalI = i;
            vBox.setOnMouseClicked(event -> {
                for (Node child : heroes.getChildren()) {
                    child.setStyle("-fx-background-color: transparent");
                }
                customDeck = finalI;
                vBox.setStyle("-fx-background-color: rgba(31, 255, 45, 0.38)");
            });
            heroes.getChildren().add(vBox);
            HBox.setHgrow(vBox, Priority.ALWAYS);
        }

    }

    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    private Deck createDeck(Integer i) {
        Command command = new Command("createDeck", i);
        Main.writer.println(new Gson().toJson(command));
        JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                .getAsJsonObject();
        if (resp.get("resp") != null) {
            return Utils.getGson().fromJson(resp.get("resp").getAsString(), Deck.class);
        } else {
            dialogController.showDialog(resp.get("error").getAsString());
            return new Deck("a");
        }
    }

    public void setBattleMenu(BattleMenu battleMenu) {
        this.battleMenu = battleMenu;
    }

    public void showSinglePlayer() {
        battleMenu.setPlayMode(BattleMenu.PlayMode.SINGLE_PLAYER);
        changeVisibility(singlePlayerBox);
    }

    private void changeVisibility(VBox box) {
        playerModeBox.setVisible(false);
        singlePlayerBox.setVisible(false);
        multiPlayerBox.setVisible(false);
        storyBox.setVisible(false);
        customModeBox.setVisible(false);
        box.setVisible(true);
    }

    public void showMultiPlayer() {
        battleMenu.setPlayMode(BattleMenu.PlayMode.MULTI_PLAYER);
        changeVisibility(multiPlayerBox);
    }

    public void showStories() {
        changeVisibility(storyBox);
    }

    public void showCustomGame() {
        changeVisibility(customModeBox);
    }

    public void selectStory() {
        StoryObject object = storyTable.getSelectionModel().getSelectedItem();
        if (object == null) {
            dialogController.showDialog("nothing is selected");
        } else {
            Command command = new Command("createGame", object.getLevel(),
                    object.getFlag());
            startSinglePlayerGame(command);
        }
    }

    public void playCustom() {
        if (customDeck != -1) {
            BattleMenu.CustomGameMode gameMode = BattleMenu.CustomGameMode.getMode(
                    String.valueOf(customMode.getSelectionModel().getSelectedIndex() + 1));
            Command command = new Command("createGame", customDeck, gameMode,
                    customFlagNumbers.getValue());
            startSinglePlayerGame(command);
        } else {
            dialogController.showDialog("enemy is not selected");
        }
    }

    private void startSinglePlayerGame(Command command) {
        Main.writer.println(new Gson().toJson(command));
        JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                .getAsJsonObject();
        if (resp.get("resp") != null && resp.get("resp").getAsString().equals("null")) {
            resp = new JsonParser().parse(Main.scanner.nextLine()).getAsJsonObject();
        }
        if (resp.get("resp") != null) {
            menuManager.setCurrentMenu(battleMenu.getInGameBattleMenu());
        } else {
            dialogController.showDialog(resp.get("error").getAsString());
        }
    }

    public void exit() {
        menuManager.setCurrentMenu(battleMenu.getParentMenu());
    }

    public void back() {
        if (playerModeBox.isVisible()) {
            exit();
        } else if (singlePlayerBox.isVisible()) {
            changeVisibility(playerModeBox);
        } else if (multiPlayerBox.isVisible()) {
            changeVisibility(playerModeBox);
        } else if (storyBox.isVisible()) {
            showSinglePlayer();
        } else if (customModeBox.isVisible()) {
            showSinglePlayer();
        }
    }
}
