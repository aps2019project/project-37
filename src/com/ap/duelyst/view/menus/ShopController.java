package com.ap.duelyst.view.menus;

import com.ap.duelyst.Command;
import com.ap.duelyst.Main;
import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.controller.menu.ShopMenu;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Collection;
import com.ap.duelyst.model.Shop;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.model.items.Item;
import com.ap.duelyst.model.items.UsableItem;
import com.ap.duelyst.view.DialogController;
import com.ap.duelyst.view.card.CardSprite;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ShopController implements Initializable {
    public VBox dialogContainer;
    public HBox dialog;
    public Label dialogText;
    public StackPane root;
    private MenuManager menuManager;
    private ShopMenu shopMenu;
    public VBox mainBox;
    public ImageView gameLogo;
    public TableView shopTable;
    public TableView collectionTable;
    public Button buyButton;
    public Button sellButton;
    public Button exitButton;
    public Label usernameLabel;
    public Label remainingMoney;
    public VBox errorBox;
    public Label errorLabel;
    public Button exitErrorBox;
    private DialogController dialogController;
    private Shop shop;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();
        dialogController = new DialogController(root, dialog, dialogText,
                dialogContainer);
    }

    private void setAllBackgrounds() {
        String greenButtonGlowPath = Utils.getPath("button_confirm.png");
        String greenButtonNormalPath = Utils.getPath("button_confirm_glow.png");
        String blueButtonNormalPath = Utils.getPath("button_primary_middle.png");
        String blueButtonGlowPath = Utils.getPath("button_primary_middle_glow.png");

        String back = Utils.getPath("chapter4_background@2x.jpg");
        mainBox.setStyle("-fx-background-image: url(' " + back + "')");
        mainBox.setId("mainBox");
        String titleOfGame = Utils.getPath("game_logo.png");
        gameLogo.setImage(new Image(titleOfGame));
        usernameLabel.setId("username");
        remainingMoney.setStyle("-fx-font-size: 12pt");
        errorLabel.setStyle("-fx-font-size: 14pt");

        setButtonBackground(sellButton, blueButtonNormalPath);
        setButtonBackground(buyButton, blueButtonNormalPath);
        setButtonBackground(exitButton, greenButtonNormalPath);

        setButtonGlowOnMouseMoving(sellButton, blueButtonNormalPath, blueButtonGlowPath);
        setButtonGlowOnMouseMoving(buyButton, blueButtonNormalPath, blueButtonGlowPath);
        setButtonGlowOnMouseMoving(exitButton, greenButtonNormalPath,
                greenButtonNormalPath);

    }

    private void setAllActions() {
        exitButton.setOnAction(e -> {
            menuManager.setCurrentMenu(shopMenu.getParentMenu());
        });
        exitErrorBox.setOnAction(e -> {
            errorBox.setVisible(false);
        });
        buyButton.setOnAction(o -> buyButtonClicked());
        sellButton.setOnAction(o -> sellButtonClicked());
    }

    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    public void setShopMenu(ShopMenu shopMenu) {
        this.shopMenu = shopMenu;
    }

    public void update() {
        usernameLabel.setText("username : " + Main.userName);
        updateShopTable();
        updateCollectionTable();
        updateRemainingMoney();
    }

    public void updateShopTable() {
        Command command = new Command("getShop");
        Main.writer.println(new Gson().toJson(command));
        JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                .getAsJsonObject();
        if (resp.get("resp") != null) {
            Shop shop = Utils.getGson().fromJson(resp.get("resp").getAsString(),
                    Shop.class);

            shop.getCards().forEach(card -> {
                if (this.shop != null) {
                    this.shop.getCards().stream()
                            .filter(o -> o.getName().equals(card.getName()))
                            .map(Card::getCardSprite).findFirst()
                            .ifPresent(card::setCardSprite);
                }
            });
            shop.getUsableItems().forEach(usableItem -> {
                if (this.shop != null) {
                    this.shop.getUsableItems().stream()
                            .filter(o -> o.getName().equals(usableItem.getName()))
                            .map(UsableItem::getCardSprite).findFirst()
                            .ifPresent(usableItem::setCardSprite);
                }
            });
            ObservableList shopCards =
                    FXCollections.observableArrayList(shop.getCards());
            shopCards.addAll(shop.getUsableItems());
            makeTableView(shopCards, shopTable, true);
            this.shop = shop;
        } else {
            dialogController.showDialog(resp.get("error").getAsString());
        }
    }

    public void updateCollectionTable() {
        ObservableList cards = FXCollections.observableArrayList();
        Command command = new Command("getCollection");
        Main.writer.println(new Gson().toJson(command));
        JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                .getAsJsonObject();
        if (resp.get("resp") != null) {
            Collection collection =
                    Utils.getGson().fromJson(resp.get("resp").getAsString(),
                            Collection.class);
            cards.addAll(collection.getUsableItems());
            cards.addAll(collection.getCards());
            makeTableView(cards, collectionTable, false);

        } else {
            dialogController.showDialog(resp.get("error").getAsString());
        }
    }

    public void updateRemainingMoney() {
        Command command = new Command("getMoney");
        Main.writer.println(new Gson().toJson(command));
        JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                .getAsJsonObject();
        if (resp.get("resp") != null) {
            remainingMoney.setText("Remaining money: " + resp.get("resp").getAsString());
        } else {
            dialogController.showDialog(resp.get("error").getAsString());
        }
        ScaleTransition transition = new ScaleTransition(Duration.millis(200),
                remainingMoney);
        transition.setCycleCount(2);
        transition.setAutoReverse(true);
        transition.setToX(1.3);
        transition.setToY(1.3);
        transition.play();
    }

    private void makeTableView(ObservableList cards, TableView tableView,
                               boolean hasCount) {
        for (Object o : cards) {
            playSprite(o);
        }

        TableColumn<?, Integer> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));

        TableColumn<?, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<?, Integer> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        tableView.getItems().clear();
        tableView.getColumns().clear();
        tableView.setItems(cards);
        tableView.getColumns().addAll(imageColumn, nameColumn, priceColumn);
        if (hasCount) {
            TableColumn<?, Integer> countColumn = new TableColumn<>("Count");
            countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
            tableView.getColumns().add(countColumn);
        }

    }

    public void buyButtonClicked() {
        Object selectedCard = shopTable.getSelectionModel().getSelectedItem();
        if (selectedCard == null) {
            dialogController.showDialog("select an item");
            return;
        }
        try {
            Command command = new Command("buyAndReturn",
                    Utils.getGson().toJson(selectedCard),
                    selectedCard.getClass().getName());
            Main.writer.println(new Gson().toJson(command));
            JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                    .getAsJsonObject();
            if (resp.get("resp") != null) {
                Object newCard =
                        Utils.getGson().fromJson(resp.get("resp").getAsString(),
                                selectedCard.getClass());
                playSprite(newCard);
                collectionTable.getItems().add(newCard);
                updateShopTable();
                updateRemainingMoney();
            } else {
                dialogController.showDialog(resp.get("error").getAsString());
            }
        } catch (Exception e) {
            dialogController.showDialog(e.getMessage());
        }
    }

    static void playSprite(Object o) {
        if (o instanceof Card) {
            Card card = (Card) o;
            if ((card.getCardSprite() == null)) {
                card.makeCardSprite();
            }
            card.getCardSprite().play();
        } else {
            Item item = (Item) o;
            if ((item.getCardSprite() == null)) {
                item.makeSprite();
            }
            item.getCardSprite().play();
        }
    }

    public void sellButtonClicked() {
        Object selectedCard = collectionTable.getSelectionModel().getSelectedItem();
        if (selectedCard == null) {
            dialogController.showDialog("select an item");
            return;
        }
        try {
            Command command = new Command("sellGUI",
                    Utils.getGson().toJson(selectedCard),
                    selectedCard.getClass().getName());
            Main.writer.println(new Gson().toJson(command));
            JsonObject resp = new JsonParser().parse(Main.scanner.nextLine())
                    .getAsJsonObject();
            if (resp.get("resp") != null) {
                collectionTable.getItems().remove(selectedCard);
                updateRemainingMoney();
                updateShopTable();
                updateRemainingMoney();
            } else {
                dialogController.showDialog(resp.get("error").getAsString());
            }
        } catch (Exception e) {
            dialogController.showDialog(e.getMessage());
        }
    }

    private void setButtonGlowOnMouseMoving(Button button, String normalPath,
                                            String glowPath) {
        button.setOnMouseEntered(e -> {
            setButtonBackground(button, glowPath);
        });
        button.setOnMouseExited(e -> {
            setButtonBackground(button, normalPath);
        });
    }

    private void setButtonBackground(Button button, String backgroundPath) {
        button.setStyle("-fx-background-image: url(' " + backgroundPath + "')");
    }
}
