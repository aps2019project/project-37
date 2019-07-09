package com.ap.duelyst.server;

import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Shop;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.items.UsableItem;
import com.ap.duelyst.view.menus.ShopController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings({"Duplicates", "unchecked"})
public class ShopServerController implements Initializable {
    public TableView shopTable;
    private Shop shop;
    public ListView<HBox> usersList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateShopTable();
        updateUsersList(new ArrayList<>());
    }

    public void updateUsersList(List<String> names) {
        usersList.getItems().clear();
        List<Account> accounts = new ArrayList<>(Utils.getAccounts());
        for (String name : names) {
            accounts.removeIf(account -> account.userNameEquals(name));
            HBox hBox = new HBox();
            Label label = new Label(name);
            label.setStyle("-fx-text-alignment: center;-fx-font-size: 18;" +
                    "-fx-alignment: center;" +
                    "-fx-text-fill: #15ff0a;" +
                    "-fx-background-color: transparent;");
            hBox.setStyle("-fx-alignment: center");
            hBox.getChildren().add(label);
            usersList.getItems().add(hBox);
        }
        for (Account account : accounts) {
            HBox hBox = new HBox();
            Label label = new Label(account.getUserName());
            label.setStyle("-fx-text-alignment: center;-fx-font-size: 18;" +
                    "-fx-alignment: center;" +
                    "-fx-text-fill: #ff0900;" +
                    "-fx-background-color: transparent;");
            hBox.setStyle("-fx-alignment: center");
            hBox.getChildren().add(label);
            usersList.getItems().add(hBox);
        }
    }

    public void updateShopTable() {
        Shop shop = Utils.getShop();

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
        makeTableView(shopCards, shopTable);
        this.shop = shop;
    }

    private void makeTableView(ObservableList cards, TableView tableView) {
        for (Object o : cards) {
            ShopController.playSprite(o);
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

        TableColumn<?, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        tableView.getColumns().add(countColumn);
    }


}
