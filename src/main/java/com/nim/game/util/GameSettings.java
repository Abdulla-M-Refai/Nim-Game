package com.nim.game.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.util.Duration;

import java.util.Arrays;

import com.nim.game.model.GameType;
import com.nim.game.model.GameLevel;

public class GameSettings
{
    private static final int maxTime = 30;

    private static final int minTime = 10;

    private static final int maxNim = 49;

    private static final int minNim = 16;

    private static String playerName = "Player One";

    private static GameType gameType = GameType.STANDARD;

    private static GameLevel gameLevel = GameLevel.EASY;

    private static int time = minTime;

    private static int nim = minNim;

    private static boolean mute = false;

    private static final String mainMusic = String.valueOf(Helper.getResource("assets/music/main-music.mp3"));

    private static final Media media = new Media(mainMusic);

    private static final MediaPlayer mediaPlayer = new MediaPlayer(media);

    private GameSettings()
    {
    }

    public static String getPlayerName()
    {
        return playerName;
    }

    public static void setPlayerName(String playerName) throws Exception
    {
        if(playerName == null || playerName.isBlank())
            throw new Exception("messing player Name");

        GameSettings.playerName = playerName;
    }

    public static GameType getType()
    {
        return gameType;
    }

    public static void setType(GameType gameType) throws Exception
    {
        if(!Arrays.stream(GameType.values()).toList().contains(gameType))
            throw new Exception("illegal game type");

        GameSettings.gameType = gameType;
    }

    public static GameLevel getLevel()
    {
        return gameLevel;
    }

    public static void setLevel(GameLevel gameLevel) throws Exception
    {
        if(!Arrays.stream(GameLevel.values()).toList().contains(gameLevel))
            throw new Exception("illegal level");

        GameSettings.gameLevel = gameLevel;
    }

    public static int getTime()
    {
        return time;
    }

    public static void setTime(int time) throws Exception
    {
        if(time > maxTime || time < minTime)
            throw new Exception("illegal time amount");

        GameSettings.time = time;
    }

    public static int getNim()
    {
        return nim;
    }

    public static void setNim(int nim) throws Exception
    {
        if(nim > maxNim || nim < minNim)
            throw new Exception("illegal nim amount");

        GameSettings.nim = nim;
    }

    public static int getMaxTime()
    {
        return maxTime;
    }

    public static int getMinTime()
    {
        return minTime;
    }

    public static int getMaxNim()
    {
        return maxNim;
    }

    public static int getMinNim()
    {
        return minNim;
    }

    public static void playMusic()
    {
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.ZERO));
        mediaPlayer.setAutoPlay(true);
    }

    public static boolean getMute()
    {
        return mute;
    }

    public static void setMute(boolean mute)
    {
        GameSettings.mute = mute;
        mediaPlayer.setMute(mute);
    }
}