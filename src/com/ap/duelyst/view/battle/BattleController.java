package com.ap.duelyst.view.battle;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.model.Account;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import com.ap.duelyst.model.game.Game;
import com.ap.duelyst.view.GameEvents;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BattleController implements Initializable {
    private Game game;
    public VBox root;
    public HBox handContainer;
    public GridPane board;
    private List<StackPane> handContainers = new ArrayList<>();
    private String circle = Utils.getPath("card_background@2x.png");
    private String circleHighlight = Utils.getPath("card_background_highlight@2x.png");
    private double xStart, yStart;
    private ImageView image;
    private StackPane container;
    private Account account;
    private boolean first = true;
    private int[] pos;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String back = Utils.getPath("chapter1_background@2x.jpg");
        root.setPrefWidth(500);
        root.setPrefHeight(300);
        root.setStyle("-fx-background-image: url(' " + back + "')");
        root.setOnMouseDragged(event -> {
            if (xStart != 0) {
                image.setTranslateX(event.getSceneX() - xStart);
                image.setTranslateY(event.getSceneY() - yStart - 20);
            }
        });
        root.setOnMouseReleased(event -> {
            xStart = 0;
            yStart = 0;
            if (image != null) {
                TranslateTransition transition =
                        new TranslateTransition(Duration.millis(300), image);
                transition.setToX(0);
                transition.setToY(-20);
                transition.play();
                container.setStyle("-fx-background-image: url('" + circle + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-position: center;" +
                        "-fx-cursor: hand");
            }
        });
        prepareHand();
        prepareBoard();
    }

    private void prepareBoard() {
        board.prefWidthProperty().bind(root.widthProperty().multiply(.4));
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
                    if (first) {
                        first = false;
                        pos = new int[]{finalI, finalJ};
                        for (int[] ints : game.getCellsInRange(finalI, finalJ, 2)) {
                            StackPane child = getNodeByRowColumnIndex(ints[0], ints[1]);
                            child.setDisable(false);
                            child.setStyle("-fx-background-color: rgba(255,240,0,0.38)");
                        }
                    } else {
                        first = true;
                        for (Node child : board.getChildren()) {
                            if (!"-fx-background-color: rgba(255,3,0,0.38)"
                                    .equals(child.getStyle())) {
                                child.setStyle("");
                                child.setDisable(true);
                            }
                        }
                    }

                });
                board.add(pane, j, i);
            }
        }

    }

    private void prepareHand() {
        handContainer.prefWidthProperty().bind(root.widthProperty().multiply(.6));
        handContainer.prefHeightProperty().bind(root.heightProperty().multiply(.15));
        for (int i = 0; i < 5; i++) {
            StackPane pane = new StackPane();
            pane.setAlignment(Pos.CENTER);
            pane.setStyle("-fx-background-image: url('" + circle + "');" +
                    "-fx-background-size: cover;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-position: center;" +
                    "-fx-cursor: hand");
            pane.prefWidthProperty().bind(handContainer.prefHeightProperty());
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
            this.account = controller.getCurrentAccount();
            this.game.setEvents(new GameEvents() {
                @Override
                public void insert(int x, int y) {

                }

                @Override
                public void nextRound(List<Card> hand, int p1Mana, int p2Mana,
                                      List<Card> inGameCards) {

                    for (int i = 0; i < hand.size(); i++) {
                        Card card = hand.get(i);
                        if (card instanceof Hero) {
                            if (card.getCardSprite() == null) {
                                card.makeCardSprite();
                                card.getCardSprite().play();
                            }
                        }
                        ImageView imageView = card.getCardSprite().getImageView();
                        StackPane pane = handContainers.get(i);
                        imageView.fitWidthProperty().bind(pane.prefWidthProperty());
                        imageView.fitHeightProperty().bind(pane.prefHeightProperty());
                        pane.setOnDragDetected(event -> {
                            xStart = event.getSceneX();
                            yStart = event.getSceneY();
                            image = imageView;
                            container = pane;
                            container.setStyle("-fx-background-image: url('" + circleHighlight + "');" +
                                    "-fx-background-size: cover;" +
                                    "-fx-background-repeat: no-repeat;" +
                                    "-fx-background-position: center;" +
                                    "-fx-cursor: hand");
                        });
                        pane.getChildren().clear();
                        pane.getChildren().add(imageView);
                    }

                    for (Card inGameCard : inGameCards) {
                        if (inGameCard instanceof Hero) {
                            Hero hero = (Hero) inGameCard;
                            if (hero.getCardSprite() == null) {
                                hero.makeCardSprite();
                                hero.getCardSprite().play();
                            }
                            StackPane pane = getNodeByRowColumnIndex(hero.getX(),
                                    hero.getY());
                            if (pane != null) {
                                ImageView imageView = hero.getCardSprite().getImageView();
                                if (!hero.getAccountName().equals(account.getUserName())) {
                                    pane.setStyle("-fx-background-color: rgba(255,3,0,0" +
                                            ".38)");
                                    pane.setDisable(true);
                                } else {
                                    pane.setDisable(false);
                                }
                                imageView.fitWidthProperty().bind(pane.prefWidthProperty());
                                imageView.fitHeightProperty().bind(pane.prefHeightProperty());
                                pane.getChildren().setAll(imageView);
                            }
                        }
                    }

                }

                @Override
                public void move(int x, int y) {

                }
            });
            this.game.startGame();
        }
    }
}
