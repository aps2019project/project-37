package model.items;

public class CollectableItem extends Item{
    CollectableItem(String name, String desc){
        super(name, desc);
    }
    @Override
    public CollectableItem clone() throws CloneNotSupportedException {
        return (CollectableItem) super.clone();
    }
}
