package model;

public class Collection extends Shop{
    public String getIdsByName(String name){
        StringBuilder ids = new StringBuilder();
        if(hasCardByName(name)){
            for(Card card:getCards()){
                if(card.nameEquals(name)){
                    ids.append(card.getId()+"\n");
                }
            }
        }
        if(hasUsableItemByName(name)){
            for(UsableItem usableItem:getUsableItems()){
                if(usableItem.nameEquals(name)){
                    ids.append(usableItem.getName()+"\n");
                }
            }
        }
        return ids.toString();
    }
    public void removeById(String id){
        if(hasCardById(id)){
            remove(getCardById(id));
        }else if(hasUsableItemById(id)){
            remove(getUsableItemById(id));
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
    public Card getCardById(String id){
        for(Card card:getCards()){
            if(card.idEquals(id)){
                return card;
            }
        }
        return null;
    }
    public UsableItem getUsableItemById(String id){
        for(UsableItem usableItem:getUsableItems()){
            if(usableItem.idEquals(id)){
                return usableItem;
            }
        }
        return null;
    }
}
