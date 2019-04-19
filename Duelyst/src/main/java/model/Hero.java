package model;

import java.util.ArrayList;

public class Hero extends Card {
    private int healthPoint;
    private int attackPower;
    private Spell specialPower;
    private Type attackType;
    private int range;
    private String info;
    private boolean movable;
    private boolean armed;
    private ArrayList<Buff> buffs;

    Hero(long id, String name, long price, int healthPoint,
         int attackPower, Spell specialPower, Type attackType, int range){
        super(id, name, price);
        setHealthPoint(healthPoint);
        setAttackPower(attackPower);
        setSpecialPower(specialPower);
        setAttackType(attackType);
        setRange(range);
        setMovable(true);
        setArmed(true);
        setBuffs(new ArrayList<>());
    }

    {
        info  = "Name : " + getName() + " AP : " + getAttackPower();
        info += " HP : " + getHealthPoint() + " Class : " + getAttackType().toString();
        info += " Special Power : "+ getSpecialPower().getDesc();
    }
    public int getHealthPoint() {
        return healthPoint;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public Spell getSpecialPower() {
        return specialPower;
    }

    public Type getAttackType() {
        return attackType;
    }

    public int getRange() {
        return range;
    }

    public boolean isArmed() {
        return armed;
    }

    public boolean isMovable() {
        return movable;
    }

    public ArrayList<Buff> getBuffs(){
        return buffs;
    }

    public void setHealthPoint(int healthPoint) {
        this.healthPoint = healthPoint;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public void setSpecialPower(Spell specialPower) {
        this.specialPower = specialPower;
    }

    public void setAttackType(Type attackType) {
        this.attackType = attackType;
    }

    public void setRange(int range) {
        this.range = range;
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

    @Override
    void showWithoutPrice() {
        System.out.println(info);
    }

    @Override
    void showWithPrice() {
        System.out.println(info + " Sell Cost : " + getPrice());
    }
}
