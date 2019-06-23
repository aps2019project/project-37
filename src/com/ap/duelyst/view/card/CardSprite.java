package com.ap.duelyst.view.card;

import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.CardFrames;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class CardSprite extends Transition implements Cloneable {
    private ImageView imageView;
    private CardFrames cardFrames;
    private List<Frame> currentFrames;
    private int currentIndex;
    private String fileName;

    public CardSprite(String filename) {
        this.fileName = filename;
        cardFrames = new CardFrames(filename);
        currentFrames = cardFrames.getBreathingFrames();
        this.imageView = new ImageView(Utils.getPath(filename + ".png"));
        imageView.setTranslateY(-30);
        imageView.setScaleX(1.5);
        imageView.setScaleY(1.5);
        setCycleDuration(Duration.seconds(1));
        setInterpolator(Interpolator.LINEAR);
        setCycleCount(Animation.INDEFINITE);
    }

    @Override
    public CardSprite clone()  {
        return new CardSprite(fileName);
    }

    @Override
    protected void interpolate(double frac) {
        final int index = Integer.min(currentFrames.size() - 1,
                (int) Math.floor(frac * currentFrames.size()));
        Frame currentFrame = currentFrames.get(index);
        if (imageView != null) {
            if (index != currentIndex) {
                imageView.setViewport(new Rectangle2D(
                        currentFrame.getPosX(), currentFrame.getPosY(),
                        currentFrame.getWidth(), currentFrame.getHeight()));
                currentIndex = index;
            }
        }
    }

    public void showAttack() {
        currentFrames = cardFrames.getAttackFrames();
        currentIndex = -1;
    }

    public void showDeath() {
        currentFrames = cardFrames.getDeathFrames();
        currentIndex = -1;
    }

    public void showBreathing() {
        currentFrames = cardFrames.getBreathingFrames();
        currentIndex = -1;
    }

    public void showRun() {
        currentFrames = cardFrames.getRunFrames();
        currentIndex = -1;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public List<Frame> getCurrentFrames() {
        return currentFrames;
    }
}
