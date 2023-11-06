package com.nim.game.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.application.Platform;

import javafx.stage.Stage;

import javafx.scene.Node;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;

import javafx.scene.control.Label;
import javafx.scene.control.Alert;

import javafx.scene.shape.Rectangle;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;

import javafx.geometry.Pos;
import javafx.util.Duration;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import java.io.IOException;

import static com.nim.game.util.Helper.*;
import static com.nim.game.util.GameSettings.*;

import com.nim.game.listener.NimClickListener;

public class GameController implements Initializable
{
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private StackPane gamePane;

    @FXML
    private Label playerNameLabel;

    @FXML
    private Rectangle playerTimeContainer;

    private GridPane gameGrid;

    private int []piles;

    private int selectedPile;

    private boolean playerTurn;

    private double timerContainerWidth;

    private Timeline timeline;

    private String playerName;

    private int depth;

    private int time;

    private int countDown;

    private int nim;

    @FXML
    void onAnchorClick(MouseEvent event)
    {
        if (event.getButton() == MouseButton.SECONDARY && playerTurn)
            countDown = 0;
    }

    private void startGame()
    {
        countDown = time;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev ->
        {
            if(playerTurn)
            {
                if(countDown > 0)
                {
                    countDown--;
                    playerTimeContainer.setWidth((countDown * timerContainerWidth) / time);
                }
                else
                {
                    System.out.println("sss");
                    ensurePlayerMovement();
                    flipPlayers();
                }
            }
            else
            {
                computerMove();
                flipPlayers();
            }

            if(isGameOver())
            {
                stopGame();
                showGameResult();
                leaveGame();
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void ensurePlayerMovement()
    {
        if(playerTurn && countDown == 0 && selectedPile == -1)
        {
            Random random = new Random();
            int row = random.nextInt(piles.length);

            while(piles[row] == 0)
                row = random.nextInt(piles.length);

            int column = 0;

            for(Node node : gameGrid.getChildren())
                if(node instanceof AnchorPane && GridPane.getRowIndex(node) == row)
                    column = GridPane.getColumnIndex(node);

            chooseNim(row, column);
        }
    }

    private void computerMove()
    {
        int[] result = alphaBeta(piles, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        int pileIndex = result[1];
        int nimCountToRemove = result[2];

         int index = 0;
         int count = 0;

         for(int i = 0 ; i < piles.length ; i++)
         {
             if(piles[i] > 0)
             {
                count++;
                index = i;
             }
         }

        if(count == 1 && nimCountToRemove == piles[index] && nimCountToRemove != 1)
            nimCountToRemove--;

        for(int j = 0 ; j < nimCountToRemove ; j++)
        {
            int column = 0;
            for(Node node : gameGrid.getChildren())
                if(node instanceof AnchorPane && GridPane.getRowIndex(node) == pileIndex)
                    column = GridPane.getColumnIndex(node);

            chooseNim(pileIndex, column);
        }
    }

    public static int[] alphaBeta(int[] piles, int depth, int alpha, int beta, boolean isMax)
    {
        if (depth == 0)
        {
            int value = evaluate(piles);
            return new int[]{value, -1};
        }

        if (isMax)
        {
            int maxEval = Integer.MIN_VALUE;
            int pileIndex = -1;
            int bestMove = -1;

            for (int i = 0; i < piles.length; i++)
            {
                for (int j = 1; j <= piles[i]; j++)
                {
                    if (piles[i] - j >= 0)
                    {
                        piles[i] -= j;
                        int[] evalResult = alphaBeta(piles, depth - 1, alpha, beta, false);
                        int eval = evalResult[0];

                        if (eval > maxEval)
                        {
                            maxEval = eval;
                            pileIndex = i;
                            bestMove = j;
                        }

                        alpha = Math.max(alpha, eval);
                        piles[i] += j;

                        if (beta <= alpha)
                            break;
                    }
                }
            }

            return new int[]{maxEval, pileIndex, bestMove};
        }
        else
        {
            int minEval = Integer.MAX_VALUE;
            int pileIndex = -1;
            int bestMove = -1;

            for (int i = 0; i < piles.length; i++)
            {
                for (int j = 1; j <= piles[i]; j++)
                {
                    if (piles[i] - j >= 0)
                    {
                        piles[i] -= j;
                        int[] evalResult = alphaBeta(piles, depth - 1, alpha, beta, true);
                        int eval = evalResult[0];

                        if (eval < minEval)
                        {
                            minEval = eval;
                            pileIndex = i;
                            bestMove = j;
                        }

                        beta = Math.min(beta, eval);
                        piles[i] += j;

                        if (beta <= alpha)
                            break;
                    }
                }
            }

            return new int[]{minEval, pileIndex, bestMove};
        }
    }

    public static int evaluate(int[] piles)
    {
        int value = 0;
        for (int pile : piles)
            value ^= pile;

        return value;
    }

    private void flipPlayers()
    {
        selectedPile = -1;
        countDown = time;
        playerTurn = !playerTurn;
        gamePane.setDisable(!playerTurn);
        playerTimeContainer.setWidth(timerContainerWidth);
    }

    private boolean isGameOver()
    {
        for(int pile : piles)
            if (pile > 0)
                return false;

        return true;
    }

    private void showGameResult()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(playerTurn ? playerName + " Wins The Game!" : "Computer Wins The Game!");
        alert.show();
    }

    private void chooseNim(int row, int column)
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

    public void stopGame()
    {
        if (timeline != null)
        {
            timeline.stop();
            timeline = null;
        }
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
        int nimLevels = (int) Math.ceil(Math.sqrt(nim));
        int maximumColumn = (nimLevels * 2) - 1;

        piles = new int[nimLevels];

        for(int i = 0 ; i < nimLevels ; i++)
        {
            for(int j = 0 ; j < maximumColumn ; j++)
            {
                gameGrid.add(new Pane(), j, i);
            }
        }

        NimClickListener nimClickListener = this::chooseNim;

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
        playerName = getPlayerName();
        depth = getLevel().getDepth();

        time = getTime();
        nim = getNim();

        countDown = 0;
        selectedPile = -1;

        playerNameLabel.setText(playerName);
        playerTurn = new Random().nextBoolean();

        GridPane gameGrid = createGraphicalGameGrid();
        initGameGrid(gameGrid);
        startGame();

        Platform.runLater(() -> {
            gamePane.setDisable(!playerTurn);
            timerContainerWidth = playerTimeContainer.getWidth();

            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> stopGame());
        });
    }
}