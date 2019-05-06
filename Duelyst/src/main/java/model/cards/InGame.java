package model.cards;

import model.buffs.Buff;

import java.util.ArrayList;

public class InGame implements Cloneable {
    private int healthPoint;
    private int attackPower;
    private boolean movable;
    private boolean armed;
    private ArrayList<Buff> buffs;
    private int holyNumber;

    InGame(int healthPoint, int attackPower){
        setHealthPoint(healthPoint);
        setAttackPower(attackPower);
        setMovable(true);
        setArmed(true);
        setBuffs(new ArrayList<>());
        setHolyNumber(0);
    }

    public InGame clone() throws CloneNotSupportedException {
        InGame inGame = (InGame)super.clone();
        inGame.buffs = new ArrayList<>(this.buffs);
        return inGame;
    }

    public int getHealthPoint() {
        return healthPoint;
    }
    public int getAttackPower() {
        return attackPower;
    }
    public int getHolyNumber(){
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
    public void setHolyNumber(int holyNumber){
        this.holyNumber = holyNumber;
    }
    public void addHolyNumber(int add){
        setHolyNumber(getHolyNumber() + add);
    }
    public void addAttackPower(int add){
        setAttackPower(getAttackPower() + add);
    }
    public void decreaseAttackPower(int decrease){
        setAttackPower(getAttackPower() - decrease);
    }
    public void addHealthPoint(int add){
        setHealthPoint(getHealthPoint() + add);
    }
    public void decreaseHealthPoint(int decrease){
        setHealthPoint(getHealthPoint() - decrease + holyNumber);
    }
}
