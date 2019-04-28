package model;

import java.util.ArrayList;

public class Collection extends Shop{
    public ArrayList<String> getIdsByName(String name){
        ArrayList<String> ids = new ArrayList<>();
        if(hasCardByName(name)){
            for(Card card:getCards()){
                if(card.nameEquals(name)){
                    ids.add(card.getId());
                }
            }
        }
        if(hasItemByName(name)){
            for(Item item:getItems()){
                if(item.nameEquals(name)){
                    ids.add(item.getName());
                }
            }
        }
        return ids;
    }
    public void removeById(String id){
        if(hasCardById(id)){
            remove(getCardById(id));
        }else if(hasItemById(id)){
            remove(getItemById(id));
        }
    }
    public boolean hasById(String id){
        if(hasCardById(id) | hasItemById(id)){
            return true;
        }else {
            return false;
        }
    }
    public boolean hasCardById(String id){
        if(getCardById(id).equals(null)){
            return false;
        }
        return true;
    }
    public boolean hasItemById(String id){
        if(getItemById(id).equals(null)){
            return false;
        }
        return true;
    }
    public Card getCardById(String id){
        for(Card card:getCards()){
            if(card.idEquals(id)){
                return card;
            }
        }
        return null;
    }
    public Item getItemById(String id){
        for(Item item:getItems()){
            if(item.idEquals(id)){
                return item;
            }
        }
        return null;
    }
}
