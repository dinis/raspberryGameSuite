package pt.dinis.common.core;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by tiago on 02-08-2017.
 */
public class Game implements Serializable {

    Integer id;
    GameType game;

    public Game(Integer id, GameType game) {
        this.id = id;
        this.game = game;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GameType getGame() {
        return game;
    }

    public void setName(GameType game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", game='" + game + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game1 = (Game) o;
        return Objects.equals(getId(), game1.getId()) &&
                getGame() == game1.getGame();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getGame());
    }
}
