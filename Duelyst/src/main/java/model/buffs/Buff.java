package model.buffs;

import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.AttackType;
import model.cards.Hero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static model.cards.AttackType.*;

public abstract class Buff implements Cloneable {
    private int duration;
    private boolean continuous;
    private int remainingTime;
    private boolean random;
    private boolean cancelable = true;
    private TargetType target;
    private SideType side;
    private RangeType range;
    private List<AttackType> allyAttackTypes;
    private List<AttackType> enemyAttackTypes;
    private TargetType allyType;


    public Buff(int duration, boolean continuous, TargetType target, SideType side, RangeType range) {
        this.duration = duration;
        this.continuous = continuous;
        this.target = target;
        this.side = side;
        this.range = range;
        allyAttackTypes = Arrays.asList(MELEE, RANGED, HYBRID);
        enemyAttackTypes = Arrays.asList(MELEE, RANGED, HYBRID);
    }

    public Buff(int duration, boolean continuous, int remainingTime, TargetType target, SideType side,
                RangeType range) {
        this.duration = duration;
        this.continuous = continuous;
        this.remainingTime = remainingTime;
        this.target = target;
        this.side = side;
        this.range = range;
        allyAttackTypes = Arrays.asList(MELEE, RANGED, HYBRID);
        enemyAttackTypes = Arrays.asList(MELEE, RANGED, HYBRID);
    }


    @Override
    public Buff clone() throws CloneNotSupportedException {
        Buff buff= (Buff) super.clone();
        buff.setRemainingTime(duration);
        return buff;
    }

    public void initRemainingTime() {
        setRemainingTime(duration);
    }

    public int getDuration() {
        return duration;
    }

    public TargetType getTarget() {
        return target;
    }

    public SideType getSide() {
        return side;
    }

    public RangeType getRange() {
        return range;
    }

    public boolean isRandom() {
        return random;
    }

    public boolean isContinuous() {
        return continuous;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void decreaseRemainingTime() {
        remainingTime--;
    }

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    public abstract void applyBuff(Hero hero);

    public abstract void inactivate(Hero hero);

    public void setRandom(boolean random) {
        this.random = random;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public List<AttackType> getAllyAttackTypes() {
        return allyAttackTypes;
    }

    public void setAllyAttackTypes(AttackType... allyAttackTypes) {
        this.allyAttackTypes = Arrays.asList(allyAttackTypes);
    }

    public List<AttackType> getEnemyAttackTypes() {
        return enemyAttackTypes;
    }

    public void setEnemyAttackTypes(AttackType... enemyAttackTypes) {
        this.enemyAttackTypes = Arrays.asList(enemyAttackTypes);
    }

    public TargetType getAllyType() {
        return allyType;
    }

    public void setAllyType(TargetType allyType) {
        this.allyType = allyType;
    }
}

