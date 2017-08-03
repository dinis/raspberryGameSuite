package pt.dinis.common.messages.invite;

import pt.dinis.common.core.Game;

/**
 * Created by tiago on 16-02-2017.
 */
public class RespondToInvite extends InviteMessage {

    Boolean accept;
    Game game;

    public RespondToInvite(Game game, Boolean accept) {
        if (accept == null) {
            throw new IllegalArgumentException("Accept cannot be null");
        }
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Boolean getAccept() {
        return accept;
    }

    public void setAccept(Boolean accept) {
        this.accept = accept;
    }

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "RespondToInvite{" +
                "accept=" + accept +
                ", game=" + game +
                "} " + super.toString();
    }
}
