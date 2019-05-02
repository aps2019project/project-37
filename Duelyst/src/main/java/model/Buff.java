package model;

public abstract class Buff implements Cloneable{
    private int duration;
    private boolean continuous;
    private int remainingTime;

    Buff(int duration, boolean continuous){
        setDuration(duration);
        initRemainingTime();
        setContinuous(continuous);
    }

    @Override
    protected Buff clone() throws CloneNotSupportedException {
        return (Buff) super.clone();
    }

    public void initRemainingTime(){
        setRemainingTime(duration);
    }
    public int getDuration() {
        return duration;
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

    public void setContinuous(boolean continuous) {
        this.continuous = continuous;
    }

    abstract void applyBuff(Hero hero);
}

