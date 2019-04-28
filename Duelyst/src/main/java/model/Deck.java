package model;

import java.awt.*;
import java.util.ArrayList;

public class Deck extends Collection{
    private String name;
    Deck(String name){
        setName(name);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean hasOneHero(){
        if(getHeroes().size() == 1){
            return true;
        }else {
            return false;
        }
    }
    public String getInfo(){
        StringBuilder info = new StringBuilder();

        info.append("Heroes :\n");
        if(!getHeroes().isEmpty()){
            info.append(getInfoOfHeroes());
        }

        info.append("Items :\n");
        if(!getItems().isEmpty()){
            info.append(getInfoOfItems());
        }

        info.append("Cards:\n");
        if(!getCards().isEmpty()){
            info.append(getInfoOfCards());
        }
        return info.toString();
    }
    public String getInfoOfHeroes(){
        StringBuilder info = new StringBuilder();
        if(!getHeroes().isEmpty()){
            for(int i = 0; i < getHeroes().size(); i++){
                info.append("\t" + (i+1) + " : " + getHeroes().get(i).getInfoWithoutPrice() + "\n");
            }
        }
        return info.toString();
    }
    public String getInfoOfItems(){
        StringBuilder info = new StringBuilder();

        if(!getItems().isEmpty()){
            for(int i = 0; i < getItems().size(); i++){
                Item item = getItems().get(i);
                info.append("\t" + (i+1) + " : " + item.getInfo() + "\n");
            }
        }

        return info.toString();
    }
    public String getInfoOfCards(){
        StringBuilder info = new StringBuilder();
        if(!getCards().isEmpty()){
            for(int i = 0; i < getCards().size(); i++){
                info.append("\t" + (i+1) + " : " + getCards().get(i).getInfoWithoutPrice() + "\n");
            }
        }
        return info.toString();
    }
    public boolean nameEquals(String name){
        if(getName().equals(name)){
            return true;
        }else{
            return false;
        }
    }
}
