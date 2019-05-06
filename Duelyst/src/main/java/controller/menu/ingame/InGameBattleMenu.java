package controller.menu.ingame;

import controller.Controller;
import controller.GameException;
import controller.menu.Menu;
import model.cards.Card;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            "^insert \\S+ in \\(\\d+,\\d+\\)$",
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
    private GraveyardMenu graveyardMenu;
    private CollectibleMenu collectibleMenu;
    private CardMenu cardMenu;

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
                return cardMenu;
            case SHOW_HAND:
                getController().showHand();
                break;
            case INSERT:
                String name = extractCardNameForInsert(command);
                int x = extractXForInsert(command);
                int y = extractYForInsert(command);
                getController().insertCardInXY(name, x, y);
                break;
            case END_TURN:
                getController().endTurn();
                break;
            case SHOW_COLLECTIBLES:
                getController().showCollectibles();
                break;
            case SELECT_COLLECTIBLE:
                getController().selectCollectible(extractLastWord(command));
                return collectibleMenu;
            case SHOW_NEXT_CARD:
                getController().showNextCard();
                break;
            case GRAVE_YARD:
                graveyardMenu.setGraveYard(getController().getGraveYard());
                return graveyardMenu;
            case HELP:
                showMessage(getHelpOfPossibleCommands());
                break;
            case END_GAME:
                return getParentMenu();
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

    private String extractLastWord(String command) {
        String[] strings = command.split(" ");
        return strings[strings.length - 1];
    }

    private String extractCardNameForInsert(String command) {
        String[] strings = command.split(" ");
        return strings[1];
    }

    private int extractXForInsert(String command){
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

    private int extractYForInsert(String command){
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

    private String getHelpOfPossibleCommands(){
        int count = 1;
        StringBuilder help = new StringBuilder();
        count = 1;
        help.append("Insertable Cards:\n");
        for(Card card:getController().getInsertableCards()){
            help.append(count + card.getId()+"\n");
        }
        return help.toString();
    }
}
