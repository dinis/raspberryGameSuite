package pt.dinis.common.core;

/**
 * Created by tiago on 03-08-2017.
 */
public enum GameType {
    TIC_TAC_TOE(1, "Tic Tac Toe");

    private Integer id;
    private String value;

    GameType(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getId().toString() + ": " + this.getValue();
    }

    public static GameType getEnum(String value) {
        for (GameType v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
