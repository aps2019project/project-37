package com.ap.duelyst.view.menus;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.controller.menu.BattleMenu;
import com.ap.duelyst.controller.menu.CollectionMenu;
import com.ap.duelyst.controller.menu.MenuManager;
import com.ap.duelyst.model.Deck;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.Card;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BattleMenuController implements Initializable {
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

    public VBox customBox;
    public Label customDescription;
    public Button custom1;
    public Button custom2;
    public Button custom3;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAllBackgrounds();
        setAllActions();

        TableColumn<Deck, String> column = new TableColumn<>("Name");
        column.setCellValueFactory(new PropertyValueFactory<>("name"));

    }
    public void update(){
        storyDescription.setText(battleMenu.storyString());
        customDescription.setText(battleMenu.customGameString());
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
            customBox.setVisible(true);
        });
        story1.setOnAction(o -> {
            battleMenu.setStoryLevel(BattleMenu.StoryLevel.ONE);
            try {
                controller.createGame(BattleMenu.StoryLevel.ONE,0);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            menuManager.setCurrentMenu(battleMenu.getInGameBattleMenu());
        });

        story2.setOnAction(o -> {
            battleMenu.setStoryLevel(BattleMenu.StoryLevel.TWO);
            try {
                controller.createGame(BattleMenu.StoryLevel.TWO,1);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            menuManager.setCurrentMenu(battleMenu.getInGameBattleMenu());
        });

        story3.setOnAction(o -> {
            battleMenu.setStoryLevel(BattleMenu.StoryLevel.THREE);
            try {
                controller.createGame(BattleMenu.StoryLevel.THREE,7);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            menuManager.setCurrentMenu(battleMenu.getInGameBattleMenu());
        });
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
