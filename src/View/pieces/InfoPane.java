package View.pieces;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class InfoPane extends Pane {
    private Pane panel;
    private int yOffset = 10;

    private String currentPlayerName;
    private String currentPlayerColor;

    public InfoPane() {
        panel = new Pane();
    }
    public void addInfo(String playerName, int wallsLeft, String playerColor) {
        setCurrentPlayerName(playerName);
        setCurrentPlayerColor(playerColor);

        Text infoText = new Text();
        infoText.setText("Player: " + playerName + "\nWalls left: " + wallsLeft);
        infoText.setFill(Color.valueOf(playerColor));
        infoText.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        infoText.setTranslateY(yOffset);
        getChildren().add(infoText);
        yOffset += 50; // Increment Y offset for next info
    }

    public void updateInfo(int currentTurn, String playerName, int wallsLeft, String playerColor) {
        setCurrentPlayerName(playerName);
        setCurrentPlayerColor(playerColor);

        Text infoText = (Text) getChildren().get(currentTurn);
        infoText.setText("Player: " + playerName + "\nWalls left: " + wallsLeft);
        infoText.setFill(Color.valueOf(playerColor));
        infoText.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
    }


    public void setCurrentPlayerName(String currentPlayerName) {
        this.currentPlayerName = currentPlayerName;
    }

    public void setCurrentPlayerColor(String currentPlayerColor) {
        this.currentPlayerColor = currentPlayerColor;
    }


}
