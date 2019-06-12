package duelyst.model;

import java.util.Date;

public class MatchHistory {

    private String opponentName;
    private boolean winStatus;
    private Date date;

    public MatchHistory(String opponentName, boolean winStatus, Date date) {
        this.opponentName = opponentName;
        this.winStatus = winStatus;
        this.date = date;
    }
}
