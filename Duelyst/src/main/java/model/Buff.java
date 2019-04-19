package model;

public abstract class Buff{
    private int duration;
    private int remainingTime;

    public void initRemainingTime(){
        setRemainingTime(duration);
    }
    public int getDuration() {
        return duration;
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
    abstract void applyBuff();
}

