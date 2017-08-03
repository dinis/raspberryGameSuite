package pt.dinis.common.messages.invite;

import pt.dinis.common.core.Game;
import pt.dinis.common.core.Player;

/**
 * Created by tiago on 16-02-2017.
 */
public class DeliverInvite extends InviteMessage {

    Game game;
    Player player;

    public DeliverInvite(Game game, Player player) {
        if (game == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.game = game;
        if (player == null) {
            throw new IllegalArgumentException("Players cannot be null");
        }
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "game=" + game +
                ", player=" + player +
                "} " + super.toString();
    }
}
