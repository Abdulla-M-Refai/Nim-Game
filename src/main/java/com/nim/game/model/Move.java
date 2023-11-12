package com.nim.game.model;

public class Move
{
    private final int index;

    private final int nimCountToRemove;

    public Move(int index, int nimCountToRemove)
    {
        this.index = index;
        this.nimCountToRemove = nimCountToRemove;
    }

    public int getIndex()
    {
        return index;
    }

    public int getNimCountToRemove()
    {
        return nimCountToRemove;
    }
}
