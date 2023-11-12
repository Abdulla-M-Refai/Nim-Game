package com.nim.game.controller;

import com.nim.game.model.Move;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.application.Platform;

import javafx.scene.layout.Pane;
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
import java.util.*;

import java.io.IOException;

import static com.nim.game.util.Helper.*;
import static com.nim.game.util.GameSettings.*;

import com.nim.game.listener.NimClickListener;

public class KaylesGameController implements Initializable
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

    private int []nims;

    private int nim;

    private int selectedIndex;

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

            if(isGameOver(nims))
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
        if(playerTurn && countDown == 0 && selectedIndex == -1)
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
        int []groupedNimsArray = sumAdjacentOnes(nims);
        int []dividedNimsArray = divideArray(groupedNimsArray);
        Move move = findBestMove(dividedNimsArray, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

        int row = move.getRow();
        int nimCountToRemove = move.getNimCountToRemove();

        dividedNimsArray[row] -= nimCountToRemove;
        int []newNimsArray = generateNewNimsArray(dividedNimsArray);

        List<Integer> indices = new ArrayList<>();
        for(int i = 0 ; i < nims.length ; i++)
            if(nims[i] != newNimsArray[i])
                indices.add(i);

        for(int i = 0 ; i < nimCountToRemove ; i++)
        {
            for(Node node : gameGrid.getChildren())
            {
                if(node instanceof AnchorPane)
                {
                    int index = GridPane.getRowIndex(node) * 9 + GridPane.getColumnIndex(node);

                    if(indices.contains(index))
                    {
                        chooseNim(GridPane.getRowIndex(node), GridPane.getColumnIndex(node));
                        break;
                    }
                }
            }
        }
    }

    private int[] sumAdjacentOnes(int[] nims)
    {
        int index = 0;
        int[] newNimsArray = new int[nims.length];

        for (int i = 0; i < nims.length; i++)
        {
            if(nims[i] == 0)
                newNimsArray[index++] = 0;

            if(i > 0 && nims[i - 1] == 1)
                continue;

            if (nims[i] == 1)
                newNimsArray[index++] = 1 + getAdjacentOnes(nims, i);
        }

        return Arrays.copyOfRange(newNimsArray, 0, index);
    }

    private int getAdjacentOnes(int[] nims, int index)
    {
        int count = 0;

        for(int i = index - 1; i >= 0 && nims[i] == 1; i--)
            count++;

        for(int i = index + 1; i < nims.length && nims[i] == 1; i++)
            count++;

        return count;
    }

    private int[] divideArray(int[] nims)
    {
        List<Integer> dividedArray = new ArrayList<>();

        for(int nimCount : nims)
        {
            if(nimCount > 3)
            {
                int remaining = nimCount;

                while(remaining > 3)
                {
                    dividedArray.add(3);
                    remaining -= 3;
                }

                if(remaining > 0)
                    dividedArray.add(remaining);
            }
            else
            {
                dividedArray.add(nimCount);
            }
        }

        return dividedArray.stream().mapToInt(Integer::intValue).toArray();
    }

    public int[] generateNewNimsArray(int[] divided)
    {
        int[] nimsArray = new int[nims.length];

        int index = 0;
        for (int nimsCount : divided)
        {
            if (nimsCount == 0)
            {
                nimsArray[index++] = 0;
                continue;
            }

            for (int j = 0; j < nimsCount ; j++)
                nimsArray[index++] = 1;
        }

        return nimsArray;
    }

    private Move findBestMove(int[] piles, int depth, int alpha, int beta, boolean isComputer)
    {
        Move bestMove = new Move(-1, -1);
        int bestRate = !isComputer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < piles.length; i++)
        {
            for (int j = 1; j <= piles[i]; j++)
            {
                int[] newPiles = Arrays.copyOf(piles, piles.length);
                newPiles[i] -= j;

                int rate = minimax(newPiles, depth - 1, alpha, beta, !isComputer);

                if (!isComputer && rate > bestRate)
                {
                    bestRate = rate;
                    bestMove = new Move(i, j);
                    alpha = Math.max(alpha, rate);
                }
                else if (isComputer && rate < bestRate)
                {
                    bestRate = rate;
                    bestMove = new Move(i, j);
                    beta = Math.min(beta, rate);
                }

                if (alpha >= beta)
                {
                    break;
                }
            }
        }

        return bestMove;
    }

    private int minimax(int[] piles, int depth, int alpha, int beta, boolean isComputer)
    {
        if (depth == 0 || isGameOver(piles))
            return !isComputer ? evaluate(piles) : -1;

        int bestRate = !isComputer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0 ; i < piles.length ; i++)
        {
            for (int j = 1 ; j <= piles[i] ; j++)
            {
                int[] newPiles = Arrays.copyOf(piles, piles.length);
                newPiles[i] -= j;

                int rate = minimax(newPiles, depth - 1, alpha, beta, !isComputer);

                if (!isComputer)
                {
                    bestRate = Math.max(bestRate, rate);
                    alpha = Math.max(alpha, rate);
                }
                else
                {
                    bestRate = Math.min(bestRate, rate);
                    beta = Math.min(beta, rate);
                }

                if (alpha >= beta)
                    break;
            }
        }

        return bestRate;
    }

    private int evaluate(int[] piles)
    {
        int value = 0;
        for (int pile : piles)
            value ^= pile;

        return value;
    }

    private void flipPlayers()
    {
        selectedIndex = -1;
        countDown = time;
        playerTurn = !playerTurn;
        gamePane.setDisable(!playerTurn);
        playerTimeContainer.setWidth(timerContainerWidth);
    }

    private static boolean isGameOver(int[] nims)
    {
        for (int nim : nims)
            if (nim == 1)
                return false;

        return true;
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
        int index = row * 9 + column;

        if(selectedIndex == -1)
            selectedIndex = index;
        else if(index != (selectedIndex + 1) && index != (selectedIndex + 2) && index != (selectedIndex - 1) && index != (selectedIndex - 2))
            return;

        for(Node node : gameGrid.getChildren())
        {
            if(node instanceof AnchorPane && GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column)
            {
                nims[row * 9 + column] = 0;
                gameGrid.getChildren().remove(node);

                Pane pane = new Pane();
                pane.setMinWidth(((AnchorPane) node).getWidth());
                pane.setMinHeight(((AnchorPane) node).getMinHeight());

                gameGrid.add(pane, column, row);
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
                fxmlLoader.setLocation(getResource("view/kayles-game-nim.fxml"));
                AnchorPane nim = fxmlLoader.load();

                if(columnIndex == 9)
                {
                    rowIndex++;
                    columnIndex = 0;
                }

                NimController nimController = fxmlLoader.getController();
                nimController.setData(rowIndex, columnIndex, nNimClickListener);

                nims[i] = 1;
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

        nims = new int[nim];

        countDown = 0;
        selectedIndex = -1;

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