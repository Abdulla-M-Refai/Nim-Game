package com.nim.game.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.application.Platform;

import javafx.scene.Node;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.stage.Stage;

import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;

import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

import javafx.geometry.Pos;

import java.util.*;
import java.net.URL;

import java.io.IOException;

import static com.nim.game.util.Helper.*;
import static com.nim.game.util.GameSettings.*;

import com.nim.game.listener.NimClickListener;

public class GameController implements Initializable
{
    int x;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Rectangle computerTimeContainer;

    @FXML
    private StackPane gamePane;

    @FXML
    private Label playerName;

    @FXML
    private Rectangle playerTimeContainer;

    private GridPane gameGrid;

    private int []piles;

    private int selectedPile;

    private int countDown;

    private boolean playerTurn;

    private Timer timer;

    @FXML
    void onAnchorClick(MouseEvent event)
    {
        if (event.getButton() == MouseButton.SECONDARY)
        {
            countDown = 0;
        }
    }

    private void startGame()
    {
        countDown = getTime();
        final double width = playerTimeContainer.getWidth();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if(countDown-- >= 0)
                {
                    if(playerTurn)
                    {
                        gamePane.setDisable(false);
                        playerTimeContainer.setWidth((countDown * width)/getTime());
                    }
                    else
                    {
                        gamePane.setDisable(true);
                        computerTimeContainer.setWidth((countDown * width)/getTime());
                    }

                    return;
                }

                selectedPile = -1;
                countDown = getTime();
                playerTurn = !playerTurn;

                playerTimeContainer.setWidth(width);
                computerTimeContainer.setWidth(width);

                if(isGameOver())
                {
                    leaveGame();
                }
            }
        }, Calendar.getInstance().getTime(), 1000);
    }

    private boolean isGameOver()
    {
        for(Integer pile : piles)
            if(pile != 0)
                return true;

        return false;
    }

    public void stopGame()
    {
        if (timer != null)
        {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void nimClickEvent(int row, int column)
    {
        if(selectedPile == -1)
            selectedPile = row;

        for(Node node : gameGrid.getChildren())
        {
            if(node instanceof AnchorPane && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column && selectedPile == row)
            {
                piles[row]--;
                gameGrid.getChildren().remove(node);
                break;
            }
        }
    }

    @FXML
    void leaveGame()
    {
        stopGame();
        loadView("view/main-menu.fxml", (Stage)anchorPane.getScene().getWindow(), anchorPane);
    }

    private GridPane createGraphicalGameGrid()
    {
        gameGrid = new GridPane();

        gameGrid.setMinWidth(750);
        gameGrid.setMinHeight(500);

        gameGrid.setVgap(5);
        gameGrid.setHgap(5);

        gameGrid.setAlignment(Pos.CENTER);

        return  gameGrid;
    }

    private void initGameGrid(GridPane gameGrid)
    {
        int nimLevels = (int) Math.ceil(Math.sqrt(getNim()));
        int maximumColumn = (nimLevels * 2) - 1;

        piles = new int[nimLevels];

        for(int i = 0 ; i < nimLevels ; i++)
        {
            for(int j = 0 ; j < maximumColumn ; j++)
            {
                gameGrid.add(new Pane(), j, i);
            }
        }

        NimClickListener nimClickListener = this::nimClickEvent;

        for(int i = 0 ; i < nimLevels ; i++)
        {
            int start = (maximumColumn / 2) - i;
            int end = ((i + 1) * 2) + (start - 1);

            for(int j = start ; j < end ; j++)
            {
                try
                {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getResource("view/nim.fxml"));
                    AnchorPane nim = fxmlLoader.load();

                    NimController nimController = fxmlLoader.getController();
                    nimController.setData(i, j, nimClickListener);

                    gameGrid.add(nim, j, i);
                }
                catch (IOException e)
                {
                    System.out.println(e.getMessage());
                }
            }

            piles[i] = ((i + 1) * 2) - 1;
        }

        gamePane.getChildren().add(gameGrid);
        StackPane.setAlignment(gameGrid,Pos.CENTER_LEFT);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        countDown = 0;
        selectedPile = -1;

        playerName.setText(getPlayerName());
        playerTurn = new Random().nextBoolean();

        GridPane gameGrid = createGraphicalGameGrid();
        initGameGrid(gameGrid);
        startGame();

        Platform.runLater(() -> {
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> stopGame());
        });
    }
}