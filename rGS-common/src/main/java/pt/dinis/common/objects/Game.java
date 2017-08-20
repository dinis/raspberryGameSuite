package pt.dinis.common.objects;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Objects;

/**
 * Created by tiago on 02-08-2017.
 */
public class Game implements MessageObject {

    Integer id;
    GameType game;
    Player host;
    DateTime date;
    boolean publicGame;

    public Game(Integer id, GameType game, Player host, boolean publicGame, DateTime date) {
        this.id = id;
        this.game = game;
        this.host = host;
        this.publicGame = publicGame;
        this.date = date;
    }

    public Player getHost() {
        return host;
    }

    public DateTime getDate() {
        return date;
    }

    public Integer getId() {
        return id;
    }

    public GameType getGame() {
        return game;
    }

    public boolean isPublicGame() {
        return publicGame;
    }

    @Override
    public String prettyPrint() {
        DateTimeFormatter formatter = DateTimeFormat.shortDateTime();
        String result = publicGame ? "Public game" : "Private game";
        return result + "(" + id +
                ": " + game.toString() +
                " of " + host.prettyPrint() +
                " at " + formatter.print(date) + ")";
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", game=" + game +
                ", host=" + host +
                ", date=" + date +
                ", publicGame=" + publicGame +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game1 = (Game) o;
        return Objects.equals(getId(), game1.getId()) &&
                getGame() == game1.getGame() &&
                Objects.equals(getHost(), game1.getHost()) &&
                Objects.equals(getDate(), game1.getDate()) &&
                Objects.equals(isPublicGame(), game1.isPublicGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getGame(), getHost(), getDate(), isPublicGame());
    }
}
