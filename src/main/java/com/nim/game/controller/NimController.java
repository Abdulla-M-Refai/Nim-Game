package com.nim.game.controller;

import javafx.fxml.FXML;

import com.nim.game.listener.NimClickListener;

public class NimController
{
    private int row;

    private int column;

    private NimClickListener nimClickListener;

    @FXML
    void onClick()
    {
        nimClickListener.onClickListener(row, column);
    }

    public void setData(int row, int column, NimClickListener nimClickListener)
    {
        this.row = row;
        this.column = column;
        this.nimClickListener = nimClickListener;
    }
}