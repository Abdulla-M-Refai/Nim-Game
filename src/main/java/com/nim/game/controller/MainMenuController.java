package com.nim.game.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.stage.Stage;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import com.nim.game.model.GameType;
import com.nim.game.model.GameLevel;

import static com.nim.game.util.Helper.*;
import static com.nim.game.util.GameSettings.*;

public class MainMenuController implements Initializable
{
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ComboBox<String> gameType;

    @FXML
    private TextField nameField;

    @FXML
    private TextField nimField;

    @FXML
    private TextField timeField;

    @FXML
    private ToggleGroup level;

    @FXML
    private RadioButton easy;

    @FXML
    private RadioButton medium;

    @FXML
    private RadioButton hard;

    @FXML
    private Button toggleMuteButton;

    private ImageView toggleMuteButtonView;

    private boolean mute;

    private GameType type;

    private GameLevel gameLevel;

    private int time;

    private int nim;

    @FXML
    void setGameType()
    {
        type = GameType.valueOf(gameType.getSelectionModel().getSelectedItem());
    }

    @FXML
    void incNim()
    {
        nim = nim >= getMaxNim() ? getMaxNim() : nim + 1;
        nimField.setText(String.valueOf(nim));
    }

    @FXML
    void decNim()
    {
        nim = nim <= getMinNim() ? getMinNim() : nim - 1;
        nimField.setText(String.valueOf(nim));
    }

    @FXML
    void incTime()
    {
        time = time >= getMaxTime() ? getMaxTime() : ++time;
        timeField.setText(time + "s");
    }

    @FXML
    void decTime()
    {
        time = time <= getMinTime() ? getMinTime() : --time;
        timeField.setText(time + "s");
    }

    @FXML
    void setGameLevel()
    {
        if(level.getSelectedToggle().equals(easy))
            gameLevel = GameLevel.EASY;
        else if(level.getSelectedToggle().equals(medium))
            gameLevel = GameLevel.MEDIUM;
        else if(level.getSelectedToggle().equals(hard))
            gameLevel = GameLevel.HARD;
    }

    @FXML
    void play() throws Exception
    {
        if(!nameField.getText().isBlank())
            setPlayerName(nameField.getText());

        setNim(nim);
        setTime(time);
        setType(type);
        setLevel(gameLevel);

        loadView(type.getType(), (Stage)anchorPane.getScene().getWindow(), anchorPane);
    }

    @FXML
    void toggleMute()
    {
        mute = !mute;
        setMute(mute);

        String imgSource = mute ?
            String.valueOf(getResource("assets/image/speaker.png")) :
            String.valueOf(getResource("assets/image/muted.png"));

        toggleMuteButtonView.setImage(new Image(imgSource));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        mute = getMute();

        type = getType();
        gameLevel = getLevel();

        time = getTime();
        nim = getNim();

        nameField.setText(getPlayerName());

        gameType.getItems()
            .addAll(
                Stream.of(GameType.values())
                .map(GameType::name)
                .toArray(String[]::new)
            );

        gameType.getSelectionModel().select(type.name());

        timeField.setText(time + "s");
        nimField.setText(String.valueOf(nim));

        if(gameLevel == GameLevel.EASY)
            level.selectToggle(easy);
        else if(gameLevel == GameLevel.MEDIUM)
            level.selectToggle(medium);
        else if(gameLevel == GameLevel.HARD)
            level.selectToggle(hard);

        String speakerImg = getMute() ?
            String.valueOf(getResource("assets/image/speaker.png")) :
            String.valueOf(getResource("assets/image/muted.png"));

        toggleMuteButtonView = new ImageView(new Image(speakerImg));

        toggleMuteButtonView.setFitHeight(40);
        toggleMuteButtonView.setFitWidth(40);
        toggleMuteButtonView.setPreserveRatio(true);

        toggleMuteButton.setGraphic(toggleMuteButtonView);

        playMusic();
    }
}