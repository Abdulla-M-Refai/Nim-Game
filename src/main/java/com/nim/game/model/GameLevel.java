package com.nim.game.model;

public enum GameLevel
{
    EASY(2),
    MEDIUM(5),
    HARD(7);

    private final int depth;

    GameLevel(int depth)
    {
        this.depth = depth;
    }

    public int getDepth()
    {
        return depth;
    }
}
