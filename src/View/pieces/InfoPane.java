package View.pieces;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class InfoPane extends Pane {
    private int yOffset = 10;


    public InfoPane() {}

    public void addInfo(String playerName, int wallsLeft, String playerColor) {

        Text infoText = new Text();
        infoText.setText("Player: " + playerName + "\nWalls left: " + wallsLeft);
        infoText.setFill(Color.valueOf(playerColor));
        infoText.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        infoText.setTranslateY(yOffset);
        getChildren().add(infoText);
        yOffset += 50; // Increment Y offset for next info
    }

    public void updateInfo(int currentTurn, String playerName, int wallsLeft, String playerColor) {
        Text infoText = (Text) getChildren().get(currentTurn);
        infoText.setText("Player: " + playerName + "\nWalls left: " + wallsLeft);
        infoText.setFill(Color.valueOf(playerColor));
        infoText.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
    }


}
