package view;

import java.util.Scanner;

public class View {
    private Scanner scanner = new Scanner(System.in);
    public String getInputAsString(){
        return scanner.nextLine().trim();
    }
    public void show(String message){
        System.out.println(message);
    }
}
