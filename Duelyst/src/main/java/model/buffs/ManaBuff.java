package model.buffs;

import model.buffs.traget.RangeType;
import model.buffs.traget.SideType;
import model.buffs.traget.TargetType;
import model.cards.Hero;
import model.game.Player;

import java.util.List;

public class ManaBuff extends Buff {

    private int amount;
    private Player player;

    public ManaBuff(int duration, boolean continuous, TargetType target, SideType side, RangeType range, int amount) {
        super(duration, continuous, target, side, range);
        this.amount = amount;
        setCancelable(false);
    }

    @Override
    void applyBuff(List<Hero> heroes) {
        player.setMana(player.getMana() + amount);
    }

    @Override
    void inactivate(List<Hero> heroes) {

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
