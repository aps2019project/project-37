package model.cards;

import java.util.HashMap;
import java.util.Map;

public class Minion extends Hero {
    private int mana;
    private String info;
    private ActivationTime activationTime;
    private boolean disableEnemyHolyBuff;
    private Map<String, Integer> attackedTimes = new HashMap<>();

    public Minion(String name, long price, int mana, int healthPoint, int attackPower,
                  AttackType attackType, int range, Spell specialPower, ActivationTime activationTime) {
        super(name, price, healthPoint, attackPower, attackType, range, specialPower, 0);
        this.activationTime = activationTime;
        setMana(mana);
        makeInfo();
    }

    @Override
    public Minion clone() throws CloneNotSupportedException {
        attackedTimes = new HashMap<>();
        return (Minion) super.clone();
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void makeInfo() {
        info = "Type : Minion" + " - Name : " + getName();
        info += " - Class : " + getAttackType().toString() + " - AP : " + getAttackPower();
        info += " - HP : " + getHealthPoint() + " - MP : " + getMana();
        String sp = "";
        if (getSpecialPower() != null) {
            sp = getSpecialPower().getDesc();
        }
        if (disableEnemyHolyBuff) {
            sp = "disables enemy holy buff";
        }
        if (activationTime != null) {
            sp += " " + activationTime.name().toLowerCase().replace("_", " ");
        }
        info += " - Special Power : " + sp;

    }

    @Override
    public String getInfoWithoutPrice() {
        return info;
    }

    @Override
    public String getInfoWithPrice() {
        return info + " Sell Cost : " + getPrice();
    }

    public void attack(Minion minion, int damage) {
        if (getName().equals("persian-hero")) {
            minion.decreaseHealthPointInGame(minion.attackedTimes.get(minion.getName())*5 + damage);
            return;
        }
        if (disableEnemyHolyBuff) {
            minion.addHealthPointInGame(-damage);
        } else {
            minion.decreaseHealthPointInGame(damage);
        }
    }

    public ActivationTime getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(ActivationTime activationTime) {
        this.activationTime = activationTime;
    }

    public void setDisableEnemyHolyBuff(boolean disableEnemyHolyBuff) {
        this.disableEnemyHolyBuff = disableEnemyHolyBuff;
    }

    public boolean isEnemyHolyBuffDisabled() {
        return this.disableEnemyHolyBuff;
    }

    public void addAttackedTimes(Minion minion) {
        attackedTimes.put(minion.getName(), getAttackedTimes(minion) + 1);
    }

    public int getAttackedTimes(Minion minion) {
        return attackedTimes.get(minion.getName());
    }
}
