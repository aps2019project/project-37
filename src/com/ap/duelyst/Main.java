package com.ap.duelyst;

import com.ap.duelyst.controller.Controller;
import com.ap.duelyst.model.Utils;
import com.ap.duelyst.view.battle.BattleController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    private static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("view/battle/battle.fxml"));
        Parent root = loader.load();
        BattleController battleController = loader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 300));
        primaryStage.getScene().getStylesheets().add("com/ap/duelyst/Bugatti.css");
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (controller != null && controller.getGame() != null) {
                    battleController.setGame(controller);
                }
            }
        }.start();
        primaryStage.getScene().setCursor(new ImageCursor(new Image(Utils.getPath("mouse_auto.png"))));
        primaryStage.show();
    }


    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            controller = new Controller();
            controller.start();
        });
        thread.setDaemon(true);
        thread.start();
        launch(args);
    }
}
