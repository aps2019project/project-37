package model;

public class Item {
    private long id;
    private String name;
    private String desc;

    Item(long id, String name, String desc){
        setName(name);
        setId(id);
        setDesc(desc);
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInfo(){
        return "Name : " + getName() + " Desc : " + getDesc();
    }

}
