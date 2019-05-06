package model.cards;

import model.buffs.Buff;

import java.util.ArrayList;

public class InGame implements Cloneable {
    private int healthPoint;
    private int attackPower;
    private int coolDown;
    private boolean movable;
    private boolean armed;
    private ArrayList<Buff> buffs;
    private int holyNumber;
    private boolean enemy;

    InGame(int healthPoint, int attackPower, int coolDown) {
        this.coolDown = coolDown;
        setHealthPoint(healthPoint);
        setAttackPower(attackPower);
        setMovable(true);
        setArmed(true);
        setBuffs(new ArrayList<>());
        setHolyNumber(0);
    }

    public InGame clone() throws CloneNotSupportedException {
        InGame inGame = (InGame) super.clone();
        inGame.setMovable(true);
        inGame.setArmed(true);
        inGame.setBuffs(new ArrayList<>());
        inGame.setHolyNumber(0);
        return inGame;
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getHolyNumber() {
        return holyNumber;
    }

    public boolean isArmed() {
        return armed;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setHealthPoint(int healthPoint) {
        this.healthPoint = healthPoint;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setBuffs(ArrayList<Buff> buffs) {
        this.buffs = buffs;
    }

    public ArrayList<Buff> getBuffs() {
        return buffs;
    }

    public void addBuff(Buff buff) {
        buffs.add(buff);
    }

    public void removeBuff(Buff buff) {
        buffs.remove(buff);
    }

    public void setHolyNumber(int holyNumber) {
        this.holyNumber = holyNumber;
    }

    public void addHolyNumber(int add) {
        setHolyNumber(getHolyNumber() + add);
    }

    public void addAttackPower(int add) {
        setAttackPower(getAttackPower() + add);
    }

    public void decreaseAttackPower(int decrease) {
        setAttackPower(getAttackPower() - decrease);
    }

    public void addHealthPoint(int add) {
        setHealthPoint(getHealthPoint() + add);
    }

    public void decreaseHealthPoint(int decrease) {
        setHealthPoint(getHealthPoint() - decrease + holyNumber);
    }

    public boolean isEnemy() {
        return enemy;
    }

    public void setIsEnemy(boolean enemy) {
        this.enemy = enemy;
    }
}
