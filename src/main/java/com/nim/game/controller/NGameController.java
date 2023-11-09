package com.nim.game.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.application.Platform;

import javafx.stage.Stage;

import javafx.scene.Node;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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

public class NGameController implements Initializable
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

    private boolean playerTurn;

    private double timerContainerWidth;

    private Timeline timeline;

    private String playerName;

    private int depth;

    private int time;

    private int countDown;

    private int nim;

    private int selectedCount;

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
                playerTurn();
            else
                computerMove();

            if(isGameOver())
                gameOver();
            else if(!playerTurn || countDown == 0)
                flipPlayers();
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void playerTurn()
    {
        countDown = Math.max(--countDown, 0);

        if(countDown > 0)
            playerTimeContainer.setWidth((countDown * timerContainerWidth) / time);
        else
            ensurePlayerMovement();
    }

    private void ensurePlayerMovement()
    {
        if(playerTurn && countDown == 0 && selectedCount == 0)
        {
            int row = 0;
            int column = 0;

            for(Node node : gameGrid.getChildren())
            {
                if(node instanceof AnchorPane)
                {
                    row = GridPane.getRowIndex(node);
                    column = GridPane.getColumnIndex(node);
                    break;
                }
            }

            chooseNim(row, column);
        }
    }

    private void computerMove()
    {
        int move = findBestMove();

        if(move == nim && nim != 1)
            move--;

        for(int i = 0 ; i < move ; i++)
        {
            for(Node node : gameGrid.getChildren())
            {
                if(node instanceof AnchorPane)
                {
                    chooseNim(GridPane.getRowIndex(node), GridPane.getColumnIndex(node));
                    break;
                }
            }
        }
    }

    private int findBestMove()
    {
        int[] result = minimax(nim, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        return result[1];
    }

    private int[] minimax(int nim, int depth, int alpha, int beta, boolean isMaximizingPlayer)
    {
        if (depth == 0 || nim <= 0) {
            int evaluation = evaluate(nim);
            return new int[]{evaluation, -1};
        }

        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            int bestMove = -1;

            for (int i = 1; i <= 3; i++) {
                int eval = minimax(nim - i, depth - 1, alpha, beta, false)[0];
                maxEval = Math.max(maxEval, eval);

                if (eval > alpha) {
                    alpha = eval;
                    bestMove = i;
                }

                if (beta <= alpha) {
                    break;
                }
            }

            return new int[]{maxEval, bestMove};
        } else {
            int minEval = Integer.MAX_VALUE;
            int bestMove = -1;

            for (int i = 1; i <= 3; i++) {
                int eval = minimax(nim - i, depth - 1, alpha, beta, true)[0];
                minEval = Math.min(minEval, eval);

                if (eval < beta) {
                    beta = eval;
                    bestMove = i;
                }

                if (beta <= alpha) {
                    break;
                }
            }

            return new int[]{minEval, bestMove};
        }
    }

    private static int evaluate(int nim)
    {
        return nim % 2 == 0 ? 1 : -1;
    }

    private void flipPlayers()
    {
        selectedCount = 0;
        countDown = time;
        playerTurn = !playerTurn;
        gamePane.setDisable(!playerTurn);
        playerTimeContainer.setWidth(timerContainerWidth);
    }

    private boolean isGameOver()
    {
        return nim == 0;
    }

    private void gameOver()
    {
        stopGame();
        showGameResult();
        leaveGame();
    }

    private void showGameResult()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.initOwner(anchorPane.getScene().getWindow());
        alert.setContentText(!playerTurn ? playerName + " Wins The Game!" : "Computer Wins The Game!");
        alert.show();
    }

    private void chooseNim(int row, int column)
    {
        if(selectedCount++ == 3)
        {
            countDown = 0;
            return;
        }

        for(Node node : gameGrid.getChildren())
        {
            if(node instanceof AnchorPane && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column)
            {
                nim--;
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

    private void stopGame()
    {
        if (timeline != null)
        {
            timeline.stop();
            timeline = null;
        }
    }

    private GridPane createGraphicalGameGrid(double width, double height)
    {
        gameGrid = new GridPane();

        gameGrid.setMinWidth(width);
        gameGrid.setMinHeight(height);

        gameGrid.setVgap(2);
        gameGrid.setHgap(5);

        gameGrid.setAlignment(Pos.CENTER);

        return  gameGrid;
    }

    private void initGameGrid(GridPane gameGrid)
    {
        NimClickListener nNimClickListener = this::chooseNim;

        int rowIndex = 0;
        int columnIndex = 0;

        for(int i = 0 ; i < nim ; i++)
        {
            try
            {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getResource("view/n-game-nim.fxml"));
                AnchorPane nim = fxmlLoader.load();

                if(columnIndex == 9)
                {
                    rowIndex++;
                    columnIndex = 0;
                }

                NimController nimController = fxmlLoader.getController();
                nimController.setData(rowIndex, columnIndex, nNimClickListener);

                gameGrid.add(nim, columnIndex++, rowIndex);
            }
            catch (IOException exception)
            {
                System.out.println(exception.getMessage());
            }
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
        selectedCount = 0;

        Platform.runLater(() -> {
            playerNameLabel.setText(playerName);
            playerTurn = new Random().nextBoolean();

            gamePane.setDisable(!playerTurn);
            timerContainerWidth = playerTimeContainer.getWidth();

            GridPane gameGrid = createGraphicalGameGrid(gamePane.getWidth(), gamePane.getHeight());
            initGameGrid(gameGrid);
            startGame();

            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> stopGame());
        });
    }
}