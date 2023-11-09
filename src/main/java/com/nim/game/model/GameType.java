package com.nim.game.model;

public enum GameType
{
    STANDARD("view/standard-game.fxml"),
    THE_N_GAME("view/n-game.fxml");

    private final String type;

    GameType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }
}
