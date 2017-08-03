package pt.dinis.common.messages.invite;

import pt.dinis.common.core.GameType;
import pt.dinis.common.core.Player;

import java.util.List;

/**
 * Created by tiago on 16-02-2017.
 */
public class Invite extends InviteMessage {

    GameType game;
    List<Player> players;

    public Invite(GameType game, List<Player> players) {
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

    public void setGame(GameType game) {
        this.game = game;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public Direction getDirection() {
        return Direction.CLIENT_TO_SERVER;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "game=" + game +
                ", players=" + players +
                "} " + super.toString();
    }
}
