package com.nim.game.model;

public enum GameType
{
    STANDARD("view/standard-game.fxml"),
    KAYLES("view/kayles-game.fxml");

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
