package model.cards;

import model.buffs.Buff;

import java.util.ArrayList;

public class Hero extends Card {
    private int healthPoint;
    private int attackPower;
    private int coolDown;
    private Spell specialPower;
    private AttackType attackType;
    private int range;
    private String info;
    private InGame inGame;
    private int specialPowerMana;
    private boolean onAttack;
    private boolean passive;


    public Hero(String name, long price, int healthPoint,
                int attackPower, AttackType attackType, int range, Spell specialPower, int coolDown) {
        super(name, price);
        this.coolDown = coolDown;
        setHealthPoint(healthPoint);
        setAttackPower(attackPower);
        setSpecialPower(specialPower);
        setAttackType(attackType);
        setRange(range);
        inGame = new InGame(healthPoint, attackPower, coolDown);
        makeInfo();
    }


    public Hero(String name, long price, int healthPoint, int attackPower, AttackType attackType, int range,
                Spell specialPower, int specialPowerMana, int coolDown) {
        super(name, price);
        this.coolDown = coolDown;
        this.specialPowerMana = specialPowerMana;
        setHealthPoint(healthPoint);
        setAttackPower(attackPower);
        setSpecialPower(specialPower);
        setAttackType(attackType);
        setRange(range);
        inGame = new InGame(healthPoint, attackPower, coolDown);
        makeInfo();
    }

    public Hero clone() throws CloneNotSupportedException {
        Hero hero = (Hero) super.clone();
        if (hero.specialPower != null) {
            hero.specialPower = hero.specialPower.clone();
        }
        hero.inGame = hero.inGame.clone();
        hero.inGame.setAttackPower(hero.attackPower);
        hero.inGame.setHealthPoint(hero.healthPoint);
        return hero;
    }

    public int getHealthPoint() {
        return healthPoint;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getHealthPointInGame() {
        return inGame.getHealthPoint();
    }

    public int getAttackPowerInGame() {
        return inGame.getAttackPower();
    }

    public Spell getSpecialPower() {
        return specialPower;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public int getRange() {
        return range;
    }

    public InGame getInGame() {
        return inGame;
    }

    public void setHealthPoint(int healthPoint) {
        this.healthPoint = healthPoint;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public void setHealthPointInGame(int healthPoint) {
        inGame.setHealthPoint(healthPoint);
    }

    public void setAttackPowerInGame(int attackPower) {
        inGame.setAttackPower(attackPower);
    }

    public void setSpecialPower(Spell specialPower) {
        this.specialPower = specialPower;
    }

    public void setAttackType(AttackType attackType) {
        this.attackType = attackType;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setArmed(boolean armed) {
        inGame.setArmed(armed);
    }

    public void setMovable(boolean movable) {
        inGame.setMovable(movable);
    }

    public void setBuffs(ArrayList<Buff> buffs) {
        inGame.setBuffs(buffs);
    }

    public void setHolyNumber(int holyNumber) {
        inGame.setHolyNumber(holyNumber);
    }

    public void addHolyNumber(int add) {
        inGame.addHolyNumber(add);
    }

    public void addAttackPowerInGame(int add) {
        inGame.addAttackPower(add);
    }

    public void decreaseAttackPowerInGame(int decrease) {
        inGame.decreaseAttackPower(decrease);
    }

    public void addHealthPointInGame(int add) {
        inGame.addHealthPoint(add);
    }

    public void decreaseHealthPointInGame(int decrease) {
        inGame.decreaseHealthPoint(decrease);
    }

    public void makeInfo() {
        info = "Name : " + getName() + " AP : " + getAttackPower();
        info += " HP : " + getHealthPoint() + " Class : " + getAttackType().toString();
        if (getSpecialPower() != null) {
            info += " Special Power : " + getSpecialPower().getDesc();
            info += " Cool Down : " + coolDown;
        }
    }

    public void initInGame() {
        inGame.setAttackPower(attackPower);
        inGame.setHealthPoint(healthPoint);
        inGame.setMovable(true);
        inGame.setArmed(true);
        inGame.setBuffs(new ArrayList<>());
        inGame.setHolyNumber(0);
    }

    @Override
    public String getInfoWithoutPrice() {
        return info;
    }

    @Override
    public String getInfoWithPrice() {
        return info + " Sell Cost : " + getPrice();
    }

    public void setOnAttack(boolean onAttack) {
        this.onAttack = onAttack;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }
}
