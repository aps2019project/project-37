package model;

import model.cards.Card;
import model.items.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private static List<Account> accounts = new ArrayList<>();
    private static Shop shop;

    static {
        List<Card> cards = new ArrayList<>();
        List<Item> items = new ArrayList<>();

        shop = new Shop(cards, items);
    }

    public static void add(Account account) {
        accounts.add(account);
    }

    public static boolean hasAccount(String userName) {
        return getAccountByUsername(userName) != null;
    }

    public static Account getAccountByUsername(String userName) {
        return accounts.stream().filter(account -> userName.equals(account.getUserName()))
                .findFirst().orElse(null);
    }

    public static List<Account> getSortedAccounts() {
        return accounts.stream()
                .sorted(Comparator
                        .comparing(Account::getWins).reversed()
                        .thenComparing(Account::getUserName))
                .collect(Collectors.toList());
    }

    public static Shop getShop() {
        return shop;
    }

    public static List<Account> getAccounts() {
        return accounts;
    }

}
