package pt.dinis.common.messages.invite;

import pt.dinis.common.objects.GameType;

import java.util.Collection;

/**
 * Created by tiago on 16-02-2017.
 */
public class InviteToGame extends InviteMessage {

    GameType game;
    Collection<Integer> players;

    public InviteToGame(GameType game, Collection<Integer> players) {
        if (game == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        this.game = game;
        if (players == null) {
            throw new IllegalArgumentException("Players cannot be null");
        }
        this.players = players;
    }

    public GameType getGame() {
        return game;
    }

    public Collection<Integer> getPlayers() {
        return players;
    }

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "InviteToGame{" +
                "game=" + game +
                ", players=" + players +
                "} " + super.toString();
    }
}
