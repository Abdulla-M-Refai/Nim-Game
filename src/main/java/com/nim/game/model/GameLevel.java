package com.nim.game.model;

public enum GameLevel
{
    EASY(1),
    MEDIUM(3),
    HARD(6);

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
