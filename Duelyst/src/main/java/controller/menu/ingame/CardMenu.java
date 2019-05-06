package controller.menu.ingame;

import controller.Controller;
import controller.GameException;
import controller.menu.Menu;
import model.cards.Card;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardMenu extends Menu {

    enum CardType {
        MOVE_TO(0),
        ATTACK(1),
        ATTACK_COMBO(2),
        SPECIAL_POWER(3),
        SHOW_MENU(4),
        HELP(5),
        EXIT(6);

        int commandIndex;

        CardType(int commandIndex) {
            this.commandIndex = commandIndex;
        }

        public static CardType getCommandType(int commandIndex) {
            for (CardType commandType : values()) {
                if (commandType.commandIndex == commandIndex) {
                    return commandType;
                }
            }
            return null;
        }

    }
    private String[] matchers = {
            "^move to \\(\\d+,\\d+\\)",
            "^attack \\S+$",
            "^attack combo \\S+ (\\S+)+$",
            "^use special power \\(\\d+,\\d+\\)",
            "^show menu$",
            "^help$",
            "^exit$"
    };
    private CardType type;
    public CardMenu(Controller controller) {
        super(controller);
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        match(command);
        int x, y;
        String opponentId;
        String[] allyCardIds;
        switch (type){
            case MOVE_TO:
                x = extractXForMove(command);
                y = extractYForMove(command);
                getController().moveSelectedCardTo(x, y);
                break;
            case ATTACK:
                opponentId = extractLastWord(command);
                getController().attackSelectedCardOn(opponentId);
                break;
            case SPECIAL_POWER:
                x = extractXForMove(command);
                y = extractYForMove(command);
                getController().useSpecialPowerOfSelectedCardOn(x, y);
                break;
            case ATTACK_COMBO:
                opponentId = extractOpponentIdForCombo(command);
                allyCardIds = extractMyCardIdsForCombo(command);
                getController().attackCombo(opponentId, allyCardIds);
                break;
            case SHOW_MENU:
                showMessage(menuHelp());
                break;
            case HELP:
                showMessage(getHelpOfPossibleCommands());
                break;
            case EXIT:
                return getParentMenu();
        }
        return this;
    }

    private void match(String command) {
        for (int i = 0; i < matchers.length; i++) {
            String matcher = matchers[i];
            if (command.matches(matcher)) {
                type = CardType.getCommandType(i);
                return;
            }
        }
        throw new GameException("invalid command");
    }

    private String extractLastWord(String command) {
        String[] strings = command.split(" ");
        return strings[strings.length - 1];
    }

    private int extractXForMove(String command){
        String[] strings = command.split(" ");
        String string = strings[strings.length - 1];
        Pattern pattern = Pattern.compile("\\d+(?=,)");
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()){
            return Integer.parseInt(matcher.group(0));
        }else{
            return -1;
        }
    }

    private int extractYForMove(String command){
        String[] strings = command.split(" ");
        String string = strings[strings.length - 1];
        Pattern pattern = Pattern.compile("(?<=,)\\d+");
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()){
            return Integer.parseInt(matcher.group(0));
        }else{
            return -1;
        }
    }

    private String[] extractMyCardIdsForCombo(String command){
        String str = command.replaceFirst("attack combo \\S+ ","");
        return str.split(" ");
    }

    private String extractOpponentIdForCombo(String command){
        String[] strings = command.split(" ");
        return strings[2];
    }

    private String menuHelp() {
        return "\nIn Game Menu\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- move to (x,y)\n" +
                "2- attack [opponent card id]\n" +
                "3- Attack combo [opponent card id] [my card id] [my card id] [...]\n" +
                "4- use special power (x,y)\n" +
                "5- show menu\n" +
                "6- help\n" +
                "7- exit\n";
    }
    public String getHelpOfPossibleCommands(){
        int count = 1;
        StringBuilder help = new StringBuilder();
        help.append("Movable Cards:\n");
        for(Card card:getController().getMovableCards()){
            help.append(count + card.getId()+"\n");
        }
        count = 1;
        help.append("Attacking Cards:\n");
        for(Card card:getController().getAttackingCards()){
            help.append(count + card.getId()+"\n");
        }
        return help.toString();
    }
}
