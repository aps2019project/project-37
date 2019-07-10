package com.ap.duelyst.view.menus;

import com.ap.duelyst.Command;
import com.ap.duelyst.Main;
import com.ap.duelyst.controller.Constants;
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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class BattleMenuController implements Initializable {
    public TextField messageField;
    public ListView<HBox> messageList;
    private static DataOutputStream writer;
    public TableView<StoryObject> storyTable;
    public ChoiceBox<String> customMode;
    public ChoiceBox<Integer> customFlagNumbers;
    public VBox dialogContainer;
    public HBox dialog;
    public Label dialogText;
    public HBox heroes;
    public ChoiceBox<String> multiMode;
    public StackPane loadingContainer;
    public ProgressIndicator loading;
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

    interface SearchListener {
        void success();

        void failure(String message);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Socket socket;
        try {
            socket = new Socket(Constants.ip, 8200);
            new ReaderHandler(socket, messageList).start();
            writer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        messageField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
        multiPlayerBox.setId("ChatRoomBox");
        String back = Utils.getPath("chapter10_background@2x.jpg");
        root.setStyle("-fx-background-image: url(' " + back + "')");
        dialogController = new DialogController(root, dialog, dialogText,
                dialogContainer);
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            try {
                writer.writeUTF(getUserName() + " : " + message);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Label label = new Label(message);
            label.setId("labelMessage2");
            HBox hBox = new HBox();
            hBox.getChildren().add(label);
            label.setAlignment(Pos.BASELINE_RIGHT);
            hBox.setAlignment(Pos.BASELINE_RIGHT);
            messageList.getItems().add(hBox);
            messageField.setText("");
        }
    }

    private String getUserName() {
        Command command = new Command("getUserName");
        Main.writer.println(new Gson().toJson(command));
        JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                .getAsJsonObject();
        return Utils.getGson().fromJson(resp.get("resp").getAsString(),
                new TypeToken<String>() {
                }.getType());
    }

    public void update() {
        changeVisibility(playerModeBox);
        prepareStories();
        prepareCustom();
        multiMode.getItems().setAll(
                "kill enemy hero",
                "keep flags 8 rounds",
                "collect half flags");
        multiMode.getSelectionModel().select(0);
        loadingContainer.setVisible(false);
        loading.setProgress(-1);
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

    public void playMulti() {
        BattleMenu.CustomGameMode mode = BattleMenu.CustomGameMode.getMode(
                String.valueOf(multiMode.getSelectionModel().getSelectedIndex() + 1));
        Command command = new Command("searchForOpponent", mode);
        Main.writer.println(new Gson().toJson(command));
        loadingContainer.setVisible(true);
        new SearchHandler(new SearchListener() {
            @Override
            public void success() {
                menuManager.setCurrentMenu(battleMenu.getInGameBattleMenu());
            }

            @Override
            public void failure(String message) {
                dialogController.showDialog(message);
            }
        }).start();
    }

    public void cancelSearch() {
        loadingContainer.setVisible(false);
    }
}


class ReaderHandler extends Thread {
    private Socket socket;
    private ListView<HBox> messageList;

    ReaderHandler(Socket socket, ListView<HBox> messageList) {
        this.socket = socket;
        this.messageList = messageList;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            DataInputStream reader = new DataInputStream(socket.getInputStream());
            String message;
            while (true) {
                message = reader.readUTF();
                String finalMessage = message;
                Platform.runLater(() -> {
                    Label label = new Label(finalMessage);
                    label.setId("labelMessage1");
                    HBox hBox = new HBox();
                    hBox.getChildren().add(label);
                    label.setAlignment(Pos.BASELINE_LEFT);
                    hBox.setAlignment(Pos.BASELINE_LEFT);
                    messageList.getItems().add(hBox);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class SearchHandler extends Thread {
    private BattleMenuController.SearchListener listener;

    SearchHandler(BattleMenuController.SearchListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (true) {
            JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                    .getAsJsonObject();
            if (resp.get("resp") != null) {
                if (listener != null) {
                    Platform.runLater(() -> listener.success());
                }
                break;
            } else if (resp.get("error") != null) {
                if (listener != null) {
                    Platform.runLater(() -> listener.failure(resp.get("error").getAsString()));
                }
            }
        }
    }
}

