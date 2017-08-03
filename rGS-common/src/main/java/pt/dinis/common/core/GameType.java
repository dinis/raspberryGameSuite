package pt.dinis.common.core;

/**
 * Created by tiago on 03-08-2017.
 */
public enum GameType {
    TIC_TAC_TOE("Tic Tac Toe");

    private String value;

    GameType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

    public static GameType getEnum(String value) {
        for (GameType v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
