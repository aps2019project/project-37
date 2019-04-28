package controller;

import controller.menu.MenuManager;
import controller.menu.Menu;
import model.Account;
import model.AllAccounts;
import view.View;

public class Controller {
    private View view;
    private Account currentAccount;
    private AllAccounts allAccounts;
    private MenuManager menuManager;

    public Controller(){
        view = new View();
        allAccounts = new AllAccounts();
        menuManager = new MenuManager(this);
    }
    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }
    public void runCommand(String command){
        Menu currentMenu = menuManager.getCurrentMenu();
        Menu nextMenu =  currentMenu.runCommandAndGetNextMenu(command);
        menuManager.setCurrentMenu(nextMenu);
    }
    public void createAccount(String userName){
        if(allAccounts.hasAccount(userName)){
            new GameException("There is an account with this user name!");
        }
        else{
            Account account = new Account();
            account.setUserName(userName);
            view.show("Enter Password:");
            account.setPassword(view.getInputAsString());
        }
    }
    public void login(String userName){
        if(currentAccount.equals(null)){
            new GameException("You should log out!");
        }
        Account account = allAccounts.getAccount(userName);
        showMessage("Enter the password:");
        String password = getInputAsString();
        if(account.getPassword().equals(password)){
            setCurrentAccount(account);
        }
        else {
            new GameException("Entered password is not correct!");
        }
    }
    public void showLeaderBoard(){
        StringBuilder leaderBoard = new StringBuilder();
        int count = 1;
        if(allAccounts.getAccounts().isEmpty()){
            new GameException("There is no account!");
        }
        for(Account account:allAccounts.getAccounts()){
            leaderBoard.append(count + "-" + account.getInfo()+"\n");
            count ++;
        }
    }
    public void save(){}
    public void logOut(){
        if(currentAccount.equals(null)){
            new GameException("You are not logged in!");
        }
        else{
            setCurrentAccount(null);
        }
    }
    public void showMessage(String message){
        view.show(message);
    }
    public String getInputAsString(){
        return view.getInputAsString();
    }
}
