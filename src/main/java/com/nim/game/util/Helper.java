package com.nim.game.util;

import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import javafx.animation.FadeTransition;

import javafx.util.Duration;

import java.net.URL;
import java.io.IOException;

import com.nim.game.Main;

public class Helper
{
    private Helper()
    {
    }

    public static URL getResource(String resource)
    {
        return Main.class.getResource(resource);
    }

    public static void loadView(String url, Stage stage, Node node)
    {
        double duration = (stage.getScene() == null) ? 0 : 1000;

        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.millis(duration));
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished((OuterActionEvent)->
        {
            try
            {
                AnchorPane root = FXMLLoader.load(getResource(url));
                stage.getScene().setRoot(root);
                root.setOpacity(0);

                fadeTransition.setNode(root);
                fadeTransition.setDuration(Duration.millis(1000));
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);
                fadeTransition.setOnFinished((InnerActionEvent)->{});
                fadeTransition.play();
            }
            catch(IOException e)
            {
                System.out.println(e.getMessage());
            }
        });
        fadeTransition.play();
    }
}
