package com.ap.duelyst.view.battle;

import com.ap.duelyst.Command;
import com.ap.duelyst.Main;
import com.ap.duelyst.controller.GameException;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.controller.menu.ingame.InGameBattleMenu;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.buffs.*;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.model.cards.Minion;
import com.ap.duelyst.model.cards.Spell;
import com.ap.duelyst.model.game.Cell;
import com.ap.duelyst.model.game.Game;
import com.ap.duelyst.model.game.GraveYard;
import com.ap.duelyst.model.game.Player;
import com.ap.duelyst.model.items.CollectableItem;
import com.ap.duelyst.model.items.Item;
import com.ap.duelyst.view.DialogController;
import com.ap.duelyst.view.GameEvents;
import com.ap.duelyst.view.card.CardSprite;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
    public HBox dialog;
    public Label dialogText;
    public VBox dialogContainer;
    public ProgressBar timerBar;
    private DialogController dialogController;
    public VBox notificationContainer;
    public Label notification;
    public VBox rightButtonContainer;
    public Button collectibleButton;
    public Button graveYardButton;
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
    private String mineNotifBack = Utils.getPath("notification_go@2x.png");
    private String enemyNotifBack = Utils.getPath("notification_enemy_turn@2x.png");
    private Image flag = new Image(Utils.getPath("flag.gif"));
    private double xStart, yStart;
    private Card insertableCard;
    private StackPane container;
    private boolean first = true;
    private MouseButton firstClickType = MouseButton.PRIMARY;
    private Hero selectedCard;
    private CollectableItem selectedItem;
    private Card hoveredCard;
    private Item hoveredItem;
    private List<CardSprite> fire = new ArrayList<>();
    private List<CardSprite> poison = new ArrayList<>();
    private List<CardSprite> holy = new ArrayList<>();
    private InGameBattleMenu menu;
    private MenuManager manager;
    private Map<String, CardSprite> spriteMap = new HashMap<>();
    private ReaderThread readerThread;
    private List<List<Cell>> cachedBoard;
    private Timeline timeline;
    private Thread thread;
    private String cheat = "";
    private Timer timer;
    private boolean counting;


    {
        thread = new Thread(() -> {
            addToList("hellfire", fire);
            addToList("poison-lake", poison);
            addToList("health-with-benefit", holy);
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void addToList(String name, List<CardSprite> list) {
        Spell spell = (Spell) Utils.getShop().getObjectByName(name);
        for (int i = 0; i < 9; i++) {
            try {
                spell = spell.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            CardSprite sp = spell.getCardSprite();
            if (sp == null) {
                spell.makeCardSprite();
            }
            sp = spell.getCardSprite().clone();
            sp.showEffect();
            sp.setCycleCount(Animation.INDEFINITE);
            sp.playFromStart();
            list.add(sp);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readerThread = new ReaderThread(events);
        readerThread.start();
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
                int offset = 0;
                if (insertableCard instanceof Minion) {
                    offset = 30;
                }
                insertableCard.getCardSprite().getImageView()
                        .setTranslateY(event.getSceneY() - yStart - offset);
            }
        });
        root.setOnMouseReleased(event -> {
            xStart = 0;
            yStart = 0;
            if (insertableCard != null) {
                if (insertableCard instanceof Spell) {
                    insertableCard.getCardSprite().showInactive();
                }
                for (Node child : board.getChildren()) {
                    if (child.getStyle().equals("-fx-background-color: rgba(0,255,11,0" +
                            ".38)")) {
                        Command command = new Command("insert",
                                insertableCard.getId(),
                                GridPane.getRowIndex(child),
                                GridPane.getColumnIndex(child));
                        Main.writer.println(new Gson().toJson(command));
                        int offset = 0;
                        if (insertableCard instanceof Minion) {
                            offset = 30;
                        }
                        insertableCard.getCardSprite().getImageView().setTranslateX(0);
                        insertableCard.getCardSprite().getImageView().setTranslateY(-offset);
                        insertableCard = null;
                        break;
                    }
                }
                if (insertableCard != null) {
                    updateBoard(getInBoardCards(), true);
                    TranslateTransition transition =
                            new TranslateTransition(Duration.millis(300),
                                    insertableCard.getCardSprite().getImageView());
                    int offset = 0;
                    if (insertableCard instanceof Minion) {
                        offset = 30;
                    }
                    transition.setToX(0);
                    transition.setToY(-offset);
                    transition.play();
                    container.setStyle("-fx-background-image: url('" + circle + "')");
                }

            }
        });
        root.setOnMouseMoved(event -> {
            if (dialogContainer.isVisible() || notificationContainer.isVisible()
                    || root.getChildren().size() > 6) {
                return;
            }
            boolean shouldHide = true;
            for (Node child : board.getChildren()) {
                if (child.localToScene(child.getBoundsInLocal())
                        .contains(event.getSceneX(), event.getSceneY())) {
                    Hero card = getCardAt(GridPane.getRowIndex(child),
                            GridPane.getColumnIndex(child));
                    if (card != null) {
                        shouldHide = false;
                        if (hoveredCard == null || (card != hoveredCard && !card.idEquals(hoveredCard.getId()))) {
                            hoveredCard = card;
                            showHeroDialog(true);
                        }
                        break;
                    }
                    Item item = getBoard().get(GridPane.getRowIndex(child))
                            .get(GridPane.getColumnIndex(child)).getCollectableItem();
                    if (item != null) {
                        shouldHide = false;
                        if (hoveredItem == null || (item != hoveredItem && !item.idEquals(hoveredItem.getId()))) {
                            hoveredItem = item;
                            Bounds bounds = child.localToScene(child.getBoundsInLocal());
                            heroDialogCard.setTranslateX(bounds.getMinX() +
                                    bounds.getWidth() / 2 - heroDialogCard.getWidth() / 2);
                            double h = bounds.getMinY() - heroDialogCard.getHeight();
                            if (h < 0) {
                                h += heroDialogCard.getHeight() + bounds.getHeight();
                            }
                            heroDialogCard.setTranslateY(h);
                            showHeroDialog(false);
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
                    for (Player player : getPlayers()) {
                        if (player.getAccountName().equals(Main.userName)) {
                            hand = player.getHand();
                            break;
                        }
                    }
                    if (hand.size() > i) {
                        Card card = hand.get(i);
                        shouldHide = false;
                        if (hoveredCard == null || (card != hoveredCard && !card.idEquals(hoveredCard.getId()))) {
                            hoveredCard = card;
                            Bounds bounds = child.localToScene(child.getBoundsInLocal());
                            heroDialogCard.setTranslateY(bounds.getMinY() - heroDialogCard.getHeight());
                            heroDialogCard.setTranslateX(bounds.getMinX() + bounds.getWidth() / 2 - heroDialogCard.getWidth() / 2);
                            showHeroDialog(false);
                        }
                        break;
                    }
                }
            }
            if (shouldHide) {
                hideHeroDialog();
            }
        });
        root.setOnKeyTyped(event -> {
            if (!counting) {
                counting = true;
                timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        counting = false;
                        cheat = "";
                    }
                }, 10000);
            }
            cheat += event.getCharacter();
            if (cheat.equals("barca.ride")) {
                Main.writer.println(new Gson().toJson(new Command("cheat")));
            }
        });
        prepareHeroDialogCard();
        prepareHand();
        prepareBoard();
        prepareMana();
        dialogController = new DialogController(root, dialog, dialogText,
                dialogContainer);
        prepareRightButtons();
        notification.setStyle("-fx-background-image: url('" + mineNotifBack + "')");
        notification.getStyleClass().addAll("back", "shadow");
        notification.prefWidthProperty().bind(root.widthProperty().multiply(.5));
        notification.prefHeightProperty()
                .bind(notification.prefWidthProperty().divide(4.571));
        notification.setText("your turn");
    }

    private GameEvents events = new GameEvents() {
        @Override
        public void nextRound() {
            playMusic("sfx_ui_yourturn.m4a");
            updateHand();
            updateBoard(getInBoardCards(), true);
            if (getCurrentPlayer().getAccountName().equals(Main.userName)) {
                showNotification(true);
                turnButton.setDisable(false);
                turnButton.setText("     End Turn     ");
                turnButton.setStyle("-fx-background-image: url('" + turn + "')");
                turnButton.setOnMousePressed(event -> turnButton
                        .setStyle("-fx-background-image: url('" + turnGlow +
                                "')"));
                turnButton.setOnMouseReleased(event -> turnButton
                        .setStyle("-fx-background-image: url('" + turn + "')"));
            } else {
                showNotification(false);
                turnButton.setText("    Enemy Turn    ");
                turnButton.setStyle("-fx-background-image: url('" + turnEnemy + "')");
                turnButton.setOnMouseReleased(event -> turnButton
                        .setStyle("-fx-background-image: url('" + turnEnemy +
                                "')"));
                turnButton.setDisable(true);
            }
            if (getCurrentPlayer().getAccountName().equals(Main.userName)) {
                timerBar.setDisable(false);
            } else {
                timerBar.setDisable(true);
            }
            if (timeline != null) {
                timeline.stop();
            }
            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(timerBar.progressProperty(), 1)),
                    new KeyFrame(Duration.seconds(30), e -> {
                        if (getCurrentPlayer().getAccountName().equals(Main.userName)) {
                            endTurn();
                        }
                    }, new KeyValue(timerBar.progressProperty(), 0))
            );
            timeline.setCycleCount(1);
            timeline.play();
        }

        @Override
        public void gameEnded(String result) {
            timeline.stop();
            thread.interrupt();
            if (timer != null) {
                timer.cancel();
            }
            if (!result.isEmpty()) {
                dialogController.showDialog(result);
                playMusic("sfx_victory_crest.m4a");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        manager.setCurrentMenu(menu.getParentMenu().getParentMenu());
                    }
                }, 5000);
            } else {
                manager.setCurrentMenu(menu.getParentMenu().getParentMenu());
            }
        }

        @Override
        public void insert(String cardId, int x, int y) {
            playMusic("sfx_unit_deploy_3.m4a");
            CardSprite cardSprite = spriteMap.get(cardId);
            if (cardSprite.hasEffect()) {
                cardSprite.getImageView()
                        .setTranslateX(0);
                cardSprite.getImageView()
                        .setTranslateY(1000);
                cardSprite = cardSprite.clone();
                cardSprite.showEffect();
                VBox vBox = new VBox(cardSprite.getImageView());
                vBox.setStyle("-fx-background-color: rgba(0,0,0,0.51)");
                root.getChildren().add(vBox);
                StackPane child = getNodeByRowColumnIndex(x, y);
                Bounds bounds =
                        child.localToScene(child.getBoundsInLocal());
                cardSprite.getImageView().setTranslateX(bounds.getMinX());
                cardSprite.getImageView().setTranslateY(bounds.getMinY());
                cardSprite.setOnFinished(e -> {
                    root.getChildren().remove(vBox);
                    updateHand();
                    updateBoard(getInBoardCards(), true);
                });
            }
            updateHand();
            updateBoard(getInBoardCards(), true);
        }

        @Override
        public void move(String id, int oldX, int oldY, int finalI, int finalJ) {
            playMusic("sfx_spell_sunbloom.m4a");
            updateBoard(getInBoardCards(), false);
            CardSprite sprite = spriteMap.get(id);
            sprite.showRun();
            ParallelTransition transition = new ParallelTransition();
            StackPane pane = getNodeByRowColumnIndex(oldX, oldY);
            pane.setStyle("");
            for (Node child : pane.getChildren()) {
                if (child instanceof ImageView) {
                    transition.getChildren().add(moveAnimation(oldX, oldY,
                            finalI, finalJ, child));
                }
            }
            transition.play();
            transition.setOnFinished(e -> {
                sprite.showBreathing();
                updateBoard(getInBoardCards(), true);
            });

        }

        @Override
        public void attack(String attackerId, String attackedId) {
            playMusic("sfx_f1tank_attack_swing.m4a");
            CardSprite attackerSprite = spriteMap.get(attackerId);
            CardSprite attackedSprite = spriteMap.get(attackedId);
            attackerSprite.showAttack();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        attackerSprite.showBreathing();
                        attackedSprite.showAttack();
                    });
                }
            }, 500);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        attackedSprite.showBreathing();
                        showDeath(attackerId, attackerSprite, attackedId, attackedSprite);
                    });
                }
            }, 1000);
        }

        @Override
        public void specialPower(String cardId, int finalI, int finalJ) {
            playMusic("sfx_f5_kolossus_attack_impact.m4a");
            Hero card = null;
            for (List<Cell> row : cachedBoard) {
                for (Cell cell : row) {
                    if (cell.getCard() != null) {
                        if (cell.getCard().idEquals(cardId)) {
                            card = cell.getCard();
                        }
                    }
                }
            }
            assert card != null;
            CardSprite sprite = card.getSpecialPower()
                    .getCardSprite();
            if (sprite == null) {
                card.getSpecialPower().makeCardSprite();
                sprite = card.getSpecialPower()
                        .getCardSprite();
            }
            sprite.showEffect();
            ImageView imageView = sprite.getImageView();
            imageView.setScaleX(2);
            imageView.setScaleY(2);
            VBox vBox = new VBox(sprite.getImageView());
            vBox.setStyle("-fx-background-color: rgba(0,0,0,0.51)");
            sprite.setOnFinished(e -> root.getChildren().remove(vBox));
            root.getChildren().add(vBox);
            vBox.setAlignment(Pos.CENTER);
            updateHand();
            updateBoard(getInBoardCards(), true);
        }

        @Override
        public void useCollectable(String itemId) {
            playMusic("sfx_neutral_spelljammer_attack_impact.m4a");
            CardSprite sprite = poison.get(0).clone();
            sprite.showEffect();
            root.getChildren().add(sprite.getImageView());
            StackPane.setAlignment(sprite.getImageView(), Pos.CENTER);
            sprite.setOnFinished(event1 ->
                    root.getChildren().remove(sprite.getImageView()));
            updateBoard(getInBoardCards(), true);
        }


        @Override
        public void startGame() {

        }

        @Override
        public void error(String message) {
            playMusic("sfx_ui_error.m4a");
            dialogController.showDialog(message);
            updateBoard(getInBoardCards(), true);
            updateHand();
        }
    };

    private void playMusic(String name){
        Media media=new Media(Utils.getPath(name));
        new MediaPlayer(media).play();
    }

    private void prepareRightButtons() {
        turnButton.setText("     End Turn     ");
        turnButton.prefWidthProperty().bind(rightButtonContainer.prefWidthProperty().multiply(.8));
        turnButton.prefHeightProperty().bind(turnButton.prefWidthProperty().divide(3.07));
        turnButton.setPadding(new Insets(16));
        turnButton.getStyleClass().add("hand-container");
        turnButton.setStyle("-fx-background-image: url('" + turn + "')");
        collectibleButton.prefWidthProperty()
                .bind(rightButtonContainer.prefWidthProperty().divide(2));
        graveYardButton.prefWidthProperty()
                .bind(rightButtonContainer.prefWidthProperty().divide(2));
        String rightBack = Utils.getPath("button_primary_right@2x.png");
        String rightBackGlow = Utils.getPath("button_primary_right_glow@2x.png");
        String leftBack = Utils.getPath("button_primary_left@2x.png");
        String leftBackGlow = Utils.getPath("button_primary_left_glow@2x.png");
        collectibleButton.setStyle("-fx-background-image: url('" + leftBack + "')");
        graveYardButton.setStyle("-fx-background-image: url('" + rightBack + "')");
        collectibleButton.setPadding(new Insets(16));
        graveYardButton.setPadding(new Insets(16));
        collectibleButton.setOnMousePressed(event ->
                collectibleButton.setStyle("-fx-background-image: url('" + leftBackGlow + "')")
        );
        collectibleButton.setOnMouseReleased(event ->
                collectibleButton.setStyle("-fx-background-image: url('" + leftBack +
                        "')")
        );
        graveYardButton.setOnMousePressed(event ->
                graveYardButton.setStyle("-fx-background-image: url('" + rightBackGlow + "')")
        );
        graveYardButton.setOnMouseReleased(event ->
                graveYardButton.setStyle("-fx-background-image: url('" + rightBack +
                        "')")
        );
    }

    private void showNotification(boolean mine) {
        if (mine) {
            notification.setStyle("-fx-background-image: url('" + mineNotifBack + "')");
            notification.setText("Your Turn");
        } else {
            notification.setStyle("-fx-background-image: url('" + enemyNotifBack + "')");
            notification.setText("Enemy Turn");
        }
        FadeTransition fade = new FadeTransition(Duration.millis(300),
                notificationContainer);
        fade.setToValue(1);
        fade.setFromValue(0);
        notificationContainer.setVisible(true);
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), notification);
        scale.setFromY(.4);
        scale.setFromX(.4);
        scale.setToY(1);
        scale.setToX(1);
        ParallelTransition transition = new ParallelTransition(fade, scale);
        transition.play();
        transition.setOnFinished(event -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> hideNotification());
            }
        }, 300));

    }

    private void hideNotification() {
        FadeTransition fade = new FadeTransition(Duration.millis(300),
                notificationContainer);
        fade.setToValue(0);
        fade.setFromValue(1);
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), notification);
        scale.setFromY(1);
        scale.setFromX(1);
        scale.setToX(.4);
        scale.setToY(.4);
        ParallelTransition transition = new ParallelTransition(fade, scale);
        transition.play();
        transition.setOnFinished(event -> notificationContainer.setVisible(false));
    }

    private void showHeroDialog(boolean showTop) {
        if (showTop) {
            VBox box = p2Box;
            if (hoveredCard.getAccountName().equals(p1Name.getText())) {
                box = p1Box;
            }
            heroDialogCard
                    .setTranslateX(box.getLayoutX() + box.getWidth() / 2 - heroDialogCard.getWidth() / 2);
            heroDialogCard
                    .setTranslateY(p1ManaBox.localToScene(p1ManaBox.getBoundsInLocal()).getMaxY());
        }
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300),
                heroDialogCard);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
        CardSprite sprite;
        if (hoveredCard != null) {
            sprite = hoveredCard.getCardSprite().clone();
        } else {
            sprite = hoveredItem.getCardSprite().clone();
        }
        sprite.play();
        if (hoveredCard instanceof Spell || hoveredItem != null) {
            sprite.showActive();
            sprite.getImageView().setScaleX(2);
            sprite.getImageView().setScaleY(2);
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    VBox vBox = (VBox) heroDialogCard.getChildren().get(0);
                    vBox.getChildren().set(0, sprite.getImageView());
                    if (hoveredCard instanceof Spell || hoveredItem != null) {
                        VBox.setMargin(sprite.getImageView(),
                                new Insets(48, 0, 32, 0));
                    }
                });
            }
        }, 100);
        if (hoveredCard != null) {
            heroName.setText(hoveredCard.getName().replaceAll("-", " "));
            heroType.setText(hoveredCard.getClass().getSimpleName().toLowerCase());
            if (hoveredCard instanceof Hero) {
                String back = Utils.getPath("neutral_unit@2x.png");
                heroDialogCard.setStyle("-fx-background-image: url('" + back + "')");
                heroAP.getParent().setVisible(true);
                Hero hero = (Hero) hoveredCard;
                heroAP.setText(String.valueOf(hero.getAttackPowerInGame()));
                heroHP.setText(String.valueOf(hero.getHealthPointInGame()));
                if (hero.getSpecialPower() != null) {
                    heroDesc.setText(hero.getSpecialPower().getDesc());
                } else {
                    heroDesc.setText("no special power");
                }
            } else {
                Spell spell = (Spell) hoveredCard;
                String back = Utils.getPath("neutral_artifact.png");
                heroDialogCard.setStyle("-fx-background-image: url('" + back + "')");
                heroAP.getParent().setVisible(false);
                heroDesc.setText(spell.getDesc());
            }
        } else {
            String back = Utils.getPath("neutral_artifact.png");
            heroAP.getParent().setVisible(false);
            heroDialogCard.setStyle("-fx-background-image: url('" + back + "')");
            heroName.setText(hoveredItem.getName());
            heroDesc.setText(hoveredItem.getDesc());
            heroType.setText(hoveredItem.getClass().getSimpleName().toLowerCase());
        }

    }

    private void hideHeroDialog() {
        if (hoveredCard == null && hoveredItem == null) {
            return;
        }
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300),
                heroDialogCard);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
        hoveredCard = null;
        hoveredItem = null;
    }

    private void prepareHeroDialogCard() {
        heroDialogCard.getStyleClass().add("hero-dialog");
        heroDialogCard.setOpacity(0);
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
            p2ManaBox.getChildren().add(imageView);
        }
        if (getPlayers().get(0).getAccountName().equals(Main.userName)) {
            p1ManaBox.setDisable(false);
            p1Box.setDisable(false);
            p1Name.setDisable(false);
            p2ManaBox.setDisable(true);
            p2Box.setDisable(true);
            p2Name.setDisable(true);
            p2ManaBox.setOpacity(.5);
        } else {
            p1ManaBox.setDisable(true);
            p1Box.setDisable(true);
            p1Name.setDisable(true);
            p2ManaBox.setDisable(false);
            p2Box.setDisable(false);
            p2Name.setDisable(false);
            p1ManaBox.setOpacity(.5);
        }
        p1Name.setText(getPlayers().get(0).getAccountName());
        p2Name.setText(getPlayers().get(1).getAccountName());
        addUsableItem(0, p1Box);
        addUsableItem(1, p2Box);
    }

    private void prepareBoard() {
        board.prefWidthProperty().bind(root.widthProperty().multiply(.6));
        board.setVgap(8);
        board.setHgap(8);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                StackPane pane = new StackPane();
                pane.setAlignment(Pos.CENTER);
                pane.prefWidthProperty().bind(board.prefWidthProperty().subtract(72)
                        .divide(9));
                pane.prefHeightProperty().bind(pane.prefWidthProperty());
                pane.getStyleClass().add("board-cell");
                pane.setDisable(true);
                int finalI = i;
                int finalJ = j;
                pane.setOnMouseClicked(event -> {
                    hideHeroDialog();
                    if (first) {
                        selectedCard = getCardAt(finalI, finalJ);
                        if (event.getButton() == MouseButton.PRIMARY) {
                            if (selectedCard.getInGame().isMoved()
                                    || !selectedCard.getInGame().isMovable()) {
                                dialogController.showDialog("card cant move");
                                return;
                            }
                            first = false;
                            for (Node child : board.getChildren()) {
                                child.setDisable(true);
                            }
                            pane.setDisable(false);
                            for (int[] ints : getCellsInRange(finalI, finalJ, 2)) {
                                if (isEmpty(ints[0], ints[1])
                                        && checkRoad(selectedCard.getX(),
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
                                dialogController.showDialog("card has attacked");
                                return;
                            }
                            for (Node child : board.getChildren()) {
                                child.setDisable(true);
                            }
                            pane.setDisable(false);
                            List<int[]> pos = new ArrayList<>();
                            switch (selectedCard.getAttackType()) {
                                case RANGED:
                                    pos = getCellsInRange(selectedCard.getX(),
                                            selectedCard.getY(), selectedCard.getRange());
                                    for (int[] neighbour :
                                            getNeighbours(selectedCard.getX(),
                                                    selectedCard.getY())) {
                                        int index = pos.indexOf(neighbour);
                                        if (index >= 0) {
                                            pos.remove(index);
                                        }
                                    }
                                    break;
                                case MELEE:
                                    pos = getNeighbours(selectedCard.getX(),
                                            selectedCard.getY());
                                    break;
                                case HYBRID:
                                    pos = getCellsInRange(selectedCard.getX(),
                                            selectedCard.getY(), selectedCard.getRange());
                                    break;
                            }
                            for (int[] p : pos) {
                                Hero card = getCardAt(p[0], p[1]);
                                if (card != null && !card.getAccountName()
                                        .equals(Main.userName)) {
                                    getNodeByRowColumnIndex(p[0], p[1]).setDisable(false);
                                    getNodeByRowColumnIndex(p[0], p[1])
                                            .setStyle("-fx-background-color: rgba(" +
                                                    "15,64,255,0.38)");
                                    first = false;
                                }
                            }
                            if (first) {
                                dialogController.showDialog("card cant attack or no " +
                                        "target is detected");
                                updateBoard(getInBoardCards(), true);
                            }
                        }
                        if (event.getButton() == MouseButton.MIDDLE) {
                            Spell spell = selectedCard.getSpecialPower();
                            if (selectedCard.getClass() != Hero.class) {
                                dialogController.showDialog("card is not a hero");
                                return;
                            }
                            if (spell == null) {
                                dialogController.showDialog("card doesnt have special " +
                                        "power");
                                return;
                            }
                            if (selectedCard.isOnAttack()
                                    || selectedCard.isPassive()) {
                                dialogController.showDialog("hero's power is" +
                                        " not controllable by player");
                                return;
                            }
                            if (getCurrentPlayer().getMana()
                                    < selectedCard.getSpecialPowerMana()) {
                                dialogController.showDialog("not enough mana");
                                return;
                            }
                            if (selectedCard.getInGame().getCoolDown() > 0) {
                                dialogController.showDialog("cool down time is not over" +
                                        " yet");
                                return;
                            }
                            first = false;
                            for (Node child : board.getChildren()) {
                                child.setDisable(false);
                                child.setStyle("-fx-background-color: rgba(249,255," +
                                        "249,0.51)");
                            }
                            firstClickType = MouseButton.SECONDARY;
                        }
                    } else {
                        first = true;
                        Hero card = getCardAt(finalI, finalJ);
                        if (firstClickType == MouseButton.SECONDARY) {
                            firstClickType = MouseButton.PRIMARY;
                            try {
                                Command command = new Command("useSpecialPower",
                                        selectedCard.getId(), finalI, finalJ);
                                Main.writer.println(new Gson().toJson(command));
                            } catch (GameException e) {
                                dialogController.showDialog(e.getMessage());
                            }
                            return;
                        }
                        if (selectedItem != null) {
                            try {
                                Command command = new Command("useCollectable",
                                        selectedItem.getId());
                                Main.writer.println(new Gson().toJson(command));

                            } catch (GameException e) {
                                dialogController.showDialog(e.getMessage());
                            }
                            selectedItem = null;
                            return;
                        }
                        if (card != null
                                && !card.getAccountName().equals(Main.userName)) {
                            Command command = new Command("attack",
                                    selectedCard.getId(), card.getId());
                            Main.writer.println(new Gson().toJson(command));
                        } else if (card == null) {
                            Command command = new Command("move",
                                    selectedCard.getId(),
                                    finalI, finalJ);
                            Main.writer.println(new Gson().toJson(command));
                        } else {
                            updateBoard(getInBoardCards(), true);
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

    private TranslateTransition moveAnimation(int oldX, int oldY, int finalI, int finalJ,
                                              Node node) {
        TranslateTransition transition =
                new TranslateTransition(Duration.seconds(1),
                        node);
        StackPane oldP = getNodeByRowColumnIndex(oldX, oldY);
        StackPane newP = getNodeByRowColumnIndex(finalI, finalJ);
        transition.setByX(newP.getLayoutX() - oldP.getLayoutX());
        transition.setByY(newP.getLayoutY() - oldP.getLayoutY());
        return transition;
    }

    private void showDeath(String attackerId, CardSprite attacker, String attackedId,
                           CardSprite attacked) {
        for (Card card : getGraveYard().getCards()) {
            if (card.idEquals(attackerId)) {
                attacker.showDeath();
                playMusic("sfx_f2_kaidoassassin_death.m4a");
            }
            if (card.idEquals(attackedId)) {
                attacked.showDeath();
                playMusic("sfx_f2_kaidoassassin_death.m4a");
            }
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateBoard(getInBoardCards(), true));
            }
        }, 1000);
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
        handContainer.setMinHeight(150);
        handContainer.setMaxHeight(150);
        handContainer.setPrefHeight(150);
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

    private void addUsableItem(int i, VBox vBox) {
        List<Item> items = getPlayers().get(i).getDeck().getItems();
        if (items != null && !items.isEmpty()) {
            Label name = new Label(items.get(0).getName());
            name.setTextFill(Color.WHITE);
            name.setStyle("-fx-font-size: 18");
            Label desc = new Label(items.get(0).getDesc());
            desc.setTextFill(Color.WHITE);
            CardSprite sprite = items.get(0).getCardSprite();
            if (sprite == null) {
                items.get(0).makeSprite();
            }
            sprite = items.get(0).getCardSprite().clone();
            sprite.play();
            ImageView imageView = sprite.getImageView();
            imageView.setScaleX(1.4);
            imageView.setScaleY(1.4);
            imageView.setFitWidth(100);
            name.setMaxWidth(180);
            desc.setMaxWidth(180);
            name.setWrapText(true);
            desc.setWrapText(true);
            vBox.getChildren().addAll(imageView, name, desc);
        }
    }

    private void updateMana() {
        fillMana(p1ManaBox, getPlayers().get(0).getMana());
        fillMana(p2ManaBox, getPlayers().get(1).getMana());
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
        for (Player player : getPlayers()) {
            if (player.getAccountName().equals(Main.userName)) {
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
            card.getCardSprite().play();
            StackPane pane = handContainers.get(i);
            Label label = (Label) ((VBox) pane.getChildren()
                    .filtered(node -> node instanceof VBox).get(0)).getChildren().get(0);
            if (getCurrentPlayer().getAccountName().equals(Main.userName)) {
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
            pane.setOnDragDetected(event -> {
                hideHeroDialog();
                showPossibleTargets(card instanceof Spell);
                xStart = event.getSceneX();
                yStart = event.getSceneY();
                insertableCard = card;
                if (insertableCard instanceof Spell) {
                    insertableCard.getCardSprite().showActive();
                }
                container = pane;
                container.setStyle("-fx-background-image: url('" + circleHighlight +
                        "')");
            });
            pane.getChildren().removeIf(child -> child instanceof ImageView);
            pane.getChildren().add(imageView);
            pane.getChildren().filtered(node -> node instanceof VBox).get(0).toFront();
        }
    }

    private void updateBoard(List<Hero> inBoardCards, boolean updatePosition) {
        for (Node child : board.getChildren()) {
            if (updatePosition) {
                ((StackPane) child).getChildren().removeIf(c -> c instanceof ImageView);
            }
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
        for (Hero hero : inBoardCards) {
            hero.getCardSprite().play();
            StackPane pane = getNodeByRowColumnIndex(hero.getX(),
                    hero.getY());
            if (pane != null) {
                ImageView imageView = hero.getCardSprite().getImageView();
                if (hero.getAccountName().equals(getPlayers().get(1).getAccountName())) {
                    imageView.setRotationAxis(Rotate.Y_AXIS);
                    imageView.setRotate(180);
                }
                if (!hero.getAccountName().equals(Main.userName)) {
                    pane.setStyle("-fx-background-color: rgba(255,3,0,0" +
                            ".38)");
                    pane.setDisable(true);
                } else if (getCurrentPlayer().getAccountName().equals(Main.userName)) {
                    pane.setDisable(false);
                } else {
                    pane.setDisable(true);
                }
                if (updatePosition) {
                    updateCardPos(pane, imageView, hero);
                }
            }
        }
        for (int i = 0; i < getBoard().size(); i++) {
            for (int j = 0; j < getBoard().get(i).size(); j++) {
                if (getBoard().get(i).get(j).isHasFlag()) {
                    addFlag(i, j, false);
                }
                Item item = getBoard().get(i).get(j).getCollectableItem();
                if (item != null) {
                    addCollectableItem(i, j, item);
                }
                List<Buff> buffs = getBoard().get(i).get(j).getBuffs();
                for (Buff buff : buffs) {
                    Buff buff1 = ((CellBuff) buff).getBuff();
                    if (buff1.getClass() == WeaknessBuff.class) {
                        addImageView(getNodeByRowColumnIndex(i, j),
                                fire.get(0).getImageView());
                        fire.add(fire.remove(0));
                    } else if (buff1.getClass() == PoisonBuff.class) {
                        addImageView(getNodeByRowColumnIndex(i, j),
                                poison.get(0).getImageView());
                        poison.add(poison.remove(0));
                    } else if (buff1.getClass() == HolyBuff.class) {
                        addImageView(getNodeByRowColumnIndex(i, j),
                                holy.get(0).getImageView());
                        holy.add(holy.remove(0));
                    }
                }
            }
        }
    }

    private void addCollectableItem(int x, int y, Item item) {
        StackPane pane = getNodeByRowColumnIndex(x, y);
        item.getCardSprite().play();
        ImageView imageView = item.getCardSprite().getImageView();
        imageView.setScaleX(1.7);
        imageView.setScaleY(1.7);
        pane.getChildren().remove(imageView);
        addImageView(pane, imageView);
    }

    private void addImageView(StackPane pane, ImageView imageView) {
        imageView.fitHeightProperty().bind(pane.prefHeightProperty().multiply(.8));
        imageView.fitWidthProperty().bind(imageView.fitHeightProperty().multiply(.457));
        pane.getChildren().add(imageView);
        imageView.toFront();
    }

    private void addFlag(int x, int y, boolean isOwned) {
        StackPane pane = getNodeByRowColumnIndex(x, y);
        ImageView imageView = new ImageView(flag);
        addImageView(pane, imageView);
        if (isOwned) {
            StackPane.setAlignment(imageView, Pos.TOP_RIGHT);
            StackPane.setMargin(imageView, new Insets(8, 8, 0, 0));
            imageView.fitHeightProperty().bind(pane.prefHeightProperty().multiply(.5));
        }
    }

    private void updateCardPos(StackPane pane, ImageView imageView, Hero hero) {
        imageView.fitWidthProperty().bind(pane.prefWidthProperty());
        imageView.fitHeightProperty().bind(pane.prefHeightProperty());
        imageView.setTranslateY(-30);
        imageView.setTranslateX(0);
        pane.getChildren().add(imageView);
        updateAPHP(pane, hero, false).toFront();
        if (hero.getInGame().isHasFlag()) {
            addFlag(hero.getX(), hero.getY(), true);
        }
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
        if (hero.getAccountName().equals(Main.userName)) {
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

    private void showPossibleTargets(boolean showAll) {
        if (showAll) {
            for (Node child : board.getChildren()) {
                child.setDisable(false);
                child.setStyle("-fx-background-color: rgba(255,240,0,0.38)");
            }
            return;
        }
        for (Node child : board.getChildren()) {
            child.setDisable(true);
        }
        getInBoardCards().stream().filter(h -> getCurrentPlayer().hasCard(h))
                .forEach(hero -> {
                    for (int[] p : getNeighbours(hero.getX(),
                            hero.getY())) {
                        if (isEmpty(p[0], p[1])) {
                            StackPane pane = getNodeByRowColumnIndex(p[0], p[1]);
                            pane.setStyle("-fx-background-color: rgba(255,240,0,0.38)");
                            pane.setDisable(false);
                        }
                    }
                });
    }

    public void endTurn() {
        Command command = new Command("endTurn");
        Main.writer.println(new Gson().toJson(command));
    }

    public void showCollectibles() {
        if (getCurrentPlayer().getAccountName().equals(Main.userName)) {
            VBox vBox = new VBox();
            vBox.setFillWidth(false);
            vBox.setStyle("-fx-background-color: rgba(0,0,0,0.71)");
            vBox.setAlignment(Pos.CENTER);
            vBox.setPadding(new Insets(128, 0, 128, 0));
            vBox.setOnMouseClicked(event -> root.getChildren().remove(vBox));
            ListView<VBox> p1 = new ListView<>();
            String back = Utils.getPath("neutral_artifact.png");
            List<VBox> p1List = getCurrentPlayer().getCollectableItems().stream()
                    .map(item -> {
                        VBox box = new VBox(16);
                        box.getStyleClass().add("back");
                        box.setPadding(new Insets(8));
                        box.setAlignment(Pos.CENTER);
                        CardSprite sprite = item.getCardSprite();
                        if (sprite == null) {
                            item.makeSprite();
                        }
                        sprite = item.getCardSprite().clone();
                        sprite.play();
                        ImageView imageView = sprite.getImageView();
                        imageView.setFitHeight(150);
                        imageView.setFitWidth(150);
                        box.getChildren().add(imageView);
                        Label label = new Label(item.getName());
                        label.setTextFill(Color.WHITE);
                        label.setStyle("-fx-font-size: 18");
                        label.setWrapText(true);
                        label.setAlignment(Pos.CENTER);
                        label.setMaxWidth(150);
                        Label label1 = new Label(item.getDesc());
                        label1.setTextFill(Color.WHITE);
                        label1.setAlignment(Pos.CENTER);
                        label1.setWrapText(true);
                        label1.setMaxWidth(150);
                        box.getChildren().add(label);
                        box.getChildren().add(label1);
                        box.prefHeightProperty()
                                .bind(box.widthProperty().multiply(1.309));
                        box.setStyle("-fx-background-image: url('" + back + "')");
                        box.setOnMouseClicked(event -> {
                            root.getChildren().remove(vBox);
                            selectedItem = item;
                            first = false;
                            for (Node child : board.getChildren()) {
                                child.setDisable(false);
                                child.setStyle("-fx-background-color: rgba(255,160,238," +
                                        "0.71)");
                            }
                        });
                        return box;
                    }).collect(Collectors.toList());
            p1.getItems().addAll(p1List);
            vBox.getChildren().add(p1);
            VBox.setVgrow(p1, Priority.ALWAYS);
            root.getChildren().add(vBox);
        } else {
            dialogController.showDialog("it's not your turn");
        }
    }

    public void showGraveYard() {
        List<Card> cards = getGraveYard().getCards();
        VBox vBox1 = new VBox();
        vBox1.setAlignment(Pos.TOP_CENTER);
        vBox1.setFillWidth(false);
        vBox1.setSpacing(32);
        VBox vBox2 = new VBox();
        vBox2.setFillWidth(false);
        vBox2.setSpacing(32);
        vBox2.setAlignment(Pos.TOP_CENTER);
        Label label1 = new Label("player 1 cards");
        label1.setTextFill(Color.WHITE);
        label1.setStyle("-fx-font-size: 30");
        Label label2 = new Label("player 2 cards");
        label2.setTextFill(Color.WHITE);
        label2.setStyle("-fx-font-size: 30");

        vBox1.getChildren().add(label1);
        vBox1.getChildren().add(getListView(cards,
                getPlayers().get(0).getAccountName()));
        vBox2.getChildren().add(label2);
        vBox2.getChildren().add(getListView(cards,
                getPlayers().get(1).getAccountName()));
        VBox.setVgrow(vBox1.getChildren().get(1), Priority.ALWAYS);
        VBox.setVgrow(vBox2.getChildren().get(1), Priority.ALWAYS);
        HBox hBox = new HBox(vBox1, vBox2);
        hBox.setStyle("-fx-background-color: rgba(0,0,0,0.71)");
        hBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(vBox1, Priority.ALWAYS);
        HBox.setHgrow(vBox2, Priority.ALWAYS);
        hBox.setPadding(new Insets(128, 0, 128, 0));
        hBox.setOnMouseClicked(event -> root.getChildren().remove(hBox));
        root.getChildren().add(hBox);
    }

    private ListView<VBox> getListView(List<Card> cards, String name) {
        ListView<VBox> p1 = new ListView<>();
        String back = Utils.getPath("neutral_artifact.png");
        List<VBox> p1List = cards.stream()
                .filter(card -> card.getAccountName().equals(name))
                .map(card -> {
                    VBox vBox = new VBox(16);
                    vBox.setAlignment(Pos.CENTER);
                    vBox.setPadding(new Insets(8));
                    CardSprite sprite = card.getCardSprite();
                    if (sprite == null) {
                        card.makeCardSprite();
                    }
                    sprite = card.getCardSprite().clone();
                    sprite.play();
                    ImageView imageView = sprite.getImageView();
                    imageView.setFitWidth(150);
                    imageView.setFitHeight(150);
                    vBox.getChildren().add(imageView);
                    Label label = new Label(card.getName());
                    label.setWrapText(true);
                    label.setTextFill(Color.WHITE);
                    label.setStyle("-fx-font-size: 18");
                    label.setMaxWidth(150);
                    label.setAlignment(Pos.CENTER);
                    vBox.getChildren().add(label);
                    vBox.getStyleClass().add("back");
                    vBox.prefHeightProperty().bind(vBox.widthProperty().multiply(1.309));
                    vBox.setStyle("-fx-background-image: url('" + back + "')");
                    return vBox;
                }).collect(Collectors.toList());
        p1.getItems().addAll(p1List);
        return p1;
    }

    public void setMenu(InGameBattleMenu menu) {
        this.menu = menu;
    }

    public void close() {
        dialogController.setEventHandler(event ->
                Main.writer.println(new Gson().toJson(new Command("endGame"))));
        dialogController.showDialog("Are you sure ? you will lose the game and lose " +
                "Money", true);
    }

    private <T> T callServer(String commandName, TypeToken<T> typeToken,
                             Object... parameters) {
        Command command = new Command(commandName, parameters);
        Main.writer.println(new Gson().toJson(command));
        String json = readerThread.getResp();
        while (json == null) {
            System.out.println("wating");
            json = readerThread.getResp();
        }
        T resp = null;
        while (resp == null) {
            try {
                resp = Utils.getGson().fromJson(json, typeToken.getType());
            } catch (JsonSyntaxException e) {
                json = readerThread.getResp();
            }
        }
        return resp;
    }


    private List<Hero> getInBoardCards() {
        List<Hero> cards = new ArrayList<>();
        for (List<Cell> row : getBoard()) {
            for (Cell cell : row) {
                if (cell.getHeroMinion() != null) {
                    cards.add(cell.getHeroMinion());
                }
            }
        }
        return cards;
    }

    private Hero getCardAt(int x, int y) {
        return getBoard().get(x).get(y).getCard();
    }

    private boolean isEmpty(int x, int y) {
        return getCardAt(x, y) == null;
    }

    private List<List<Cell>> getBoard() {
        List<List<Cell>> board = callServer("getBoard",
                new TypeToken<List<List<Cell>>>() {
                });
        for (List<Cell> row : board) {
            for (Cell cell : row) {
                if (cell.getCard() != null) {
                    Hero hero = cell.getCard();
                    spriteMap.computeIfAbsent(hero.getId(), k -> {
                        hero.makeCardSprite();
                        return hero.getCardSprite();
                    });
                    hero.setCardSprite(spriteMap.get(hero.getId()));
                }
                if (cell.getCollectableItem() != null) {
                    CollectableItem item = cell.getCollectableItem();
                    spriteMap.computeIfAbsent(item.getId(), k -> {
                        item.makeSprite();
                        return item.getCardSprite();
                    });
                    item.setCardSprite(spriteMap.get(item.getId()));
                }
            }
        }
        cachedBoard = board;
        return board;
    }

    private List<Player> getPlayers() {
        List<Player> players = callServer("getPlayers", new TypeToken<List<Player>>() {
        });
        for (Player player : players) {
            addSprite(player);
        }
        return players;
    }

    private Player getCurrentPlayer() {
        Player player = callServer("getCurrentPlayer", new TypeToken<Player>() {
        });
        addSprite(player);
        return player;
    }

    private void addSprite(Player player) {
        for (Card card : player.getHand()) {
            spriteMap.computeIfAbsent(card.getId(), k -> {
                card.makeCardSprite();
                return card.getCardSprite();
            });
            card.setCardSprite(spriteMap.get(card.getId()));
        }

        for (CollectableItem item : player.getCollectableItems()) {
            spriteMap.computeIfAbsent(item.getId(), k -> {
                item.makeSprite();
                return item.getCardSprite();
            });
            item.setCardSprite(spriteMap.get(item.getId()));
        }
    }

    private List<int[]> getCellsInRange(int x, int y, int range) {
        List<int[]> list = Utils.getEmptyPosList();
        list.addAll(callServer("getCellsInRange", new TypeToken<List<int[]>>() {
        }, x, y, range));
        return list;
    }

    private List<int[]> getNeighbours(int x, int y) {
        List<int[]> list = Utils.getEmptyPosList();
        list.addAll(callServer("getNeighbours", new TypeToken<List<int[]>>() {
        }, x, y));
        return list;
    }

    private GraveYard getGraveYard() {
        return callServer("getGraveYard", new TypeToken<GraveYard>() {
        });
    }

    private boolean checkRoad(int x1, int y1, int x2, int y2) {
        return callServer("checkRoad", new TypeToken<Boolean>() {
        }, x1, y1, x2, y2);
    }

    public void setManager(MenuManager manager) {
        this.manager = manager;
    }
}

class ReaderThread extends Thread {
    private GameEvents events;
    private String resp;

    public ReaderThread(GameEvents events) {
        this.events = events;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        super.run();
        main:
        while (true) {
            try {
                JsonObject jsonObject =
                        new JsonParser().parse(Main.scanner.nextLine()).getAsJsonObject();
                if (jsonObject.get("name") != null) {
                    String name = jsonObject.get("name").getAsString();
                    switch (name) {
                        case "nextRound":
                            Platform.runLater(() -> events.nextRound());
                            break;
                        case "insert":
                            Platform.runLater(() ->
                                    events.insert(jsonObject.get("cardId").getAsString(),
                                            jsonObject.get("x").getAsInt(),
                                            jsonObject.get("y").getAsInt()));
                            break;
                        case "move":
                            Platform.runLater(() ->
                                    events.move(jsonObject.get("id").getAsString(),
                                            jsonObject.get("oldX").getAsInt(),
                                            jsonObject.get("oldY").getAsInt(),
                                            jsonObject.get("finalI").getAsInt(),
                                            jsonObject.get("finalJ").getAsInt()));
                            break;
                        case "attack":
                            Platform.runLater(() ->
                                    events.attack(jsonObject.get("attackerId").getAsString(),
                                            jsonObject.get("attackedId").getAsString()));
                            break;
                        case "specialPower":
                            Platform.runLater(() ->
                                    events.specialPower(jsonObject.get("cardId").getAsString(),
                                            jsonObject.get("finalI").getAsInt(),
                                            jsonObject.get("finalJ").getAsInt()));
                            break;
                        case "useCollectable":
                            Platform.runLater(() -> events.useCollectable(""));
                            break;
                        case "gameEnded":
                            Platform.runLater(() -> events.gameEnded(jsonObject.get(
                                    "result").getAsString()));
                            break main;
                    }
                }
                if (jsonObject.get("resp") != null && !jsonObject.get("resp").getAsString().equals("null")) {
                    resp = jsonObject.get("resp").getAsString();
                }
                if (jsonObject.get("error") != null) {
                    Platform.runLater(() -> events.error(jsonObject.get("error").getAsString()));
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

        }
    }

    public String getResp() {
        return resp;
    }
}
