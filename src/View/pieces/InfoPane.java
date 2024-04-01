package View.pieces;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;



/**
 * Represents an information panel displaying player information such as name, remaining walls, and color.
 */
public class InfoPane extends Pane {
    private int yOffset = 10;

    /**
     * Constructs a new information panel.
     */
    public InfoPane() {}

    /**
     * Adds player information to the information panel.
     *
     * @param playerName the name of the player
     * @param wallsLeft the number of walls left for the player
     * @param playerColor the color of the player
     */
    public void addInfo(String playerName, int wallsLeft, String playerColor) {
        // Create a text node to display player information
        Text infoText = new Text();
        infoText.setText("Player: " + playerName + "\nWalls left: " + wallsLeft);
        infoText.setFill(Color.valueOf(playerColor));
        infoText.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        infoText.setTranslateY(yOffset);
        // Add the text node to the information panel
        getChildren().add(infoText);
        // Increment Y offset for next player information
        yOffset += 50;
    }

    /**
     * Updates the player information displayed in the information panel.
     *
     * @param currentTurn the index of the player whose information is being updated
     * @param playerName the updated name of the player
     * @param wallsLeft the updated number of walls left for the player
     * @param playerColor the updated color of the player
     */
    public void updateInfo(int currentTurn, String playerName, int wallsLeft, String playerColor) {
        // Retrieve the text node corresponding to the player's information
        Text infoText = (Text) getChildren().get(currentTurn);
        // Update the text content with the new player information
        infoText.setText("Player: " + playerName + "\nWalls left: " + wallsLeft);
        infoText.setFill(Color.valueOf(playerColor));
        infoText.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
    }
}

