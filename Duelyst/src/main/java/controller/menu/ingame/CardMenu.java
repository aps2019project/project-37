package controller.menu.ingame;

import controller.Controller;
import controller.GameException;
import controller.menu.Menu;
import model.cards.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardMenu extends Menu {

    enum CardType {
        MOVE_TO(0),
        ATTACK(1),
        ATTACK_COMBO(2),
        INSERT(3),
        SPECIAL_POWER(4),
        SHOW_MENU(5),
        HELP(6),
        EXIT(7);

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
            "^move to \\(\\d+,\\d+\\)$",
            "^attack \\S+$",
            "^attack combo \\S+ (\\S+)+$",
            "^insert \\(\\d+,\\d+\\)$",
            "^use special power \\(\\d+,\\d+\\)$",
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
        String opponentId;
        String[] allyCardIds;
        switch (type) {
            case MOVE_TO:
                List<Integer> pos = getPosition(command);
                getController().moveSelectedCardTo(pos.get(0), pos.get(1));
                break;
            case ATTACK:
                opponentId = extractLastWord(command);
                getController().attackSelectedCardOn(opponentId);
                break;
            case INSERT:
                pos = getPosition(command);
                getController().insertCardInXY(pos.get(0), pos.get(1));
                break;
            case SPECIAL_POWER:
                pos = getPosition(command);
                getController().useSpecialPowerOfSelectedCardOn(pos.get(0), pos.get(1));
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
                getController().getCardHelp();
                break;
            case EXIT:
                getController().deselectCard();
                showMessage("\nyou've entered game menu\n");
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

    private List<Integer> getPosition(String command) {
        List<Integer> positions = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+").matcher(command);
        while (matcher.find()) {
            positions.add(Integer.valueOf(matcher.group()) - 1);
        }
        return positions;
    }

    private String[] extractMyCardIdsForCombo(String command) {
        String str = command.replaceFirst("attack combo \\S+ ", "");
        return str.split(" ");
    }

    private String extractOpponentIdForCombo(String command) {
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
                "4- insert (x,y)\n" +
                "5- use special power (x,y)\n" +
                "6- show menu\n" +
                "7- help\n" +
                "8- exit\n";
    }

}
