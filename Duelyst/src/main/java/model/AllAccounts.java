package model;

import controller.GameException;
import java.util.TreeSet;

public class AllAccounts {
    TreeSet<Account> accounts = new TreeSet<>();

    public TreeSet<Account> getAccounts(){
        return accounts;
    }
    public void add(Account account){
        accounts.add(account);
    }
    public boolean hasAccount(String userName){
        for(Account account:accounts){
            if(account.userNameEquals(userName)){
                return true;
            }
        }
        return false;
    }
    public Account getAccount(String userName){
        for(Account account:accounts){
            if(account.userNameEquals(userName)){
                return account;
            }
        }
        new GameException("No Account with this user name!");
        return null;
    }
}
