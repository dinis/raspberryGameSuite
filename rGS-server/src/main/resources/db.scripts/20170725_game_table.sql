CREATE TABLE games (
    id SERIAL PRIMARY KEY,
    game_type VARCHAR NOT NULL,
    user_id INT NOT NULL REFERENCES users;
    status VARCHAR NOT NULL,
    creation_date timestamp without time zone NOT NULL DEFAULT now()
);

CREATE TABLE games_players (
    id SERIAL PRIMARY KEY,
    game_id INT NOT NULL REFERENCES games,
    user_id INT NOT NULL REFERENCES users,
    status VARCHAR NOT NULL,
    user_order INT,
    invite_date timestamp without time zone NOT NULL DEFAULT now()
);

CREATE TABLE game_tictactoe_moves (
    id SERIAL PRIMARY KEY,
    game_id INT NOT NULL REFERENCES games,
    user_id INT NOT NULL REFERENCES users,
    position_x INT NOT NULL,
    position_y INT NOT NULL,
    move_date timestamp without time zone NOT NULL DEFAULT now()
);
