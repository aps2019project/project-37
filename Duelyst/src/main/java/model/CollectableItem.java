package model;

public class CollectableItem extends Item{
    CollectableItem(String name, String desc){
        super(name, desc);
    }
    CollectableItem(String id, CollectableItem collectable){
        super(id, collectable.getName(), collectable.getDesc());
    }

    @Override
    public CollectableItem clone() throws CloneNotSupportedException {
        return (CollectableItem) super.clone();
    }
}
