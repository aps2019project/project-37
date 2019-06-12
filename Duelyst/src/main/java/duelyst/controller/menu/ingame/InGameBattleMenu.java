package duelyst.controller.menu.ingame;

import duelyst.controller.Controller;
import duelyst.controller.GameException;
import duelyst.controller.menu.Menu;

public class InGameBattleMenu extends Menu {

    enum InGameType {
        GAME_INFO(0),
        SHOW_ALLY_MINIONS(1),
        SHOW_ENEMY_MINIONS(2),
        SHOW_CARD_INFO(3),
        SELECT_CARD(4),
        SHOW_HAND(5),
        END_TURN(6),
        SHOW_COLLECTIBLES(7),
        SELECT_COLLECTIBLE(8),
        SHOW_NEXT_CARD(9),
        GRAVE_YARD(10),
        HELP(11),
        END_GAME(12),
        SHOW_MENU(13),
        EXIT(14);

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
            "^show card info \\S+$",
            "^select \\S+$",
            "^show hand$",
            "^end turn$",
            "^show collectibles$",
            "^select collectable \\S+$",
            "^show next card$",
            "^enter graveyard$",
            "^help$",
            "^end game$",
            "^show menu$",
            "^exit$"
    };
    private InGameType type;
    private GraveyardMenu graveyardMenu;
    private CollectibleMenu collectibleMenu;
    private CardMenu cardMenu;

    public void setGameOn(boolean gameOn) {
        this.gameOn = gameOn;
    }

    private boolean gameOn = true;

    public InGameBattleMenu(Controller controller) {
        super(controller);
        graveyardMenu = new GraveyardMenu(controller);
        collectibleMenu = new CollectibleMenu(controller);
        cardMenu = new CardMenu(controller);
        graveyardMenu.setParentMenu(this);
        collectibleMenu.setParentMenu(this);
        cardMenu.setParentMenu(this);
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {
        match(command);
        if (gameOn) {
            switch (type) {
                case GAME_INFO:
                    getController().showGameInfo();
                    break;
                case SHOW_ALLY_MINIONS:
                    getController().showAllyMinions();
                    break;
                case SHOW_ENEMY_MINIONS:
                    getController().showEnemyMinions();
                    break;
                case SHOW_CARD_INFO:
                    getController().showCardInfo(extractLastWord(command));
                    break;
                case SELECT_CARD:
                    getController().selectCard(extractLastWord(command));
                    showMessage("\nyou've entered card menu\n");
                    return cardMenu;
                case SHOW_HAND:
                    getController().showHand();
                    break;
                case END_TURN:
                    if (getController().endTurn()) {
                        gameOn = false;
                    }
                    break;
                case SHOW_COLLECTIBLES:
                    getController().showCollectibles();
                    break;
                case SELECT_COLLECTIBLE:
                    getController().selectCollectible(extractLastWord(command));
                    showMessage("\nyou've entered collectable menu\n");
                    return collectibleMenu;
                case SHOW_NEXT_CARD:
                    getController().showNextCard();
                    break;
                case GRAVE_YARD:
                    showMessage("\nyou've entered graveyard menu\n");
                    return graveyardMenu;
                case HELP:
                    getController().inGameHelp();
                    break;
                case END_GAME:
                    throw new GameException("invalid command");
                case SHOW_MENU:
                    showMessage(menuHelp());
                    break;
                case EXIT:
                    showMessage("\nyou've entered MainMenu\n");
                    return getParentMenu().getParentMenu();
            }
        } else {
            if (type == InGameType.END_GAME) {
                getController().deleteGame();
                showMessage("\nyou've entered MainMenu\n");
                return getParentMenu().getParentMenu();
            } else {
                throw new GameException("invalid command");
            }
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
                "7- end turn\n" +
                "8- show collectibles\n" +
                "9- select collectable [collectible id]\n" +
                "10- show next card\n" +
                "11- enter graveyard\n" +
                "12- help\n" +
                "13- end game\n" +
                "14- show menu\n" +
                "15- exit\n";
    }

    private String extractLastWord(String command) {
        String[] strings = command.split(" ");
        return strings[strings.length - 1];
    }

}
