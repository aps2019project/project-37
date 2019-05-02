package model;

public class Flag extends Item {
    Flag(String name, String desc){
        super(name, desc);
    }
    Flag(String id, Flag flag){
        super(id, flag.getName(), flag.getDesc());
    }

    @Override
    public Flag clone() throws CloneNotSupportedException {
        return (Flag) super.clone();
    }
}
