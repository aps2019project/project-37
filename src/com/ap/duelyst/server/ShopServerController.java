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
import java.util.ResourceBundle;

@SuppressWarnings({"Duplicates", "unchecked"})
public class ShopServerController implements Initializable {
    public TableView shopTable;
    private Shop shop;
    public ListView<HBox> usersList;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateShopTable();
        updateUsersList();
    }
    public void updateUsersList(){
        usersList.getItems().clear();
        for (Account onlineAccount : Server.getOnlineAccounts()) {
            HBox hBox = new HBox();
            Label label = new Label(onlineAccount.getUserName());
            label.setStyle("-fx-text-alignment: center");
            label.setStyle("-fx-font-size: 16");
            label.setStyle("-fx-alignment: center");
            label.setStyle("-fx-fill: rgba(0,0,0,0.65)");
            label.setStyle("-fx-text-fill: white");
            label.setStyle("-fx-background-color: transparent");
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
