package com.ap.duelyst.view;

import com.ap.duelyst.model.Utils;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class DialogController {
    private StackPane root;
    private HBox dialog;
    private Label dialogText;
    private VBox dialogContainer;
    private EventHandler<ActionEvent> eventHandler;

    public DialogController(StackPane root, HBox dialog, Label dialogText,
                            VBox dialogContainer) {
        this.root = root;
        this.dialog = dialog;
        this.dialogText = dialogText;
        this.dialogContainer = dialogContainer;
        prepareDialog();
    }

    private void prepareDialog() {
        dialogText.prefWidthProperty().bind(root.widthProperty().multiply(.2));
        dialog.prefWidthProperty().bind(root.widthProperty().multiply(.35));
        dialog.prefHeightProperty().bind(dialog.prefWidthProperty().multiply(.287));
        String back = Utils.getPath("dialog_plate@2x.png");
        dialog.getStyleClass().add("back");
        dialog.setStyle("-fx-background-image: url('" + back + "')");
        dialogContainer.setOnMouseClicked(event -> hideDialog());
        dialogContainer.setVisible(false);
    }


    public void showDialog(String message, boolean hasButton) {
        dialogContainer.getChildren().removeIf(node -> node instanceof Button);
        if (hasButton) {
            Button button = new Button("Yes");
            button.getStyleClass().add("button-primary");
            button.setPrefSize(161.1, 50);
            button.setOnAction(eventHandler);
            dialogContainer.getChildren().add(button);
        }
        dialogText.setText(message);
        FadeTransition fade = new FadeTransition(Duration.millis(300),
                dialogContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        dialogContainer.setVisible(true);
        fade.play();
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), dialog);
        scale.setFromX(.4);
        scale.setFromY(.4);
        scale.setToY(1);
        scale.setToX(1);
        scale.play();
    }

    public void showDialog(String message) {
        showDialog(message, false);
    }

    public void hideDialog() {
        FadeTransition fade = new FadeTransition(Duration.millis(300),
                dialogContainer);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.play();
        fade.setOnFinished(event -> dialogContainer.setVisible(false));
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), dialog);
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToY(.4);
        scale.setToX(.4);
        scale.play();
    }

    public void setEventHandler(EventHandler<ActionEvent> eventHandler) {
        this.eventHandler = eventHandler;
    }
}
