package model;

public class PoisonBuff extends WeaknessBuff {
    PoisonBuff(int duration){
        super(duration, DamageType.HEALTH, 1);
    }
}
