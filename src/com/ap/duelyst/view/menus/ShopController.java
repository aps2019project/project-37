package com.ap.duelyst.view.menus;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.controller.menu.ShopMenu;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
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
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ShopController implements Initializable {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();
    }
    private void setAllBackgrounds(){
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
        setButtonGlowOnMouseMoving(exitButton, greenButtonNormalPath, greenButtonNormalPath);

    }

    private void setAllActions(){
        exitButton.setOnAction(e->{
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
    public void update(){
        account = controller.getCurrentAccount();
        usernameLabel.setText("username : " + controller.getCurrentAccount().getUserName());
        updateShopTable();
        updateCollectionTable();
        updateRemainingMoney();
    }
    public void updateShopTable(){
        ObservableList<Card> shopCards = FXCollections.observableArrayList(Utils.getShop().getHeroMinions());
        //shopCards.remove(0,40);
        makeTableView(shopCards,shopTable);

    }
    public void updateCollectionTable() {
        ObservableList<Card> collectionCards = FXCollections.observableArrayList(controller.getCurrentAccount().getCollection().getHeroMinions());
        makeTableView(collectionCards,collectionTable);
    }
    public void updateRemainingMoney(){
         remainingMoney.setText("Remaining money: "+ account.getBudget());
    }
    private void makeTableView(ObservableList<Card> cards, TableView tableView){
        cards.forEach(card -> {
            if (card.getCardSprite() == null) {
                card.makeCardSprite();
            }
            card.getCardSprite().play();
        });

        TableColumn<Card, Integer> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));

        TableColumn<Card, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Card, Integer> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        tableView.getItems().clear();
        tableView.getColumns().clear();
        tableView.setItems(cards);
        tableView.getColumns().addAll(imageColumn, nameColumn, priceColumn);
    }
    public void buyButtonClicked() {
        Card selectedCard = (Card) shopTable.getSelectionModel().getSelectedItem();
        Card newCard = null;
        try {
            newCard = (Card) controller.buyAndReturn(selectedCard.getName());
        }
        catch (GameException e){
            errorLabel.setText(e.getMessage());
            errorBox.setVisible(true);
        }
        collectionTable.getItems().add(newCard);
        updateRemainingMoney();
    }
    public void sellButtonClicked() {
        Card selectedCard = (Card) collectionTable.getSelectionModel().getSelectedItem();
        try {
            controller.sell(selectedCard.getId());
        }
        catch (GameException e){
            errorLabel.setText(e.getMessage());
            errorBox.setVisible(true);
        }
        collectionTable.getItems().remove(selectedCard);
        updateRemainingMoney();
    }

    private void setButtonGlowOnMouseMoving(Button button, String normalPath, String glowPath){
        button.setOnMouseEntered(e -> {
            setButtonBackground(button,glowPath);
        });
        button.setOnMouseExited(e -> {
            setButtonBackground(button,normalPath);
        });
    }
    private void setButtonBackground(Button button, String backgroundPath){
        button.setStyle("-fx-background-image: url(' " + backgroundPath + "')");
    }
}
