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

public class CardSprite extends Transition {
    private ImageView imageView;
    private CardFrames cardFrames;
    private List<Frame> currentFrames;
    private FrameType frameType;
    private int currentIndex;

    public CardSprite(String filename) {
        cardFrames = new CardFrames(filename);
        currentFrames = cardFrames.getBreathingFrames();
        frameType = FrameType.BREATHING;
        currentIndex = 0;
        Image image = new Image(Utils.getPath(filename + ".png"));
        this.imageView = new ImageView(image);
        Frame currentFrame = currentFrames.get(currentIndex);
        imageView.setTranslateY(-20);
        imageView.setScaleX(1.5);
        imageView.setScaleY(1.5);
        imageView.setViewport(new Rectangle2D(
                currentFrame.getPosX(), currentFrame.getPosY(),
                currentFrame.getWidth(), currentFrame.getHeight()));
        setCycleDuration(Duration.millis(1500));
        setInterpolator(Interpolator.LINEAR);
        setCycleCount(Animation.INDEFINITE);
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
            if (index == currentFrames.size() - 1) {
                if (frameType == FrameType.ATTACK) {
                    currentFrames = cardFrames.getBreathingFrames();
                    frameType = FrameType.BREATHING;
                } else if (frameType == FrameType.DEATH) {
                    imageView.setImage(null);
                }
            }
        }
    }

    public void showAttack() {
        currentFrames = cardFrames.getAttackFrames();
        frameType = FrameType.ATTACK;
        currentIndex = -1;
    }

    public void showDeath() {
        currentFrames = cardFrames.getDeathFrames();
        frameType = FrameType.DEATH;
        currentIndex = -1;
    }

    public void showBreathing() {
        currentFrames = cardFrames.getBreathingFrames();
        frameType = FrameType.BREATHING;
        currentIndex = -1;
    }

    public void showRun() {
        currentFrames = cardFrames.getRunFrames();
        frameType = FrameType.RUN;
        currentIndex = -1;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public List<Frame> getCurrentFrames() {
        return currentFrames;
    }
}
