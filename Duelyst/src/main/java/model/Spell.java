package model;

public class Spell extends Card{
    private Target target;
    private Buff effect;
    private int mana;
    private String desc;
    Spell(long id, long price, String name, Target target, Buff effect, int mana){
        super(id, name, price);
        setTarget(target);
        setEffect(effect);
        setMana(mana);
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void setEffect(Buff effect) {
        this.effect = effect;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setDesc(String desc){
        this.desc = desc;
    }
    public Target getTarget() {
        return target;
    }

    public Buff getEffect() {
        return effect;
    }

    public int getMana() {
        return mana;
    }

    public String getDesc(){
        return desc;
    }
    @Override
    void showWithPrice() {
        String show;
        show = "Type : Spell - Name : " + getName();
        show = show + " - MP : " + getMana() + " Description : " + getDesc();
        System.out.println(show);
    }

    @Override
    void showWithoutPrice() {
        String show ;
        show = "Type : Spell - Name : " + getName();
        show = show + " - MP : " + getMana() + " Description : " + getDesc();
        show = show + " - Sell Cost : " + getPrice();
        System.out.println(show);
    }
}
