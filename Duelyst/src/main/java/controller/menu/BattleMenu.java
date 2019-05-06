package controller.menu;

import controller.Controller;
import controller.GameException;
import controller.menu.ingame.InGameBattleMenu;

public class BattleMenu extends Menu {

    enum PlayMode {
        SINGLE_PLAYER("1"),
        MULTI_PLAYER("2"),
        UNKNOWN("-1");

        private String value;

        PlayMode(String value) {
            this.value = value;
        }

        public static PlayMode getMode(String command) {
            for (PlayMode value : values()) {
                if (value.value.equals(command) && !command.equals("-1")) {
                    return value;
                }
            }
            return null;
        }
    }

    enum SingleMode {
        STORY("1"),
        CUSTOM_GAME("2"),
        UNKNOWN("-1");

        private String value;

        SingleMode(String value) {
            this.value = value;
        }

        public static SingleMode getMode(String command) {
            for (SingleMode value : values()) {
                if (value.value.equals(command) && !command.equals("-1")) {
                    return value;
                }
            }
            return null;
        }
    }

    public enum CustomGameMode {
        KILL_ENEMY_HERO("1"),
        KEEP_FLAG_6_ROUNDS("2"),
        COLLECT_HALF_FLAGS("3"),
        UNKNOWN("-1");

        private String value;

        CustomGameMode(String value) {
            this.value = value;
        }

        public static CustomGameMode getMode(String command) {
            for (CustomGameMode value : values()) {
                if (value.value.equals(command) && !command.equals("-1")) {
                    return value;
                }
            }
            return null;
        }
    }

    public enum StoryLevel {
        ONE("1"),
        TWO("2"),
        THREE("3"),
        UNKNOWN("-1");

        public String value;

        StoryLevel(String value) {
            this.value = value;
        }

        public static StoryLevel getMode(String command) {
            for (StoryLevel value : values()) {
                if (value.value.equals(command) && !command.equals("-1")) {
                    return value;
                }
            }
            return null;
        }
    }

    private PlayMode playMode;
    private SingleMode singleMode;
    private CustomGameMode customGameMode;
    private StoryLevel storyLevel;
    private InGameBattleMenu inGameBattleMenu;

    BattleMenu(Controller controller) {
        super(controller);
        inGameBattleMenu = new InGameBattleMenu(controller);
        inGameBattleMenu.setParentMenu(this);
        init();
    }

    public void init(){
        playMode = PlayMode.UNKNOWN;
        singleMode = SingleMode.UNKNOWN;
        customGameMode = CustomGameMode.UNKNOWN;
        storyLevel = StoryLevel.UNKNOWN;
    }

    @Override
    public Menu runCommandAndGetNextMenu(String command) {

        switch (playMode) {
            case SINGLE_PLAYER:
                switch (singleMode) {
                    case STORY:
                        if (storyLevel == StoryLevel.UNKNOWN) {
                            handleStoryMode(command);
                            return inGameBattleMenu;
                        }
                        break;
                    case CUSTOM_GAME:
                        if (customGameMode == CustomGameMode.UNKNOWN) {
                            try {
                                handleGameMode(command);
                                return inGameBattleMenu;
                            } catch (Exception e) {
                                throw new GameException("invalid command");
                            }
                        }
                        break;
                    case UNKNOWN:
                        handleSingleMode(command);
                        break;
                }
                break;
            case MULTI_PLAYER:
                break;
            case UNKNOWN:
                handlePlayMode(command);
                break;
        }

        return this;
    }

    private void handlePlayMode(String command) {
        if (PlayMode.getMode(command) == null) {
            throw new GameException("Invalid command");
        } else {
            this.playMode = PlayMode.getMode(command);
            showHelp();
        }
    }

    private void handleSingleMode(String command) {
        if (SingleMode.getMode(command) == null) {
            throw new GameException("Invalid command");
        } else {
            this.singleMode = SingleMode.getMode(command);
            showHelp();
        }
    }

    private void handleStoryMode(String command) {
        if (StoryLevel.getMode(command) == null) {
            throw new GameException("Invalid command");
        } else {
            this.storyLevel = StoryLevel.getMode(command);
            int flagNumber = 0;
            assert storyLevel != null;
            switch (storyLevel) {
                case TWO:
                    flagNumber = 1;
                    break;
                case THREE:
                    flagNumber = 5;
                    break;
            }
            try {
                getController().createGame(storyLevel, flagNumber);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleGameMode(String command) throws CloneNotSupportedException {
        String[] strings = command.replace("start game ", "").split(" ");
        int deckNumber = Integer.valueOf(strings[0].replace("AI_deck", ""));
        if (deckNumber <= 3 && deckNumber >= 1) {
            if (CustomGameMode.getMode(strings[1]) == null) {
                throw new GameException("Invalid command");
            } else {
                this.customGameMode = CustomGameMode.getMode(strings[1]);
                int flagNumber = 0;
                assert customGameMode != null;
                switch (customGameMode) {
                    case KEEP_FLAG_6_ROUNDS:
                        flagNumber = 1;
                        break;
                    case COLLECT_HALF_FLAGS:
                        flagNumber = Integer.valueOf(strings[2]);
                        break;
                }
                getController().createGame(deckNumber, customGameMode, flagNumber);

            }
        } else {
            throw new GameException("invalid command");
        }
    }

    public void showHelp() {
        switch (playMode) {
            case SINGLE_PLAYER:
                switch (singleMode) {
                    case STORY:
                        showMessage(storyString());
                        break;
                    case CUSTOM_GAME:
                        showMessage("start game [ AI_deck1 | AI_deck2 | AI_deck3 ] [mode] [flag numbers]");
                        break;
                    case UNKNOWN:
                        showMessage("choose one\n1. story\n2. custom game");
                        break;
                }
                break;
            case MULTI_PLAYER:
                break;
            case UNKNOWN:
                showMessage("choose one\n1. single player\n2. multi player");
                break;
        }
    }

    private String storyString() {
        return "choose one\n1.  Hero: white-beast   mode: kill-enemy-hero  reward: 500\n" +
                "2.  Hero: zahhak   mode: keep-flags-6-rounds  reward: 1000\n" +
                "3.  Hero: arash   mode: collect-half-flags  reward: 1500\n";
    }
}
