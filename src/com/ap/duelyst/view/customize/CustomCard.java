package com.ap.duelyst.view.customize;

import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.buffs.traget.RangeType;
import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.traget.TargetType;
import com.ap.duelyst.model.cards.ActivationTime;
import com.ap.duelyst.model.cards.AttackType;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Spell;
import com.ap.duelyst.view.card.CardSprite;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CustomCard implements Initializable {
    public TextField name;
    public StackPane root;
    public ChoiceBox<String> cardType;
    public ChoiceBox<String> targetType;
    public ChoiceBox<String> rangeType;
    public ChoiceBox<String> sideType;
    public TextField ap;
    public ChoiceBox<String> attackType;
    public TextField range;
    public TextField hp;
    public ChoiceBox<String> specialPower;
    public TextField mana;
    public ChoiceBox<String> activationTime;
    public TextField coolDown;
    public TextField cost;
    public ChoiceBox<String> buffType;
    public ChoiceBox<String> buffValue;
    public ChoiceBox<String> buffDuration;
    private String fileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String back = Utils.getPath("chapter22_background@2x.jpg");
        root.setStyle("-fx-background-image: url('" + back + "')");
        cardType.getItems().addAll("hero", "minion", "spell");
        cardType.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    for (Node node : getAllNodes(root)) {
                        node.setDisable(false);
                    }
                    switch (newValue) {
                        case "hero":
                            targetType.setDisable(true);
                            rangeType.setDisable(true);
                            sideType.setDisable(true);
                            buffDuration.setDisable(true);
                            buffType.setDisable(true);
                            buffValue.setDisable(true);
                            activationTime.setDisable(true);
                            break;
                        case "minion":
                            targetType.setDisable(true);
                            rangeType.setDisable(true);
                            sideType.setDisable(true);
                            buffDuration.setDisable(true);
                            buffType.setDisable(true);
                            buffValue.setDisable(true);
                            coolDown.setDisable(true);
                            break;
                        case "spell":
                            ap.setDisable(true);
                            hp.setDisable(true);
                            coolDown.setDisable(true);
                            activationTime.setDisable(true);
                            attackType.setDisable(true);
                            range.setDisable(true);
                            specialPower.setDisable(true);
                            break;
                    }
                });
        cardType.setValue("hero");
        targetType.getItems().addAll(Arrays.stream(TargetType.values()).map(TargetType::getType).collect(Collectors.toList()));
        targetType.setValue(TargetType.HERO.getType());
        rangeType.getItems().addAll(Arrays.stream(RangeType.values()).map(Enum::name).collect(Collectors.toList()));
        rangeType.setValue(RangeType.ONE.name());
        sideType.getItems().addAll(Arrays.stream(SideType.values()).map(Enum::name).collect(Collectors.toList()));
        sideType.setValue(SideType.ALLY.name());

        buffDuration.getItems().addAll("1","2","3","indefinite");
        buffDuration.setValue("1");
        buffType.getItems().addAll(
                "AttackBuff",
                "CellBuff",
                "DisarmBuff",
                "DispelBuff",
                "HistoryBuff",
                "HolyBuff",
                "KingsGuardBuff",
                "ManaBuff",
                "PoisonBuff",
                "PowerBuff",
                "StunBuff",
                "WeaknessBuff"
        );
        buffType.setValue("AttackBuff");
        buffValue.getItems().addAll("1","2","3","4","5","6","7","8","9","10");
        buffValue.setValue("1");
        attackType.getItems().addAll(Arrays.stream(AttackType.values()).map(AttackType::toString).collect(Collectors.toList()));
        attackType.setValue(AttackType.MELEE.toString());
        specialPower.getItems().addAll(Utils.getShop().getCards().stream().filter(card -> card instanceof Spell).map(Card::getName).collect(Collectors.toList()));
        specialPower.setValue(((Spell) Utils.getShop().getObjectByName("hellfire")).getName());
        activationTime.getItems().addAll(Arrays.stream(ActivationTime.values()).map(ActivationTime::name).collect(Collectors.toList()));
        activationTime.setValue(ActivationTime.PASSIVE.name());
        addNumberLimitation(ap);
        addNumberLimitation(hp);
        addNumberLimitation(range);
        addNumberLimitation(mana);
        addNumberLimitation(coolDown);
        addNumberLimitation(cost);

    }

    private void addNumberLimitation(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*\\D.*")) {
                textField.setText(oldValue);
            }
        });
    }

    public static List<Node> getAllNodes(Parent root) {
        List<Node> nodes = new ArrayList<>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, List<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent) {
                addAllDescendents((Parent) node, nodes);
            }
        }
    }

    public void showSprites() {
        List<Card> cards = new ArrayList<>();
        if (cardType.getValue().equals("spell")) {
            cards.addAll(Utils.getShop().getCards().stream()
                    .filter(card -> card instanceof Spell).collect(Collectors.toList()));
        } else {
            cards.addAll(Utils.getShop().getHeroMinions());
        }
        ListView<HBox> listView = new ListView<>();
        listView.setMinWidth(850);
        HBox hBox = new HBox();
        for (int i = 0; i < cards.size(); i++) {
            Card card=cards.get(i);
            if (i != 0 && i % 4 == 0) {
                listView.getItems().add(hBox);
                hBox = new HBox();
            }
            CardSprite sprite = card.getCardSprite();
            if (sprite == null) {
                card.makeCardSprite();
                sprite = card.getCardSprite();
            }
            sprite.play();
            ImageView imageView = sprite.getImageView();
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);

            imageView.setOnMouseClicked(event -> {
                CustomCard.this.fileName = card.getFileName();
                root.getChildren().remove(1);
            });
            hBox.getChildren().add(imageView);
        }
        if (hBox.getChildren().size() > 0) {
            listView.getItems().add(hBox);
        }
        VBox vBox = new VBox(listView);
        vBox.setStyle("-fx-background-color: rgba(0,0,0,0.65)");
        vBox.setAlignment(Pos.CENTER);
        vBox.setFillWidth(false);
        VBox.setVgrow(listView, Priority.ALWAYS);
        vBox.setOnMouseClicked(event -> root.getChildren().remove(vBox));
        root.getChildren().add(vBox);
    }

    public void save() {
        //todo
    }
}
