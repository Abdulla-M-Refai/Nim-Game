package com.nim.game.model;

public enum GameType
{
    STANDARD("view/standard-game.fxml"),
    CIRCULAR("view/circular-game.fxml");

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
