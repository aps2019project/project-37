package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AllAccounts {
    private List<Account> accounts = new ArrayList<>();
    private Shop shop = new Shop();


    public void add(Account account) {
        accounts.add(account);
    }

    public boolean hasAccount(String userName) {
        return getAccountByUsername(userName) != null;
    }

    public Account getAccountByUsername(String userName) {
        return accounts.stream().filter(account -> userName.equals(account.getUserName()))
                .findFirst().orElse(null);
    }

    public List<Account> getSortedAccounts() {
        return accounts.stream()
                .sorted(Comparator
                        .comparing(Account::getWins).reversed()
                        .thenComparing(Account::getUserName))
                .collect(Collectors.toList());
    }

    public Shop getShop() {
        return shop;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

}
