package com.ap.duelyst.model.cards;


import com.ap.duelyst.view.card.CardSprite;
import javafx.scene.image.ImageView;

public abstract class Card implements Cloneable {
    private transient ImageView imageView;
    private String fileName;
    transient CardSprite cardSprite;
    private String id;
    private String name;
    private long price;
    private String accountName;
    private int count = 10;

    Card(String name, long price) {
        setName(name);
        setPrice(price);
    }

    Card(String id, String name, long price) {
        setId(id);
        setName(name);
        setPrice(price);
    }

    public Card clone() throws CloneNotSupportedException {
        Card card = (Card) super.clone();
        card.cardSprite = null;
        return card;
    }

    public void makeCardSprite() {
        this.cardSprite = new CardSprite(fileName);
        this.imageView = this.cardSprite.getImageView();
    }

    public void setCardSprite(CardSprite cardSprite) {
        this.cardSprite = cardSprite;
        this.imageView = this.cardSprite.getImageView();
    }

    public CardSprite getCardSprite() {
        return cardSprite;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public String getAccountName() {
        return accountName;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public boolean idEquals(String id) {
        if (getId().equals(id)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean nameEquals(String name) {
        if (getName().equals(name)) {
            return true;
        } else {
            return false;
        }
    }

    public abstract String getInfoWithPrice();

    public abstract String getInfoWithoutPrice();

    public abstract String getInGameInfo();

    public String getFileName() {
        return fileName;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        count++;
    }public void decreaseCount() {
        count--;
    }
}

