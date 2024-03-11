package View;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SelectionScreen {
    private final Runnable selectionHandler;

    SelectionScreen(Runnable selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

    Scene createScene() {
        Button humanButton = new Button("Human");
        Button aiButton = new Button("AI");

        humanButton.setOnAction(e -> selectionHandler.run());
        aiButton.setOnAction(e -> selectionHandler.run());

        HBox hbox = new HBox(20);
        hbox.setPadding(new Insets(50));
        hbox.getChildren().addAll(humanButton, aiButton);

        return new Scene(hbox);
    }
}
