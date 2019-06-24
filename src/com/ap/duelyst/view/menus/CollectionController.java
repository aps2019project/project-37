package com.ap.duelyst.view.menus;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.CollectionMenu;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.controller.menu.ShopMenu;
import com.ap.duelyst.model.Deck;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class CollectionController implements Initializable {
    private MenuManager menuManager;
    private CollectionMenu collectionMenu;
    private Controller controller;
    private Deck currentDeck;
    public VBox mainBox;
    public VBox DeckBox;
    public ImageView gameLogo;
    public Button exitButton;
    public Button changeDeckButton;
    public Button selectDeckButton;
    public Button addToDeckButton;
    public Button removeFromDeckButton;
    public Button createNewDeckButton;
    public Button setAsMainDeckButton;
    public Button importDeckButton;
    public Button exportDeckButton;
    public Button exitErrorBox;
    public Label errorLabel;
    public VBox errorBox;
    public VBox newDeckBox;
    public TextField nameOfNewDeck;
    public Button submitNameOfNewDeck;
    public TableView<Deck> listOfDecks;
    public TableView<Card> collectionTable;
    public TableView<Card> deckTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();
    }
    private void setAllBackgrounds(){
        String rightButtonNormalPath = Utils.getPath("button_icon_right.png");
        String leftButtonNormalPath = Utils.getPath("button_icon_left.png");
        String rightButtonGlowPath = Utils.getPath("button_icon_right_glow.png");
        String leftButtonGlowPath = Utils.getPath("button_icon_left_glow.png");

        String greenButtonGlowPath = Utils.getPath("button_confirm.png");
        String greenButtonNormalPath = Utils.getPath("button_confirm_glow.png");
        String blueButtonNormalPath = Utils.getPath("button_primary.png");
        String blueButtonGlowPath = Utils.getPath("button_primary_glow.png");

        String back = Utils.getPath("chapter2_background@2x.jpg");
        mainBox.setStyle("-fx-background-image: url(' " + back + "')");
        mainBox.setId("mainBox");
        String backDeckBox = Utils.getPath("chapter23_preview@2x.jpg");
        DeckBox.setStyle("-fx-background-image: url(' " + backDeckBox + "')");
        DeckBox.setId("mainBox");
        String titleOfGame = Utils.getPath("game_logo.png");
        gameLogo.setImage(new Image(titleOfGame));
        errorLabel.setStyle("-fx-font-size: 14pt");

        setButtonBackground(addToDeckButton, rightButtonNormalPath);
        setButtonBackground(removeFromDeckButton, leftButtonNormalPath);
        setButtonBackground(createNewDeckButton, greenButtonNormalPath);
        setButtonBackground(changeDeckButton, greenButtonNormalPath);
        setButtonBackground(setAsMainDeckButton, greenButtonNormalPath);
        setButtonBackground(importDeckButton, blueButtonNormalPath);
        setButtonBackground(exportDeckButton, blueButtonNormalPath);
        setButtonBackground(exitButton, blueButtonNormalPath);

        setButtonGlowOnMouseMoving(addToDeckButton, rightButtonNormalPath, rightButtonGlowPath);
        setButtonGlowOnMouseMoving(removeFromDeckButton, leftButtonNormalPath, leftButtonGlowPath);
        setButtonGlowOnMouseMoving(createNewDeckButton, greenButtonNormalPath, greenButtonGlowPath);
        setButtonGlowOnMouseMoving(changeDeckButton, greenButtonNormalPath, greenButtonGlowPath);
        setButtonGlowOnMouseMoving(setAsMainDeckButton, greenButtonNormalPath, greenButtonGlowPath);
        setButtonGlowOnMouseMoving(importDeckButton, blueButtonNormalPath, blueButtonGlowPath);
        setButtonGlowOnMouseMoving(exportDeckButton, blueButtonNormalPath, blueButtonGlowPath);
        setButtonGlowOnMouseMoving(exitButton, blueButtonNormalPath, blueButtonGlowPath);

    }

    private void setAllActions(){
        exitButton.setOnAction(e->{
            menuManager.setCurrentMenu(collectionMenu.getParentMenu());
        });
        changeDeckButton.setOnAction(e ->{
            if(currentDeck == null){
                showNoDeck();
            }else{
                DeckBox.setVisible(true);
            }
        });
        addToDeckButton.setOnAction(o -> {
            if(currentDeck == null){
                showNoDeck();
            }else if(collectionTable.getSelectionModel().getSelectedItem() == null){
                showError("Please select a card from collection!");
            }else{
                Card selectedCard = collectionTable.getSelectionModel().getSelectedItem();
                try {
                    currentDeck.add(selectedCard);
                    collectionTable.getItems().remove(selectedCard);
                    deckTable.getItems().add(selectedCard);
                } catch (GameException e) {
                    errorLabel.setText(e.getMessage());
                    errorBox.setVisible(true);
                }
            }
        });
        removeFromDeckButton.setOnAction(o -> {
            if(currentDeck == null){
                showNoDeck();
            }else if(deckTable.getSelectionModel().getSelectedItem() == null){
                showError("Please select a card from deck!");
            }
            else {
                Card selectedCard = deckTable.getSelectionModel().getSelectedItem();
                try {
                    currentDeck.remove(selectedCard);
                    deckTable.getItems().remove(selectedCard);
                    collectionTable.getItems().add(selectedCard);
                } catch (GameException e) {
                    errorLabel.setText(e.getMessage());
                    errorBox.setVisible(true);
                }
            }
        });
        createNewDeckButton.setOnAction(o -> {
            newDeckBox.setVisible(true);
        });
        submitNameOfNewDeck.setOnAction(o -> {
            if(!nameOfNewDeck.getText().isEmpty()) {
                try {
                    controller.createDeck(nameOfNewDeck.getText());
                    currentDeck = controller.getCurrentAccount().getDeck(nameOfNewDeck.getText());
                    update();
                }catch (GameException e){
                    errorLabel.setText(e.getMessage());
                    errorBox.setVisible(true);
                }
            }
            nameOfNewDeck.setText("");
            newDeckBox.setVisible(false);
        });
        selectDeckButton.setOnAction(o -> {
            Deck selectedDeck = listOfDecks.getSelectionModel().getSelectedItem();
            if(selectedDeck != null){
                currentDeck = selectedDeck;
                update();
            }
            DeckBox.setVisible(false);
        });
        setAsMainDeckButton.setOnAction(o -> {
            try {
                controller.setMainDeck(currentDeck.getName());
            }catch (GameException e){
                errorLabel.setText(e.getMessage());
                errorBox.setVisible(true);
            }
        });
        exitErrorBox.setOnAction(o -> {
            errorBox.setVisible(false);
        });
    }
    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }
    public void setCollectionMenu(CollectionMenu collectionMenu) {
        this.collectionMenu = collectionMenu;
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void initializeCurrentDeck(){
        if(controller != null && controller.getCurrentAccount().getMainDeck()!=null){
            currentDeck = controller.getCurrentAccount().getMainDeck();
        }
    }
    public void update(){
        updateListOfDecks();
        updateDeckTable();
        updateCollectionTable();
    }
    public void updateListOfDecks(){
        ObservableList<Deck> decks = FXCollections.observableArrayList(controller.getCurrentAccount().getDecks());

        TableColumn<Deck, String> nameColumn = getDeckStringTableColumn("Name", "name");

        listOfDecks.getItems().clear();
        listOfDecks.getColumns().clear();
        listOfDecks.getItems().addAll(decks);
        listOfDecks.getColumns().addAll(nameColumn);

    }
    public void updateDeckTable(){
        if(currentDeck != null) {
            ObservableList<Card> cards = FXCollections.observableArrayList(currentDeck.getHeroMinions());
            makeTableView(cards, deckTable);
        }
    }
    public void updateCollectionTable(){
        ObservableList<Card> cards = FXCollections.observableArrayList(controller.getCurrentAccount().getCollection().getHeroMinions());
        cards.removeAll(deckTable.getItems());
        makeTableView(cards, collectionTable);

    }
    private void makeTableView(ObservableList<Card> cards, TableView tableView){
        cards.forEach(card -> {
            if (card.getCardSprite() == null) {
                card.makeCardSprite();
            }
            card.getCardSprite().play();
        });

        TableColumn<Card, ImageView> imageColumn = getCardImageTableColumn("Image", "imageView");

        TableColumn<Card, String> nameColumn = getCardStringTableColumn("Name","name");

        TableColumn<Card, String> idColumn = getCardStringTableColumn("ID", "id");

        tableView.getItems().clear();
        tableView.getColumns().clear();
        tableView.setItems(cards);
        tableView.getColumns().addAll(imageColumn, nameColumn, idColumn);
    }

    private TableColumn<Card, String> getCardStringTableColumn(String showName, String realName) {
        TableColumn<Card, String> column = new TableColumn<>(showName);
        column.setCellValueFactory(new PropertyValueFactory<>(realName));
        return column;
    }
    private TableColumn<Card, ImageView> getCardImageTableColumn(String showName, String realName) {
        TableColumn<Card, ImageView> column = new TableColumn<>("Image");
        column.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        return column;
    }
    private TableColumn<Deck, String> getDeckStringTableColumn(String showName, String realName) {
        TableColumn<Deck, String> column = new TableColumn<>(showName);
        column.setCellValueFactory(new PropertyValueFactory<>(realName));
        return column;
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
    private void showNoDeck(){
        errorLabel.setText("There is no deck, please create a new deck");
        errorBox.setVisible(true);
    }
    private void showError(String message){
        errorLabel.setText(message);
        errorBox.setVisible(true);
    }
}
