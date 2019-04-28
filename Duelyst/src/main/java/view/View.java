package view;

import java.util.Scanner;

public class View {
    Scanner scanner = new Scanner(System.in);
    public String getInputAsString(){
        return scanner.nextLine();
    }
    public void show(String message){
        System.out.println(message);
    }
}
