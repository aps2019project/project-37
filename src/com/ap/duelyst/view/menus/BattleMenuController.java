package com.ap.duelyst.view.menus;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.menu.BattleMenu;
import com.ap.duelyst.controller.menu.CollectionMenu;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.model.Deck;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import com.ap.duelyst.model.cards.Hero;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BattleMenuController implements Initializable {
    public HBox heroRow;
    private MenuManager menuManager;
    private BattleMenu battleMenu;
    private Controller controller;

    public StackPane mainPane;

    public VBox playerModeBox;
    public Button singlePlayerButton;
    public Button multiPlayerButton;

    public VBox singlePlayerBox;
    public Button storyButton;
    public Button customButton;

    public VBox storyBox;
    public Label storyDescription;
    public Button story1;
    public Button story2;
    public Button story3;

    public VBox customModeBox;
    public Button custom1;
    public Button custom2;
    public Button custom3;

    public VBox customHeroBox;

    public VBox flagBox;
    public ChoiceBox<Integer> numberOfFlags;
    public Button flagOkButton;

    BattleMenu.CustomGameMode customGameMode;
    int flagNumber;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();
        numberOfFlags.getItems().addAll(5,6,7,8,9,10);


    }
    public void update(){
        playerModeBox.setVisible(true);
        singlePlayerBox.setVisible(false);
        storyBox.setVisible(false);
        customModeBox.setVisible(false);
        try {
            Hero first = controller.createDeck(1).getHero();
            Hero second = controller.createDeck(2).getHero();
            Hero third = controller.createDeck(3).getHero();
            first.makeCardSprite();
            first.getCardSprite().play();
            second.makeCardSprite();
            second.getCardSprite().play();
            third.makeCardSprite();
            third.getCardSprite().play();

            heroRow.getChildren().addAll(first.getImageView(),second.getImageView(),third.getImageView());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        storyDescription.setText(battleMenu.storyString());
        storyDescription.setStyle("-fx-font-size: 14pt");
    }

    public void setAllBackgrounds(){
        String back = Utils.getPath("chapter10_background@2x.jpg");
        mainPane.setStyle("-fx-background-image: url(' " + back + "')");

        mainPane.setId("stackPane");

        String greenButtonGlowPath = Utils.getPath("button_confirm.png");
        String greenButtonNormalPath = Utils.getPath("button_confirm_glow.png");

        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(singlePlayerButton);
        buttons.add(multiPlayerButton);
        buttons.add(storyButton);
        buttons.add(customButton);
        buttons.add(custom1);
        buttons.add(custom2);
        buttons.add(custom3);
        buttons.add(story1);
        buttons.add(story2);
        buttons.add(story3);
        buttons.add(flagOkButton);
        buttons.forEach(button -> {
            setButtonBackground(button,greenButtonNormalPath);
            setButtonGlowOnMouseMoving(button,greenButtonNormalPath,greenButtonGlowPath);
        });

    }
    public void setAllActions(){
        singlePlayerButton.setOnAction(o -> {
            battleMenu.setPlayMode(BattleMenu.PlayMode.SINGLE_PLAYER);
            playerModeBox.setVisible(false);
            singlePlayerBox.setVisible(true);
        });
        multiPlayerButton.setOnAction(o -> {
            battleMenu.setPlayMode(BattleMenu.PlayMode.MULTI_PLAYER);
        });
        storyButton.setOnAction(o->{
            singlePlayerBox.setVisible(false);
            storyBox.setVisible(true);
        });
        customButton.setOnAction(o->{
            singlePlayerBox.setVisible(false);
            customModeBox.setVisible(true);
        });
        story1.setOnAction(o -> {
            try {
                controller.createGame(BattleMenu.StoryLevel.ONE,0);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            menuManager.setCurrentMenu(battleMenu.getInGameBattleMenu());
        });

        story2.setOnAction(o -> {
            try {
                controller.createGame(BattleMenu.StoryLevel.TWO,1);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            menuManager.setCurrentMenu(battleMenu.getInGameBattleMenu());
        });

        story3.setOnAction(o -> {
            try {
                controller.createGame(BattleMenu.StoryLevel.THREE,7);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            menuManager.setCurrentMenu(battleMenu.getInGameBattleMenu());
        });
        custom1.setOnAction(o ->{
            customGameMode = BattleMenu.CustomGameMode.KILL_ENEMY_HERO;
            customModeBox.setVisible(false);
            customHeroBox.setVisible(true);

        });
        custom2.setOnAction(o ->{
            customGameMode = BattleMenu.CustomGameMode.KEEP_FLAG_8_ROUNDS;
            customModeBox.setVisible(false);
            customHeroBox.setVisible(true);
        });
        custom3.setOnAction(o ->{
            customGameMode = BattleMenu.CustomGameMode.COLLECT_HALF_FLAGS;
            customModeBox.setVisible(false);
            flagBox.setVisible(true);
        });
        flagOkButton.setOnAction(o -> {
            flagNumber = numberOfFlags.getValue();
            flagBox.setVisible(false);
            customHeroBox.setVisible(true);
        });

        for (int i = 0; i < heroRow.getChildren().size(); i++) {
            int finalI = i;
            heroRow.getChildren().get(i).setOnMouseClicked(o->{
                try {
                    controller.createGame(finalI +1,customGameMode,flagNumber);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                menuManager.setCurrentMenu(battleMenu.getInGameBattleMenu());
            });
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    public void setBattleMenu(BattleMenu battleMenu) {
        this.battleMenu = battleMenu;
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
