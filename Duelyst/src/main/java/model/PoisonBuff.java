package model;

public class PoisonBuff extends WeaknessBuff {
    PoisonBuff(int duration , boolean continuous){
        super(duration, continuous, DamageType.HEALTH, 1);
    }
}
