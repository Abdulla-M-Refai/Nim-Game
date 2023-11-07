package com.nim.game.model;

public class Move
{
    private final int row;

    private final int nimCountToRemove;

    public Move(int row, int nimCountToRemove)
    {
        this.row = row;
        this.nimCountToRemove = nimCountToRemove;
    }

    public int getRow()
    {
        return row;
    }

    public int getNimCountToRemove()
    {
        return nimCountToRemove;
    }
}
