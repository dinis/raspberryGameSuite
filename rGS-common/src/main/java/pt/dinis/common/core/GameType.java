package pt.dinis.common.core;

/**
 * Created by tiago on 03-08-2017.
 */
public enum GameType {
    TIC_TAC_TOE(1, "Tic Tac Toe", 2);

    private int id;
    private String value;
    private int numberOfPlayers;

    GameType(int id, String value, int numberOfPlayers) {
        this.id = id;
        this.value = value;
        this.numberOfPlayers = numberOfPlayers;
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public String prettyPrint() {
        return this.getId().toString() + ": " + this.getValue();
    }

    @Override
    public String toString() {
        return getValue();
    }

    public static GameType getEnum(String value) {
        for (GameType v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
