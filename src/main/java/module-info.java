module com.nim.game.nim_game {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires javafx.media;

    opens com.nim.game to javafx.fxml;
    exports com.nim.game;
    exports com.nim.game.controller;
    exports com.nim.game.listener;
    exports com.nim.game.model;
    opens com.nim.game.controller to javafx.fxml;
}