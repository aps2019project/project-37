package com.ap.duelyst.model.cards;

import com.ap.duelyst.plist.NSDictionary;
import com.ap.duelyst.view.card.Frame;
import com.ap.duelyst.view.card.FrameType;

import java.util.List;

public class CardFrames {
    private List<Frame> attackFrames;
    private List<Frame> runFrames;
    private List<Frame> breathingFrames;
    private List<Frame> deathFrames;

    public CardFrames(String filename) {
        NSDictionary rootDict = Frame.parseRootDictionary(filename);
        breathingFrames = Frame.getFrames(rootDict, FrameType.BREATHING);
        attackFrames = Frame.getFrames(rootDict, FrameType.ATTACK);
        runFrames = Frame.getFrames(rootDict, FrameType.RUN);
        deathFrames = Frame.getFrames(rootDict, FrameType.DEATH);
    }

    public List<Frame> getAttackFrames() {
        return attackFrames;
    }

    public void setAttackFrames(List<Frame> attackFrames) {
        this.attackFrames = attackFrames;
    }

    public List<Frame> getRunFrames() {
        return runFrames;
    }

    public void setRunFrames(List<Frame> runFrames) {
        this.runFrames = runFrames;
    }

    public List<Frame> getBreathingFrames() {
        return breathingFrames;
    }

    public void setBreathingFrames(List<Frame> breathingFrames) {
        this.breathingFrames = breathingFrames;
    }

    public List<Frame> getDeathFrames() {
        return deathFrames;
    }

    public void setDeathFrames(List<Frame> deathFrames) {
        this.deathFrames = deathFrames;
    }
}
