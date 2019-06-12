package duelyst.model.cards;

public enum AttackType {
    MELEE("Melee"),
    RANGED("Ranged"),
    HYBRID("Hybrid");

    String attackType;

    AttackType(String type) {
        this.attackType = type;
    }

    public String toString() {
        return attackType;
    }
}
