package pt.dinis.common.messages.invite;

import pt.dinis.common.objects.Game;

/**
 * Created by tiago on 16-02-2017.
 */
public class DeliverInvite extends InviteMessage {

    Game game;

    public DeliverInvite(Game game) {
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
        return "DeliverInvite{" +
                "game=" + game +
                "} " + super.toString();
    }
}
