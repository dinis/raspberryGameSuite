package pt.dinis.common.messages.invite;

/**
 * Created by tiago on 16-02-2017.
 */
public class RespondToInvite extends InviteMessage {

    Boolean accept;
    Integer game;

    public RespondToInvite(Integer game, Boolean accept) {
        if (accept == null) {
            throw new IllegalArgumentException("Accept cannot be null");
        }
        this.accept = accept;
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        this.game = game;
    }

    public Integer getGame() {
        return game;
    }

    public Boolean getAccept() {
        return accept;
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
