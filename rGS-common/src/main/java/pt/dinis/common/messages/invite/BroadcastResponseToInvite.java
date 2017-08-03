package pt.dinis.common.messages.invite;

import pt.dinis.common.core.Game;
import pt.dinis.common.core.Player;

/**
 * Created by tiago on 16-02-2017.
 */
public class BroadcastResponseToInvite extends InviteMessage {

    Boolean accept;
    Player player;
    Game game;

    public BroadcastResponseToInvite(Game game, Boolean accept, Player player) {
        if (accept == null) {
            throw new IllegalArgumentException("Accept cannot be null");
        }
        this.accept = accept;
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        this.game = game;
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.player = player;
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "BroadcastResponseToInvite{" +
                "accept=" + accept +
                ", player=" + player +
                ", game=" + game +
                "} " + super.toString();
    }
}
