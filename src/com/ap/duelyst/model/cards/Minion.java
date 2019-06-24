package com.ap.duelyst.model.cards;

import com.ap.duelyst.model.buffs.traget.SideType;
import com.ap.duelyst.model.buffs.Buff;

import java.util.HashMap;
import java.util.Map;

public class Minion extends Hero {
    private int mana;
    private ActivationTime activationTime;
    private boolean disableEnemyHolyBuff;
    private Map<String, Integer> attackedTimes = new HashMap<>();

    public Minion(String name, long price, int mana, int healthPoint, int attackPower,
                  AttackType attackType, int range, Spell specialPower,
                  ActivationTime activationTime) {
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

    @Override
    public void makeInfo() {
        info = "Type : Minion" + " - Name : " + getName();
        info += " - Class : " + getAttackType().toString() + " - AP : " + getAttackPower();
        info += " - HP : " + getHealthPoint() + " - MP : " + getMana();

        info += " - Special Power : " + getSpecialPowerInfo();

    }


    @Override
    public String getInGameInfo() {
        String s = "Combo Ability: ";
        String desc = "Desc:\n";
        if (getSpecialPower() != null) {
            desc = "Desc: " + getSpecialPower().getDesc() + "\n";
        }
        if (activationTime == ActivationTime.COMBO) {
            s += "yes";
        } else {
            s += "no";
        }
        s += "\n";
        return "Minion:\n" +
                "Name: " + getName() + "\n" +
                "Range: " + getAttackType().toString() + "\n" +
                s +
                "Cost: " + getPrice() + "\n" +
                desc +
                "HP: " + getHealthPointInGame() + "\n" +
                "AP: " + getAttackPowerInGame() + "\n" +
                "MP: " + getMana() + "\n";
    }

    private String getSpecialPowerInfo() {
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
        return sp;
    }

    @Override
    public String getInfoWithoutPrice() {
        return info;
    }

    @Override
    public String getInfoWithPrice() {
        return info + " Sell Cost : " + getPrice();
    }

    @Override
    public void attack(Hero hero) {
        if (hero instanceof Minion) {
            Minion minion = (Minion) hero;
            if (getName().equals("persian-hero")) {
                minion.decreaseHealthPointInGame(minion.attackedTimes.get(minion.getName()) * 5 + getAttackPowerInGame());
                return;
            }
            if (getName().equals("double-headed-giant")) {
                for (Buff buff : minion.getInGame().getBuffs()) {
                    if (buff.getSide() == SideType.ALLY) {
                        if (buff.isCancelable()) {
                            buff.inactivate(hero);
                        }
                    }
                }
                minion.decreaseHealthPointInGame(getAttackPowerInGame());
                return;
            }
            if (disableEnemyHolyBuff) {
                minion.addHealthPointInGame(-getAttackPowerInGame());
            } else {
                minion.decreaseHealthPointInGame(getAttackPowerInGame());
            }
        } else {
            super.attack(hero);
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
