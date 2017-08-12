package pt.dinis.common.messages.invite;

import pt.dinis.common.core.Game;
import pt.dinis.common.core.Player;

/**
 * Created by tiago on 16-02-2017.
 */
public class BroadcastInvite extends InviteMessage {

    Game game;

    public BroadcastInvite(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public Direction getDirection() {
        return Direction.SERVER_TO_CLIENT;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "game=" + game +
                "} " + super.toString();
    }
}
