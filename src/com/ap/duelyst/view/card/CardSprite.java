package com.ap.duelyst.view.card;

import com.ap.duelyst.model.Utils;
import com.ap.duelyst.model.cards.CardFrames;
import com.ap.duelyst.plist.NSDictionary;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.List;

public class CardSprite extends Transition implements Cloneable {
    private ImageView imageView;
    private CardFrames cardFrames;
    private List<Frame> currentFrames;
    private int currentIndex;
    private String fileName;
    private List<Frame> spellEffectFrames;
    private String spellEffect;

    public CardSprite(String filename) {
        this(filename, null);
    }

    public CardSprite(String filename, String effectFileName) {
        this.imageView = new ImageView();
        if (filename!=null){
            this.fileName = filename;
            cardFrames = new CardFrames(filename);
            this.imageView = new ImageView(Utils.getPath(filename + ".png"));
            currentFrames = cardFrames.getBreathingFrames();
            if (currentFrames == null || currentFrames.isEmpty()) {
                currentFrames = cardFrames.getSpellInactiveFrames();
                imageView.setScaleX(.8);
                imageView.setScaleY(.8);
            } else {
                imageView.setScaleX(1.4);
                imageView.setScaleY(1.4);
                imageView.setTranslateY(-30);
            }
            imageView.setPreserveRatio(true);
        }
        setCycleDuration(Duration.seconds(1));
        setInterpolator(Interpolator.LINEAR);
        setCycleCount(Animation.INDEFINITE);
        if (effectFileName != null) {
            spellEffect = effectFileName;
            NSDictionary dic = Frame.parseRootDictionary(effectFileName);
            spellEffectFrames = Frame.getFrames(dic, FrameType.SPELL_EFFECT);
        }
    }

    @Override
    public CardSprite clone() {
        return new CardSprite(fileName, spellEffect);
    }

    @Override
    protected void interpolate(double frac) {
        final int index = Integer.min(currentFrames.size() - 1,
                (int) Math.floor(frac * currentFrames.size()));
        if (currentFrames.isEmpty())
            return;
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

    public void showInactive() {
        currentFrames = cardFrames.getSpellInactiveFrames();
        currentIndex = -1;
    }

    public void showActive() {
        currentFrames = cardFrames.getSpellActiveFrames();
        currentIndex = -1;
    }

    public void showEffect() {
        currentIndex = -1;
        currentFrames = spellEffectFrames;
        setCycleCount(1);
        setCycleDuration(Duration.seconds(2));
        playFromStart();
        imageView.setImage(new Image(Utils.getPath(spellEffect + ".png")));
        imageView.setScaleX(2);
        imageView.setScaleY(2);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public boolean hasEffect(){
        return spellEffect != null;
    }
}
