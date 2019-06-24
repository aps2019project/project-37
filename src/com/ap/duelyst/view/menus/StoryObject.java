package com.ap.duelyst.view.menus;

import com.ap.duelyst.controller.menu.BattleMenu;
import com.ap.duelyst.model.cards.Hero;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class StoryObject {
    private int reward;
    private Hero hero;
    private String mode;
    private int flag;
    private BattleMenu.StoryLevel level;

    public StoryObject(int reward, Hero hero, String mode, int flag,
                       BattleMenu.StoryLevel level) {
        this.reward = reward;
        this.hero = hero;
        this.mode = mode;
        this.flag = flag;
        this.level = level;
    }

    public int getReward() {
        return reward;
    }

    public VBox getHero() {
        if (hero.getCardSprite() == null) {
            hero.makeCardSprite();
            hero.getCardSprite().play();
        }
        VBox vBox = new VBox(16);
        ImageView imageView = hero.getImageView();
        Label name = new Label(hero.getName());
        name.setStyle("-fx-text-fill: white; -fx-font-weight: normal; -fx-font-size: 16");
        vBox.getChildren().addAll(imageView, name);
        vBox.setAlignment(Pos.CENTER);
        vBox.setMinHeight(220);
        return vBox;
    }

    public String getMode() {
        return mode;
    }

    public int getFlag() {
        return flag;
    }

    public BattleMenu.StoryLevel getLevel() {
        return level;
    }
}
