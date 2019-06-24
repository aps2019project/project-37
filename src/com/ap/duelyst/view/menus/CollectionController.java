package com.ap.duelyst.view.menus;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.CollectionMenu;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.model.Deck;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.items.UsableItem;
import com.ap.duelyst.view.DialogController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("unchecked")
public class CollectionController implements Initializable {
    public StackPane root;
    public VBox dialogContainer;
    public HBox dialog;
    public Label dialogText;
    public ListView<String> deckView;
    private MenuManager menuManager;
    private CollectionMenu collectionMenu;
    private Controller controller;
    private Deck currentDeck;
    public VBox mainBox;
    public VBox selectDeckBox;
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
    public VBox importDeckBox;
    public Button importButton;
    public TextField nameOfImportDeck;
    public Label errorLabel;
    public VBox errorBox;
    public VBox newDeckBox;
    public TextField nameOfNewDeck;
    public Button submitNameOfNewDeck;
    public TableView collectionTable;
    public TableView deckTable;
    private DialogController dialogController;
    private ObservableList<Deck> decks;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();
        newDeckBox.setOnMouseClicked(event -> newDeckBox.setVisible(false));
        importDeckBox.setOnMouseClicked(event -> importDeckBox.setVisible(false));
        selectDeckBox.setOnMouseClicked(event -> selectDeckBox.setVisible(false));
        dialogController = new DialogController(root, dialog, dialogText,
                dialogContainer);
    }

    private void setAllBackgrounds() {
        String rightButtonNormalPath = Utils.getPath("button_icon_right.png");
        String leftButtonNormalPath = Utils.getPath("button_icon_left.png");
        String rightButtonGlowPath = Utils.getPath("button_icon_right_glow.png");
        String leftButtonGlowPath = Utils.getPath("button_icon_left_glow.png");

        String greenButtonGlowPath = Utils.getPath("button_confirm_glow.png");
        String greenButtonNormalPath = Utils.getPath("button_confirm.png");
        String blueButtonNormalPath = Utils.getPath("button_primary.png");
        String blueButtonGlowPath = Utils.getPath("button_primary_glow.png");

        String back = Utils.getPath("chapter2_background@2x.jpg");
        mainBox.setStyle("-fx-background-image: url(' " + back + "')");
        mainBox.setId("mainBox");
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

        setButtonGlowOnMouseMoving(addToDeckButton, rightButtonNormalPath,
                rightButtonGlowPath);
        setButtonGlowOnMouseMoving(removeFromDeckButton, leftButtonNormalPath,
                leftButtonGlowPath);
        setButtonGlowOnMouseMoving(createNewDeckButton, greenButtonNormalPath,
                greenButtonGlowPath);
        setButtonGlowOnMouseMoving(changeDeckButton, greenButtonNormalPath,
                greenButtonGlowPath);
        setButtonGlowOnMouseMoving(setAsMainDeckButton, greenButtonNormalPath,
                greenButtonGlowPath);
        setButtonGlowOnMouseMoving(importDeckButton, blueButtonNormalPath,
                blueButtonGlowPath);
        setButtonGlowOnMouseMoving(exportDeckButton, blueButtonNormalPath,
                blueButtonGlowPath);
        setButtonGlowOnMouseMoving(exitButton, blueButtonNormalPath, blueButtonGlowPath);

    }

    private void setAllActions() {
        exitButton.setOnAction(e -> {
            menuManager.setCurrentMenu(collectionMenu.getParentMenu());
        });
        changeDeckButton.setOnAction(e -> {
            if (controller.getCurrentAccount().getDecks().isEmpty()) {
                showNoDeck();
            } else {
                selectDeckBox.setVisible(true);
            }
        });
        addToDeckButton.setOnAction(o -> {
            if (currentDeck == null) {
                showNoDeck();
            } else if (collectionTable.getSelectionModel().getSelectedItem() == null) {
                showError("Please select a card from collection!");
            } else {
                Object selectedCard =
                        collectionTable.getSelectionModel().getSelectedItem();
                try {
                    currentDeck.add(selectedCard);
                    collectionTable.getItems().remove(selectedCard);
                    deckTable.getItems().add(selectedCard);
                } catch (GameException e) {
                    errorLabel.setText(e.getMessage());
//                    errorBox.setVisible(true);
                    dialogController.showDialog(e.getMessage());
                }
            }
        });
        removeFromDeckButton.setOnAction(o -> {
            if (currentDeck == null) {
                showNoDeck();
            } else if (deckTable.getSelectionModel().getSelectedItem() == null) {
                showError("Please select a card from deck!");
            } else {
                Object selectedCard = deckTable.getSelectionModel().getSelectedItem();
                try {
                    currentDeck.remove(selectedCard);
                    deckTable.getItems().remove(selectedCard);
                    collectionTable.getItems().add(selectedCard);
                } catch (GameException e) {
                    errorLabel.setText(e.getMessage());
//                    errorBox.setVisible(true);
                    dialogController.showDialog(e.getMessage());
                }
            }
        });
        createNewDeckButton.setOnAction(o -> {
            newDeckBox.setVisible(true);
        });
        submitNameOfNewDeck.setOnAction(o -> {
            if (!nameOfNewDeck.getText().isEmpty()) {
                try {
                    controller.createDeck(nameOfNewDeck.getText());
                    currentDeck =
                            controller.getCurrentAccount().getDeck(nameOfNewDeck.getText());
                    updateListOfDecks();
                } catch (GameException e) {
                    errorLabel.setText(e.getMessage());
//                    errorBox.setVisible(true);
                    dialogController.showDialog(e.getMessage());
                }
            }
            nameOfNewDeck.setText("");
            newDeckBox.setVisible(false);
        });
        selectDeckButton.setOnAction(o -> {
            String name = deckView.getSelectionModel().getSelectedItem();
            if (name != null) {
                currentDeck = decks.filtered(deck -> deck.getName().equals(name)).get(0);
                update();
            }
            deckView.getSelectionModel().clearSelection();
            selectDeckBox.setVisible(false);
        });
        setAsMainDeckButton.setOnAction(o -> {
            if (currentDeck == null) {
                showError("you dont have any selected deck");
            } else {
                try {
                    controller.setMainDeck(currentDeck.getName());
                    showError("current deck selected as main deck");
                } catch (GameException e) {
                    errorLabel.setText(e.getMessage());
//                errorBox.setVisible(true);
                    dialogController.showDialog(e.getMessage());
                }
            }
        });
        exitErrorBox.setOnAction(o -> {
            errorBox.setVisible(false);
        });
        exportDeckButton.setOnAction(o -> {
            if (currentDeck != null) {
                controller.exportDeck(currentDeck);
                dialogController.showDialog("deck exported successfully");
            } else {
                showError("Please select a deck to export!");
            }
        });
        importDeckButton.setOnAction(o -> {
            importDeckBox.setVisible(true);
        });
        importButton.setOnAction(o -> {
            Deck deck = controller.importDeck(nameOfImportDeck.getText());
            if (deck != null) {
                if (controller.getCurrentAccount().hasDeck(deck.getName())) {
                    showError("You already have that deck!");
                } else {
                    controller.getCurrentAccount().getDecks().add(deck);
                    showError(deck.getName() + " is added successfully!");
                }
            } else {
                showError("There is no deck with this name");
            }
            nameOfImportDeck.setText("");
            importDeckBox.setVisible(false);
            updateListOfDecks();
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

    public void update() {
        updateListOfDecks();
        updateDeckTable();
        updateCollectionTable();
    }

    public void loadMainDeckOnTable() {
        if (controller.getCurrentAccount().getMainDeck() != null) {
            currentDeck = controller.getCurrentAccount().getMainDeck();
        } else {
            currentDeck = null;
        }
    }

    public void updateListOfDecks() {
        decks = FXCollections.observableArrayList(controller.getCurrentAccount().getDecks());
        deckView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        deckView.getItems().clear();
        for (Deck deck : decks) {
            deckView.getItems().add(deck.getName());
        }
    }

    public void updateDeckTable() {
        if (currentDeck != null) {
            ObservableList cards = FXCollections.observableArrayList();
            cards.addAll(currentDeck.getCards());
            cards.addAll(currentDeck.getUsableItems());
            makeTableView(cards, deckTable);
        } else {
            makeTableView(FXCollections.observableArrayList(), deckTable);
        }
    }

    public void updateCollectionTable() {
        ObservableList cards = FXCollections.observableArrayList();
        cards.addAll(controller.getCurrentAccount().getCollection().getCards());
        cards.addAll(controller.getCurrentAccount().getCollection().getUsableItems());
        cards.removeIf(o -> {
            String id = "";
            if (o instanceof Card) {
                id = ((Card) o).getId();
            } else if (o instanceof UsableItem) {
                id = ((UsableItem) o).getId();
            }
            try {
                currentDeck.getObjectById(id);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
        makeTableView(cards, collectionTable);

    }

    private void makeTableView(ObservableList cards, TableView tableView) {
        for (Object o : cards) {
            ShopController.playSprite(o);
        }

        TableColumn<?, ImageView> imageColumn = getCardImageTableColumn();

        TableColumn<?, String> nameColumn = getCardStringTableColumn("Name", "name");

        TableColumn<?, String> idColumn = getCardStringTableColumn("ID", "id");

        tableView.getItems().clear();
        tableView.getColumns().clear();
        tableView.setItems(cards);
        tableView.getColumns().addAll(imageColumn, nameColumn, idColumn);
    }

    private TableColumn<?, String> getCardStringTableColumn(String showName,
                                                            String realName) {
        TableColumn<?, String> column = new TableColumn<>(showName);
        column.setCellValueFactory(new PropertyValueFactory<>(realName));
        return column;
    }

    private TableColumn<?, ImageView> getCardImageTableColumn() {
        TableColumn<?, ImageView> column = new TableColumn<>("Image");
        column.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        return column;
    }

    private TableColumn<Deck, String> getDeckStringTableColumn(String showName,
                                                               String realName) {
        TableColumn<Deck, String> column = new TableColumn<>(showName);
        column.setCellValueFactory(new PropertyValueFactory<>(realName));
        return column;
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

    private void showNoDeck() {
        errorLabel.setText("There is no deck, please create a new deck");
//        errorBox.setVisible(true);
        dialogController.showDialog("There is no deck, please create a new deck");
    }

    private void showError(String message) {
        errorLabel.setText(message);
//        errorBox.setVisible(true);
        dialogController.showDialog(message);
    }
}
