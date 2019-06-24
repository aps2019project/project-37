package com.ap.duelyst.view.customize;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.buffs.*;
import com.ap.duelyst.model.buffs.traget.EffectType;
import com.ap.duelyst.model.buffs.traget.RangeType;
import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.traget.TargetType;
import com.ap.duelyst.model.cards.*;
import com.ap.duelyst.view.DialogController;
import com.ap.duelyst.view.card.CardSprite;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class CustomCardController implements Initializable {
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
    public VBox dialogContainer;
    public HBox dialog;
    public Label dialogText;
    private String fileName;
    private String effectFileName;
    private Controller controller;
    private DialogController dialogController;
    private EventHandler<ActionEvent> eventHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String back = Utils.getPath("chapter22_background@2x.jpg");
        root.setStyle("-fx-background-image: url('" + back + "')");
        cardType.getItems().addAll("hero", "minion", "spell");
        cardType.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    fileName = null;
                    effectFileName = null;
                    for (Node node : getAllNodes(root)) {
                        node.setDisable(false);
                    }
                    switch (newValue) {
                        case "hero":
                            targetType.setDisable(true);
                            mana.setDisable(true);
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
        buffDuration.getItems().addAll("1", "2", "3", "indefinite");
        buffDuration.setValue("1");
        buffType.getItems().addAll(
                "AttackBuff",
                "DisarmBuff",
                "DispelBuff",
                "HolyBuff",
                "ManaBuff",
                "PoisonBuff",
                "PowerBuff",
                "StunBuff",
                "WeaknessBuff"
        );
        buffType.setValue("AttackBuff");
        buffValue.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
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
        dialogController = new DialogController(root, dialog, dialogText,
                dialogContainer);
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
        VBox vBox = new VBox();
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
            Card card = cards.get(i);
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
                CustomCardController.this.fileName = card.getFileName();
                if (card instanceof Spell) {
                    CustomCardController.this.effectFileName =
                            ((Spell) card).getEffectFileName();
                }
                root.getChildren().remove(vBox);
            });
            hBox.getChildren().add(imageView);
        }
        if (hBox.getChildren().size() > 0) {
            listView.getItems().add(hBox);
        }
        vBox.getChildren().add(listView);
        vBox.setStyle("-fx-background-color: rgba(0,0,0,0.65)");
        vBox.setAlignment(Pos.CENTER);
        vBox.setFillWidth(false);
        VBox.setVgrow(listView, Priority.ALWAYS);
        vBox.setOnMouseClicked(event -> root.getChildren().remove(vBox));
        root.getChildren().add(vBox);
    }

    public void save() {
        Card card = null;
        String name = this.name.getText();
        if (name.isEmpty()) {
            dialogController.showDialog("name should not be empty");
            return;
        }
        try {
            Utils.getShop().getObjectByName(name);
            dialogController.showDialog("card exists");
            return;
        } catch (GameException ignored) {
        }
        if (fileName == null || fileName.isEmpty()) {
            dialogController.showDialog("sprite is not selected");
            return;
        }
        if (this.cost.getText().isEmpty()) {
            dialogController.showDialog("cost should not be empty");
            return;
        }
        switch (cardType.getValue()) {
            case "hero":
                if (ap.getText().isEmpty()) {
                    dialogController.showDialog("ap should not be empty");
                    return;
                }
                if (hp.getText().isEmpty()) {
                    dialogController.showDialog("hp should not be empty");
                    return;
                }
                if (range.getText().isEmpty()) {
                    dialogController.showDialog("range should not be empty");
                    return;
                }
                if (coolDown.getText().isEmpty()) {
                    dialogController.showDialog("coolDown should not be empty");
                    return;
                }
                Hero hero = new Hero(name, getValue(cost), getValue(hp), getValue(hp),
                        AttackType.valueOf(attackType.getValue().toUpperCase()),
                        getValue(range),
                        ((Spell) Utils.getShop().getObjectByName(specialPower.getValue())),
                        1, getValue(coolDown));
                hero.setFileName(fileName);
                card = hero;
                break;
            case "minion":
                if (ap.getText().isEmpty()) {
                    dialogController.showDialog("ap should not be empty");
                    return;
                }
                if (hp.getText().isEmpty()) {
                    dialogController.showDialog("hp should not be empty");
                    return;
                }
                if (range.getText().isEmpty()) {
                    dialogController.showDialog("range should not be empty");
                    return;
                }
                if (mana.getText().isEmpty()) {
                    dialogController.showDialog("coolDown should not be empty");
                    return;
                }
                Minion minion = new Minion(name, getValue(cost), getValue(mana),
                        getValue(hp),
                        getValue(hp),
                        AttackType.valueOf(attackType.getValue().toUpperCase()),
                        getValue(range),
                        ((Spell) Utils.getShop().getObjectByName(specialPower.getValue())),
                        ActivationTime.valueOf(activationTime.getValue()));
                minion.setFileName(fileName);
                card = minion;
                break;
            case "spell":
                if (mana.getText().isEmpty()) {
                    dialogController.showDialog("coolDown should not be empty");
                    return;
                }
                Buff buff = null;
                TargetType targetType = Arrays.stream(TargetType.values())
                        .filter(t -> t.getType().equals(this.targetType.getValue()))
                        .findFirst().orElse(TargetType.MINION);
                RangeType rangeType = RangeType.valueOf(this.rangeType.getValue());
                SideType sideType = SideType.valueOf(this.sideType.getValue());
                String val = buffDuration.getValue();
                int duration;
                if (val.equals("indefinite")) {
                    duration = Integer.MAX_VALUE;
                } else {
                    duration = Integer.parseInt(val);
                }
                int value = Integer.parseInt(this.buffValue.getValue());
                int mana = getValue(this.mana);
                switch (buffType.getValue()) {
                    case "AttackBuff":
                        buff = new AttackBuff(duration, false, targetType, sideType,
                                rangeType, value);
                        break;
                    case "DisarmBuff":
                        buff = new DisarmBuff(duration, false, targetType, sideType,
                                rangeType);
                        break;
                    case "DispelBuff":
                        buff = new DispelBuff(duration, false, targetType, sideType,
                                rangeType);
                        break;
                    case "HolyBuff":
                        buff = new HolyBuff(duration, false, targetType, sideType,
                                rangeType, value);
                        break;
                    case "ManaBuff":
                        buff = new ManaBuff(duration, false, targetType, sideType,
                                rangeType, value);
                        break;
                    case "PoisonBuff":
                        buff = new PoisonBuff(duration, false, targetType, sideType,
                                rangeType);
                        break;
                    case "PowerBuff":
                        buff = new PowerBuff(duration, false, targetType, sideType,
                                rangeType, EffectType.ATTACK_POWER, value);
                        break;
                    case "StunBuff":
                        buff = new StunBuff(duration, false, targetType, sideType,
                                rangeType);
                        break;
                    case "WeaknessBuff":
                        buff = new WeaknessBuff(duration, false, targetType, sideType,
                                rangeType, EffectType.ATTACK_POWER, value);
                        break;
                }
                Spell spell = new Spell(name, getValue(cost), mana, "", buff);
                spell.setFileName(fileName);
                spell.setEffectFileName(effectFileName);
                card = spell;
                break;
        }
        controller.addCustomCard(card);
        dialogController.showDialog("card added successfully");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    dialogController.hideDialog();
                    close();
                });
            }
        }, 2000);
    }

    private int getValue(TextField textField) {
        return Integer.parseInt(textField.getText());
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    private void close() {
        eventHandler.handle(null);
    }

    public void setEventHandler(EventHandler<ActionEvent> eventHandler) {
        this.eventHandler = eventHandler;
    }
}
