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
    private Label playerName;

    @FXML
    private Rectangle playerTimeContainer;

    private GridPane gameGrid;

    private int []piles;

    private int selectedPile;

    private int countDown;

    private boolean playerTurn;

    private double timerContainerWidth;

    private Timeline timeline;

    @FXML
    void onAnchorClick(MouseEvent event)
    {
        if (event.getButton() == MouseButton.SECONDARY && playerTurn)
            flipPlayers();
    }

    private void startGame()
    {
        countDown = getTime();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev ->
        {
            if(playerTurn)
            {
                playerTimeContainer.setWidth((countDown * timerContainerWidth) / getTime());
                ensurePlayerMovement();
                countDown--;
            }
            else
            {
                flipPlayers();
            }

            if(isGameOver(piles))
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
            flipPlayers();
        }
    }

    private void flipPlayers()
    {
        selectedPile = -1;
        countDown = getTime();
        playerTurn = !playerTurn;
        gamePane.setDisable(!playerTurn);
        playerTimeContainer.setWidth(timerContainerWidth);
    }

    private boolean isGameOver(int []piles)
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
        alert.setContentText(playerTurn ? getPlayerName() + " Wins The Game!" : "Computer Wins The Game!");
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
        countDown = 0;
        selectedPile = -1;

        playerName.setText(getPlayerName());
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