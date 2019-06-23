package com.ap.duelyst.view.battle;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.model.cards.Minion;
import com.ap.duelyst.model.cards.Spell;
import com.ap.duelyst.model.game.Game;
import com.ap.duelyst.model.game.Player;
import com.ap.duelyst.view.GameEvents;
import com.ap.duelyst.view.card.CardSprite;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class BattleController implements Initializable {
    public HBox manaBox;
    public VBox p1Box;
    public Label p1Name;
    public VBox p2Box;
    public Label p2Name;
    public HBox p1ManaBox;
    public HBox p2ManaBox;
    public VBox heroDialogCard;
    public Label heroDesc;
    public Label heroName;
    public Label heroType;
    public Label heroAP;
    public Label heroHP;
    public Button turnButton;
    private Game game;
    public StackPane root;
    public HBox handContainer;
    public GridPane board;
    private List<StackPane> handContainers = new ArrayList<>();
    private String circle = Utils.getPath("card_background@2x.png");
    private String circleDisable = Utils.getPath("card_background_disabled@2x.png");
    private String circleHighlight = Utils.getPath("card_background_highlight@2x.png");
    private String manaActivePath = Utils.getPath("icon_mana@2x.png");
    private Image manaActive = new Image(manaActivePath);
    private String manaInactivePath = Utils.getPath("icon_mana_inactive@2x.png");
    private Image manaInactive = new Image(manaInactivePath);
    private String manaDisablePath = Utils.getPath("icon_mana_disable@2x.png");
    private String ap = "-fx-background-image: url(' " + Utils.getPath("icon_atk@2x" +
            ".png") + "')";
    private String apBW = "-fx-background-image: url(' " + Utils.getPath("icon_atk_bw" +
            "@2x.png") + "')";
    private String hp =
            "-fx-background-image: url(' " + Utils.getPath("icon_hp@2x.png") + "')";
    private String hpBW = "-fx-background-image: url(' " + Utils.getPath("icon_hp_bw@2x" +
            ".png") + "')";
    private String turn = Utils.getPath("button_end_turn_mine@2x.png");
    private String turnGlow = Utils.getPath("button_end_turn_mine_glow@2x.png");
    private String turnEnemy = Utils.getPath("button_end_turn_enemy@2x.png");
    private double xStart, yStart;
    private Card insertableCard;
    private StackPane container;
    private Account account;
    private boolean first = true;
    private Hero selectedCard;
    private Hero hoveredCard;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String back = Utils.getPath("chapter1_background@2x.jpg");
        root.setStyle("-fx-background-image: url(' " + back + "')");
        root.setOnMouseDragged(event -> {
            if (xStart != 0) {
                for (Node child : board.getChildren()) {
                    if (!child.isDisable()) {
                        if (child.localToScene(child.getBoundsInLocal())
                                .contains(event.getSceneX(), event.getSceneY())) {
                            child.setStyle("-fx-background-color: rgba(0,255,11,0.38)");
                        } else {
                            child.setStyle("-fx-background-color: rgba(255,240,0,0.38)");
                        }
                    }
                }
                insertableCard.getCardSprite().getImageView()
                        .setTranslateX(event.getSceneX() - xStart);
                insertableCard.getCardSprite().getImageView()
                        .setTranslateY(event.getSceneY() - yStart - 20);
            }
        });
        root.setOnMouseReleased(event -> {
            xStart = 0;
            yStart = 0;
            if (insertableCard != null) {
                for (Node child : board.getChildren()) {
                    if (child.getStyle().equals("-fx-background-color: rgba(0,255,11,0" +
                            ".38)")) {
                        game.insert(insertableCard, GridPane.getRowIndex(child)
                                , GridPane.getColumnIndex(child));
                        insertableCard = null;
                        updateBoard(game.getInBoardCards(), true);
                        updateHand();
                        break;
                    }
                }
                if (insertableCard != null) {
                    updateBoard(game.getInBoardCards(), true);
                    TranslateTransition transition =
                            new TranslateTransition(Duration.millis(300),
                                    insertableCard.getCardSprite().getImageView());
                    transition.setToX(0);
                    transition.setToY(-30);
                    transition.play();
                    container.setStyle("-fx-background-image: url('" + circle + "')");
                }

            }
        });
        root.setOnMouseMoved(event -> {
            boolean shouldHide = true;
            for (Node child : board.getChildren()) {
                if (child.localToScene(child.getBoundsInLocal())
                        .contains(event.getSceneX(), event.getSceneY())) {
                    Hero card = game.getCardAt(GridPane.getRowIndex(child),
                            GridPane.getColumnIndex(child));
                    if (card != null) {
                        shouldHide = false;
                        if (card != hoveredCard) {
                            hoveredCard = card;
                            showDialog(true);
                        }
                        break;
                    }
                }
            }
            for (int i = 0; i < handContainers.size(); i++) {
                StackPane child = handContainers.get(i);
                if (child.localToScene(child.getBoundsInLocal())
                        .contains(event.getSceneX(), event.getSceneY())) {
                    List<Card> hand = new ArrayList<>();
                    for (Player player : game.getPlayers()) {
                        if (player.getAccountName().equals(account.getUserName())) {
                            hand = player.getHand();
                            break;
                        }
                    }
                    if (hand.size() > i && hand.get(i) instanceof Hero) {
                        Hero card = (Hero) hand.get(i);
                        shouldHide = false;
                        if (card != hoveredCard) {
                            hoveredCard = card;
                            Bounds bounds = child.localToScene(child.getBoundsInLocal());
                            heroDialogCard.setTranslateY(bounds.getMinY() - heroDialogCard.getHeight());
                            heroDialogCard.setTranslateX(bounds.getMinX() + bounds.getWidth() / 2 - heroDialogCard.getWidth() / 2);
                            showDialog(false);
                        }
                        break;
                    }
                }
            }
            if (shouldHide) {
                hideDialog();
            }
        });
        prepareHeroDialogCard();
        prepareHand();
        prepareBoard();
        prepareMana();
        turnButton.setText("     End Turn     ");
        turnButton.prefHeightProperty().bind(turnButton.widthProperty().divide(3.07));
        turnButton.setPadding(new Insets(16));
        turnButton.getStyleClass().add("hand-container");
        turnButton.setStyle("-fx-background-image: url('" + turn + "')");
    }

    private void showDialog(boolean showTop) {
        if (showTop) {
            VBox box = p2Box;
            if (hoveredCard.getAccountName().equals(account.getUserName())) {
                box = p1Box;
            }
            heroDialogCard
                    .setTranslateX(box.getLayoutX() + box.getWidth() / 2 - heroDialogCard.getWidth() / 2);
            heroDialogCard
                    .setTranslateY(manaBox.localToScene(manaBox.getBoundsInLocal()).getMaxY());
        }
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300),
                heroDialogCard);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        CardSprite sprite = hoveredCard.getCardSprite().clone();
        sprite.play();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> ((VBox) heroDialogCard.getChildren().get(0))
                        .getChildren().set(0, sprite.getImageView()));
            }
        }, 100);
        heroAP.setText(String.valueOf(hoveredCard.getAttackPowerInGame()));
        heroHP.setText(String.valueOf(hoveredCard.getHealthPointInGame()));
        heroName.setText(hoveredCard.getName().replaceAll("-", " "));
        heroType.setText(hoveredCard.getClass().getSimpleName().toLowerCase());
        if (hoveredCard.getSpecialPower() != null) {
            heroDesc.setText(hoveredCard.getSpecialPower().getDesc());
        } else {
            heroDesc.setText("no special power");
        }
    }

    private void hideDialog() {
        if (hoveredCard == null) {
            return;
        }
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300),
                heroDialogCard);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
        hoveredCard = null;
    }

    private void prepareHeroDialogCard() {
        heroDialogCard.getStyleClass().add("hero-dialog");
        heroDialogCard.setOpacity(0);
        String back = Utils.getPath("neutral_unit@2x.png");
        heroDialogCard.setStyle("-fx-background-image: url('" + back + "')");
    }

    private void prepareMana() {
        manaBox.prefWidthProperty().bind(root.widthProperty());
        for (int i = 0; i < 9; i++) {
            ImageView imageView = new ImageView(manaInactive);
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            p1ManaBox.getChildren().add(imageView);
            imageView = new ImageView(manaInactive);
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);

            p2ManaBox.setOpacity(.7);
            p2ManaBox.getChildren().add(imageView);
        }
    }

    private void prepareBoard() {
        board.prefWidthProperty().bind(root.widthProperty().multiply(.6));
        board.setVgap(8);
        board.setHgap(8);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                StackPane pane = new StackPane();
                pane.setAlignment(Pos.TOP_CENTER);
                pane.prefWidthProperty().bind(board.prefWidthProperty().subtract(72)
                        .divide(9));
                pane.prefHeightProperty().bind(pane.prefWidthProperty());
                pane.getStyleClass().add("board-cell");
                pane.setDisable(true);
                int finalI = i;
                int finalJ = j;
                pane.setOnMouseClicked(event -> {
                    hideDialog();
                    if (first) {
                        selectedCard = game.getCardAt(finalI, finalJ);
                        if (event.getButton() == MouseButton.PRIMARY) {
                            if (selectedCard.getInGame().isMoved()
                                    || !selectedCard.getInGame().isMovable()) {
                                return;
                            }
                            first = false;
                            for (Node child : board.getChildren()) {
                                child.setDisable(true);
                            }
                            pane.setDisable(false);
                            for (int[] ints : game.getCellsInRange(finalI, finalJ, 2)) {
                                if (game.isEmpty(ints[0], ints[1])
                                        && game.checkRoad(selectedCard.getX(),
                                        selectedCard.getY(), ints[0], ints[1])) {
                                    StackPane child = getNodeByRowColumnIndex(ints[0],
                                            ints[1]);
                                    child.setDisable(false);
                                    child.setStyle("-fx-background-color: rgba(255,240," +
                                            "0,0" +
                                            ".38)");
                                }
                            }
                        }
                        if (event.getButton() == MouseButton.SECONDARY) {
                            if (selectedCard.getInGame().isAttacked()) {
                                return;
                            }
                            for (Node child : board.getChildren()) {
                                child.setDisable(true);
                            }
                            pane.setDisable(false);
                            List<int[]> pos = new ArrayList<>();
                            switch (selectedCard.getAttackType()) {
                                case MELEE:
                                    pos = game.getNeighbours(selectedCard.getX(),
                                            selectedCard.getY());
                                    break;
                                case RANGED:
                                    pos = game.getCellsInRange(selectedCard.getX(),
                                            selectedCard.getY(), selectedCard.getRange());
                                    for (int[] neighbour :
                                            game.getNeighbours(selectedCard.getX(),
                                                    selectedCard.getY())) {
                                        int index = pos.indexOf(neighbour);
                                        if (index >= 0) {
                                            pos.remove(index);
                                        }
                                    }
                                    break;
                                case HYBRID:
                                    pos = game.getCellsInRange(selectedCard.getX(),
                                            selectedCard.getY(), selectedCard.getRange());
                                    break;
                            }
                            for (int[] p : pos) {
                                Hero card = game.getCardAt(p[0], p[1]);
                                if (card != null && !card.getAccountName()
                                        .equals(account.getUserName())) {
                                    getNodeByRowColumnIndex(p[0], p[1]).setDisable(false);
                                    getNodeByRowColumnIndex(p[0], p[1])
                                            .setStyle("-fx-background-color: rgba(" +
                                                    "15,64,255,0.38)");
                                    first = false;
                                }
                            }
                            if (first){
                                updateBoard(game.getInBoardCards(),true);
                            }
                        }
                    } else {
                        first = true;
                        Hero card = game.getCardAt(finalI, finalJ);
                        if (card != null
                                && !card.getAccountName().equals(account.getUserName())) {
                            StackPane pane1 = getNodeByRowColumnIndex(selectedCard.getX(),
                                    selectedCard.getY());
                            StackPane pane2 = getNodeByRowColumnIndex(card.getX(),
                                    card.getY());
                            List<CardSprite> result = game.attack(selectedCard,
                                    card.getId(), true);
                            selectedCard.getCardSprite().showAttack();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> {
                                        selectedCard.getCardSprite().showBreathing();
                                        updateAPHP(pane2, card, true);
                                        card.getCardSprite().showAttack();
                                    });
                                }
                            }, 500);
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(() -> {
                                        card.getCardSprite().showBreathing();
                                        updateAPHP(pane1, selectedCard, true);
                                        showDeath(result);
                                    });
                                }
                            }, 1000);
                        } else if (selectedCard != card) {
                            int oldX = selectedCard.getX();
                            int oldY = selectedCard.getY();
                            game.move(selectedCard, finalI, finalJ);
                            updateBoard(game.getInBoardCards(), false);
                            selectedCard.getCardSprite().showRun();
                            TranslateTransition transition =
                                    new TranslateTransition(Duration.seconds(1),
                                            selectedCard.getCardSprite().getImageView());
                            StackPane oldP = getNodeByRowColumnIndex(oldX, oldY);
                            StackPane newP = getNodeByRowColumnIndex(finalI, finalJ);
                            transition.setByX(newP.getLayoutX() - oldP.getLayoutX());
                            transition.setByY(newP.getLayoutY() - oldP.getLayoutY());
                            transition.play();
                            transition.setOnFinished(e -> {
                                selectedCard.getCardSprite().showBreathing();
                                updateBoard(game.getInBoardCards(), true);
                            });
                        } else {
                            updateBoard(game.getInBoardCards(), true);
                        }
                    }

                });
                HBox hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                Region region = new Pane();
                hBox.getChildren().addAll(createLabel(), region, createLabel());
                HBox.setHgrow(region, Priority.ALWAYS);
                VBox vBox = new VBox(hBox);
                addBoxLayer(pane, vBox);
                board.add(pane, j, i);
            }
        }
    }

    private void showDeath(List<CardSprite> result) {
        updateBoard(
                game.getInBoardCards(),
                true);
        for (CardSprite cardSprite : result) {
            if (cardSprite != null) {
                cardSprite.showDeath();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            for (Node child : board.getChildren()) {
                                ((StackPane) child).getChildren()
                                        .removeIf(node -> node == cardSprite.getImageView());
                            }
                        });
                    }
                }, 1000);
            }
        }
    }

    private Label createLabel() {
        Label label = new Label();
        label.setAlignment(Pos.CENTER);
        label.setMinSize(25, 25);
        label.setPadding(new Insets(4, 0, 4, 0));
        label.getStyleClass().add("hp-ap-label");
        return label;
    }

    private void addBoxLayer(StackPane pane, VBox vBox) {
        vBox.setAlignment(Pos.BOTTOM_CENTER);
        vBox.setPadding(new Insets(0, 8, 4, 8));
        vBox.prefWidthProperty().bind(pane.prefWidthProperty());
        vBox.prefHeightProperty().bind(pane.prefHeightProperty());
        pane.getChildren().add(vBox);
    }

    private void prepareHand() {
        handContainer.prefWidthProperty().bind(root.widthProperty().multiply(.6));
        handContainer.prefHeightProperty().bind(root.heightProperty().multiply(.15));
        for (int i = 0; i < 5; i++) {
            StackPane pane = new StackPane();
            pane.setAlignment(Pos.CENTER);
            pane.getStyleClass().add("hand-container");
            pane.setDisable(true);
            pane.setStyle("-fx-background-image: url('" + circleDisable + "')");
            pane.prefWidthProperty().bind(handContainer.prefHeightProperty());
            Label label = new Label();
            label.setMinSize(35, 35);
            label.setPadding(new Insets(4, 0, 4, 0));
            label.setAlignment(Pos.CENTER);
            label.getStyleClass().add("mana-label");
            label.setStyle("-fx-background-image: url('" + manaDisablePath + "')");
            VBox vBox = new VBox(label);
            addBoxLayer(pane, vBox);
            handContainers.add(pane);
        }
        handContainer.getChildren().addAll(handContainers);
    }

    private StackPane getNodeByRowColumnIndex(final int row, final int column) {
        Node result = null;
        ObservableList<Node> children = board.getChildren();

        for (Node node : children) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return (StackPane) result;
    }

    public void setGame(Controller controller) {
        if (this.game == null) {
            this.game = controller.getGame();
            p1Name.setText(game.getPlayers().get(0).getAccountName());
            p2Name.setText(game.getPlayers().get(1).getAccountName());
            this.account = controller.getCurrentAccount();
            this.game.setEvents(new GameEvents() {
                @Override
                public void nextRound(List<Hero> inGameCards) {
                    updateHand();
                    updateBoard(inGameCards, true);
                    if (game.getCurrentPlayer().getAccountName().equals(account.getUserName())) {
                        turnButton.setDisable(false);
                        turnButton.setText("     End Turn     ");
                        turnButton.setStyle("-fx-background-image: url('" + turn + "')");
                        turnButton.setOnMousePressed(event -> turnButton
                                .setStyle("-fx-background-image: url('" + turnGlow +
                                        "')"));
                        turnButton.setOnMouseReleased(event -> turnButton
                                .setStyle("-fx-background-image: url('" + turn + "')"));
                    } else {
                        turnButton.setText("    Enemy Turn    ");
                        turnButton.setOnMouseReleased(event -> turnButton
                                .setStyle("-fx-background-image: url('" + turnEnemy + "')"));
                        turnButton.setDisable(true);
                    }

                }
            });
            this.game.startGame();
        }
    }

    private void updateMana() {
        fillMana(p1ManaBox, game.getPlayers().get(0).getMana());
        fillMana(p2ManaBox, game.getPlayers().get(1).getMana());
    }

    private void fillMana(HBox box, int amount) {
        ObservableList<Node> children = box.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node child = children.get(i);
            ImageView imageView = (ImageView) child;
            if (i < amount) {
                imageView.setImage(manaActive);
            } else {
                imageView.setImage(manaInactive);
            }
        }
    }

    private void updateHand() {
        updateMana();
        List<Card> hand = new ArrayList<>();
        Player p = null;
        for (Player player : game.getPlayers()) {
            if (player.getAccountName().equals(account.getUserName())) {
                hand = player.getHand();
                p = player;
                break;
            }
        }
        for (int i = 0; i < 5; i++) {
            StackPane pane = handContainers.get(i);
            Label label = (Label) ((VBox) pane.getChildren()
                    .filtered(node -> node instanceof VBox).get(0)).getChildren().get(0);
            label.setText("");
            label.setStyle("-fx-background-image: url('" + manaDisablePath + "')");
            pane.setStyle("-fx-background-image: url('" + circleDisable + "')");
            pane.setDisable(true);
        }
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card instanceof Hero) {
                if (card.getCardSprite() == null) {
                    card.makeCardSprite();
                    card.getCardSprite().play();
                }
            }
            StackPane pane = handContainers.get(i);
            Label label = (Label) ((VBox) pane.getChildren()
                    .filtered(node -> node instanceof VBox).get(0)).getChildren().get(0);
            if (game.getCurrentPlayer().getAccountName().equals(account.getUserName())) {
                pane.setStyle("-fx-background-image: url('" + circle + "')");
                pane.setDisable(false);
                label.setStyle("-fx-background-image: url('" + manaActivePath + "')");
                if (card instanceof Minion) {
                    if (p.getMana() < ((Minion) card).getMana()) {
                        pane.setStyle("-fx-background-image: url('" + circleDisable +
                                "')");
                        pane.setDisable(true);
                        label.setStyle("-fx-background-image: url('" + manaDisablePath + "')");
                    }
                    label.setText(String.valueOf(((Minion) card).getMana()));
                }
                if (card instanceof Spell) {
                    if (p.getMana() < ((Spell) card).getMana()) {
                        pane.setStyle("-fx-background-image: url('" + circleDisable +
                                "')");
                        pane.setDisable(true);
                        label.setStyle("-fx-background-image: url('" + manaDisablePath + "')");
                    }
                    label.setText(String.valueOf(((Spell) card).getMana()));
                }
            }
            ImageView imageView = card.getCardSprite().getImageView();
            imageView.fitWidthProperty().bind(pane.prefWidthProperty());
            imageView.fitHeightProperty().bind(pane.prefHeightProperty());
            pane.setOnDragDetected(event -> {
                hideDialog();
                showPossibleTargets();
                xStart = event.getSceneX();
                yStart = event.getSceneY();
                insertableCard = card;
                container = pane;
                container.setStyle("-fx-background-image: url('" + circleHighlight +
                        "')");
            });
            pane.getChildren().removeIf(child -> child instanceof ImageView);
            pane.getChildren().add(imageView);
            pane.getChildren().filtered(node -> node instanceof VBox).get(0).toFront();
        }
    }

    private void updateBoard(List<Hero> inGameCards, boolean updatePosition) {
        for (Node child : board.getChildren()) {
            child.setStyle("");
            child.setDisable(true);
            VBox vBox = (VBox) ((StackPane) child).getChildren()
                    .filtered(node -> node instanceof VBox).get(0);
            HBox hBox = (HBox) vBox.getChildren().get(0);
            Label hp = ((Label) hBox.getChildren().get(2));
            Label ap = ((Label) hBox.getChildren().get(0));
            ap.setText("");
            hp.setText("");
            ap.setStyle("");
            hp.setStyle("");
        }
        for (Hero hero : inGameCards) {
            if (hero.getCardSprite() == null) {
                hero.makeCardSprite();
                hero.getCardSprite().play();
            }
            StackPane pane = getNodeByRowColumnIndex(hero.getX(),
                    hero.getY());
            if (pane != null) {
                ImageView imageView = hero.getCardSprite().getImageView();
                if (!hero.getAccountName().equals(account.getUserName())) {
                    imageView.setRotationAxis(Rotate.Y_AXIS);
                    imageView.setRotate(180);
                    pane.setStyle("-fx-background-color: rgba(255,3,0,0" +
                            ".38)");
                    pane.setDisable(true);
                } else if (game.getCurrentPlayer().getAccountName().equals(account.getUserName())) {
                    pane.setDisable(false);
                } else {
                    pane.setDisable(true);
                }
                if (updatePosition) {
                    updateCardPos(pane, imageView, hero);
                }
            }
        }
    }

    private void updateCardPos(StackPane pane, ImageView imageView, Hero hero) {
        imageView.fitWidthProperty().bind(pane.prefWidthProperty());
        imageView.fitHeightProperty().bind(pane.prefHeightProperty());
        imageView.setTranslateY(-30);
        imageView.setTranslateX(0);
        pane.getChildren().removeIf(child -> child instanceof ImageView);
        pane.getChildren().add(imageView);
        updateAPHP(pane, hero, false).toFront();
    }

    private VBox updateAPHP(StackPane pane, Hero hero, boolean animation) {
        VBox vBox =
                (VBox) pane.getChildren().filtered(node -> node instanceof VBox).get(0);
        HBox hBox = (HBox) vBox.getChildren().get(0);
        Label ap = ((Label) hBox.getChildren().get(0));
        Label hp = ((Label) hBox.getChildren().get(2));
        List<ScaleTransition> transitions = new ArrayList<>();
        if (!ap.getText().equals(String.valueOf(hero.getAttackPowerInGame()))) {
            transitions.add(new ScaleTransition(Duration.millis(200), ap));
        }
        if (!hp.getText().equals(String.valueOf(hero.getHealthPointInGame()))) {
            transitions.add(new ScaleTransition(Duration.millis(200), hp));
        }
        ap.setText(String.valueOf(Math.max(0, hero.getAttackPowerInGame())));
        hp.setText(String.valueOf(Math.max(0, hero.getHealthPointInGame())));
        if (hero.getAccountName().equals(account.getUserName())) {
            ap.setStyle(this.ap);
            hp.setStyle(this.hp);
        } else {
            ap.setStyle(this.apBW);
            hp.setStyle(this.hpBW);
        }
        if (animation) {
            for (ScaleTransition transition : transitions) {
                transition.setByX(1.4);
                transition.setByY(1.4);
                transition.setCycleCount(2);
                transition.setAutoReverse(true);
                transition.play();
            }
        }
        return vBox;
    }

    private void showPossibleTargets() {
        for (Node child : board.getChildren()) {
            child.setDisable(true);
        }
        game.getInBoardCards().stream().filter(h -> game.getCurrentPlayer().hasCard(h))
                .forEach(hero -> {
                    for (int[] p : game.getNeighbours(hero.getX(),
                            hero.getY())) {
                        if (game.isEmpty(p[0], p[1])) {
                            StackPane pane = getNodeByRowColumnIndex(p[0], p[1]);
                            pane.setStyle("-fx-background-color: rgba(255,240,0,0.38)");
                            pane.setDisable(false);
                        }
                    }
                });
    }

    public void endTurn() {
        game.endTurn();
    }
}
