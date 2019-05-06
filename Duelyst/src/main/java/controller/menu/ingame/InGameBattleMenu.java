package controller.menu.ingame;

import controller.Controller;
import controller.GameException;
import controller.menu.Menu;
import model.cards.InGame;

public class InGameBattleMenu extends Menu {

    enum InGameType {
        GAME_INFO(0),
        SHOW_ALLY_MINIONS(1),
        SHOW_ENEMY_MINIONS(2),
        SHOW_CARD_INFO(3),
        SELECT_CARD(4),
        SHOW_HAND(5),
        INSERT(6),
        END_TURN(7),
        SHOW_COLLECTIBLES(8),
        SELECT_COLLECTIBLE(9),
        SHOW_NEXT_CARD(10),
        GRAVE_YARD(11),
        HELP(12),
        END_GAME(13),
        SHOW_MENU(14),
        EXIT(15);

        int commandIndex;

        InGameType(int commandIndex) {
            this.commandIndex = commandIndex;
        }

        public static InGameType getCommandType(int commandIndex) {
            for (InGameType commandType : values()) {
                if (commandType.commandIndex == commandIndex) {
                    return commandType;
                }
            }
            throw new GameException("invalid command");
        }
    }

    private String[] matchers = {
            "^game info$",
            "^show my minions$",
            "^show opponent minions$",
            "^show card info (\\w+_){2}\\d+$",
            "^select (\\w+_){2}\\d+$",
            "^show hand$",
            "^insert \\S+ in (\\d+,\\d+)$",
            "^end turn$",
            "^show collectibles$",
            "^select (\\w+_){2}\\d+$",
            "^show next card$",
            "^enter graveyard$",
            "^help$",
            "^end game$",
            "^show menu$",
            "^exit$"
    };
    private InGameType type;

    public InGameBattleMenu(Controller controller) {
        super(controller);
        GraveyardMenu graveyardMenu = new GraveyardMenu(controller);
        CollectibleMenu collectibleMenu = new CollectibleMenu(controller);
        CardMenu cardMenu = new CardMenu(controller);
        graveyardMenu.setParentMenu(this);
        collectibleMenu.setParentMenu(this);
        cardMenu.setParentMenu(this);
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        match(command);
        switch (type) {
            case GAME_INFO:
                break;
            case SHOW_ALLY_MINIONS:
                break;
            case SHOW_ENEMY_MINIONS:
                break;
            case SHOW_CARD_INFO:
                break;
            case SELECT_CARD:
                break;
            case SHOW_HAND:
                break;
            case INSERT:
                break;
            case END_TURN:
                break;
            case SHOW_COLLECTIBLES:
                break;
            case SELECT_COLLECTIBLE:
                break;
            case SHOW_NEXT_CARD:
                break;
            case GRAVE_YARD:
                break;
            case HELP:
                break;
            case END_GAME:
                break;
            case SHOW_MENU:
                showMessage(menuHelp());
                break;
            case EXIT:
                showMessage("\nyou've entered MainMenu\n");
                return getParentMenu().getParentMenu();
        }
        return this;
    }

    private void match(String command) {
        for (int i = 0; i < matchers.length; i++) {
            String matcher = matchers[i];
            if (command.matches(matcher)) {
                type = InGameType.getCommandType(i);
                return;
            }
        }
        throw new GameException("invalid command");
    }

    private String menuHelp() {
        return "\nIn Game Menu\n" +
                "-----------\n" +
                "Commands:\n" +
                "1- game info\n" +
                "2- show my minions\n" +
                "3- show opponent minions\n" +
                "4- show card info [card id]\n" +
                "5- select [card id]\n" +
                "6- show hand\n" +
                "7- insert [card name] in (x,y)\n" +
                "8- end turn\n" +
                "9- show collectibles\n" +
                "10- select [collectible id]\n" +
                "11- show next card\n" +
                "12- enter graveyard\n" +
                "13- help\n" +
                "14- end game\n" +
                "15- show menu\n" +
                "16- exit\n";
    }

}
