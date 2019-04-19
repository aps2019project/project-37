package model;

public enum Type {
    MELEE ("Melee"),
    RANGED ("Ranged"),
    HYBRID ("Hybrid");

    String type;
    Type(String type){
        this.type = type;
    }
    public String toString(){
        return type;
    }
}
