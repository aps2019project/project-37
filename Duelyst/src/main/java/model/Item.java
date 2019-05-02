package model;

public class Item implements Cloneable{
    private String id;
    private String name;
    private String desc;

    public Item(String name, String desc){
        setName(name);
        setDesc(desc);
    }
    public Item(String id, String name, String desc){
        setId(id);
        setName(name);
        setDesc(desc);
    }
    @Override
    public Item clone() throws CloneNotSupportedException {
        return (Item) super.clone();
    }
    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }
    public String getDesc() {
        return desc;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public boolean idEquals(String id){
        if(getId().equals(id)){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean nameEquals(String name){
        if(getName().equals(name)){
            return true;
        }
        else{
            return false;
        }
    }
    public String getInfo(){
        return "Name : " + getName() + " Desc : " + getDesc();
    }
}
