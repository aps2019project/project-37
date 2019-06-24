package com.ap.duelyst.view.menus;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.controller.menu.ShopMenu;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.items.Item;
import com.ap.duelyst.view.DialogController;
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
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
public class ShopController implements Initializable {
    public VBox dialogContainer;
    public HBox dialog;
    public Label dialogText;
    public StackPane root;
    private Controller controller;
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
    private Account account;
    private DialogController dialogController;

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

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void update() {
        account = controller.getCurrentAccount();
        usernameLabel.setText("username : " + controller.getCurrentAccount().getUserName());
        updateShopTable();
        updateCollectionTable();
        updateRemainingMoney();
    }

    public void updateShopTable() {
        ObservableList shopCards =
                FXCollections.observableArrayList(Utils.getShop().getCards());
        shopCards.addAll(Utils.getShop().getUsableItems());
        makeTableView(shopCards, shopTable);

    }

    public void updateCollectionTable() {
        ObservableList cards = FXCollections.observableArrayList();
        cards.addAll(controller.getCurrentAccount().getCollection().getUsableItems());
        cards.addAll(controller.getCurrentAccount().getCollection().getCards());
        makeTableView(cards, collectionTable);
    }

    public void updateRemainingMoney() {
        remainingMoney.setText("Remaining money: " + account.getBudget());
        ScaleTransition transition=new ScaleTransition(Duration.millis(200),remainingMoney);
        transition.setCycleCount(2);
        transition.setAutoReverse(true);
        transition.setToX(1.3);
        transition.setToY(1.3);
        transition.play();
    }

    private void makeTableView(ObservableList cards, TableView tableView) {
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
    }

    public void buyButtonClicked() {
        Object selectedCard = shopTable.getSelectionModel().getSelectedItem();
        if (selectedCard == null) {
            dialogController.showDialog("select an item");
            return;
        }
        try {
            Object newCard = controller.buyAndReturn(selectedCard);
            playSprite(newCard);
            collectionTable.getItems().add(newCard);
            updateRemainingMoney();
        } catch (GameException e) {
            errorLabel.setText(e.getMessage());
//            errorBox.setVisible(true);
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
            controller.sellGUI(selectedCard);
            collectionTable.getItems().remove(selectedCard);
            updateRemainingMoney();
        } catch (GameException e) {
            errorLabel.setText(e.getMessage());
//            errorBox.setVisible(true);
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
