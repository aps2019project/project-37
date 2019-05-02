package model;

import controller.GameException;

import java.util.Optional;

public class Collection extends Shop{
    public String getIdsByName(String name){
        StringBuilder ids = new StringBuilder();
        getCards()
                .stream()
                .filter(card -> card.nameEquals(name))
                .forEach(card -> ids.append(card.getId()+"\n"));
        getUsableItems()
                .stream()
                .filter(usableItem -> usableItem.nameEquals(name))
                .forEach(usableItem -> ids.append(usableItem.getId()+"\n"));
        if(!ids.toString().isEmpty()){
            return ids.toString();
        }else{
            throw new GameException("No Card or item with this name");
        }
    }
    public void removeById(String id){
        Object object = getObjectById(id);
        if(object instanceof  Card){
            remove((Card) object);
        }else if(object instanceof UsableItem){
            remove((UsableItem) object);
        }
    }
    public boolean hasById(String id){
        if(hasCardById(id) | hasUsableItemById(id)){
            return true;
        }else {
            return false;
        }
    }
    public boolean hasCardById(String id){
        if(getCardById(id)== null){
            return false;
        }
        return true;
    }
    public boolean hasUsableItemById(String id){
        if(getUsableItemById(id)== null){
            return false;
        }
        return true;
    }
    public Object getObjectById(String id){
        return getCardById(id)
                .map(Object.class::cast)
                .or(() -> getUsableItemById(id))
                .orElseThrow(() -> new GameException("No Card or item with this id!"));
    }
    private Optional<Card> getCardById(String id){
        return getCards()
                .stream()
                .filter(card -> card.idEquals(id))
                .findFirst();
    }
    private Optional<UsableItem> getUsableItemById(String id){
        return getUsableItems()
                .stream()
                .filter(usableItem -> usableItem.idEquals(id))
                .findFirst();
    }
    public void add(UsableItem usableItem){
        if(getItems().size()<3){
            super.add(usableItem);
        }else {
            throw new GameException("You have 3 Items in your Collection!");
        }
    }
}
