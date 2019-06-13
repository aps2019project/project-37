package com.ap.duelyst.model.buffs.traget;

public enum TargetType {
    CELL("Cell"),
    HERO("Hero"),
    MINION("Minion"),
    ALL_MINIONS("All Minions"),
    HERO_MINION("Hero/Minion");

    private String type;

    TargetType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
