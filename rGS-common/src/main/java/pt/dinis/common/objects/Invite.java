package pt.dinis.common.objects;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by tiago on 19-08-2017.
 */
public class Invite {
    Game game;
    InviteStatus status;
    Collection<Player> opponents;

    public Invite(Game game, InviteStatus status, Collection<Player> opponents) {
        this.game = game;
        this.status = status;
        this.opponents = opponents;
    }

    public Game getGame() {
        return game;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public Collection<Player> getOpponents() {
        return opponents;
    }

    @Override
    public String toString() {
        return "Invite{" +
                "game=" + game +
                ", status=" + status +
                ", opponents=" + opponents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invite)) return false;
        Invite invite = (Invite) o;
        return Objects.equals(getGame(), invite.getGame()) &&
                getStatus() == invite.getStatus() &&
                Objects.equals(getOpponents(), invite.getOpponents());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGame(), getStatus(), getOpponents());
    }
}
