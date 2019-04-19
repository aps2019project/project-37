package model;

public class Hero extends Card {
    private int healthPoint;
    private int attackPower;
    private Spell specialPower;
    private Type attackType;
    private int range;
    private String info;
    Hero(long id, String name, long price, int healthPoint,
         int attackPower, Spell specialPower, Type attackType, int range){
        super(id, name, price);
        setHealthPoint(healthPoint);
        setAttackPower(attackPower);
        setSpecialPower(specialPower);
        setAttackType(attackType);
        setRange(range);
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

    @Override
    void showWithoutPrice() {
        System.out.println(info);
    }

    @Override
    void showWithPrice() {
        System.out.println(info + " Sell Cost : " + getPrice());
    }
}
