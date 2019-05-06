package model.items;

public class Flag extends Item {
    Flag(String name, String desc){
        super(name, desc);
    }

    @Override
    public Flag clone() throws CloneNotSupportedException {
        return (Flag) super.clone();
    }
}
