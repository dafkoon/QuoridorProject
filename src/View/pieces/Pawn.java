package View.pieces;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static Utilities.Constants.BOARD_SIZE;
import static Utilities.Constants.TILE_SIZE;

/**
 * Represents a pawn on the game board.
 * Each pawn has a specific type and color.
 */
public class Pawn extends StackPane {

    private final PawnType type;
    private final PawnColor color;
    private double oldX, oldY;
    public double mouseX, mouseY;


    /**
     * Constructs a new pawn with the specified type, color, and initial position.
     * @param type the type of the pawn
     * @param color the color of the pawn
     * @param xPixel the x-coordinate of the initial position
     * @param yPixel the y-coordinate of the initial position
     */
    public Pawn(PawnType type, PawnColor color, int xPixel, int yPixel) {
        this.type = type;
        this.color = color;
        move(xPixel, (BOARD_SIZE - TILE_SIZE) - yPixel);

        Circle ellipse = new Circle(TILE_SIZE * 0.3125);
        ellipse.setFill(Color.web(color.name()));
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);
        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);

        Text text = new Text(type.name());
        text.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        text.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 8));
        text.setFill(Color.BEIGE);
        getChildren().addAll(ellipse, text);
    }

    /**
     * Gets the color of the pawn.
     * @return the color of the pawn
     */
    public PawnColor getColor() {
        return color;
    }

    /**
     * Gets the type of the pawn.
     * @return the type of the pawn
     */
    public PawnType getType() {
        return type;
    }


    /**
     * Reverses the movement of the pawn, moving it back to its previous position.
     */
    public void reverse() {
        relocate(oldX, oldY);
    }

    /**
     * Moves the pawn to the specified position.
     * @param xPixel the x-coordinate of the new position
     * @param yPixel the y-coordinate of the new position
     */
    public void move(double xPixel, double yPixel) {
        oldX = xPixel;
        oldY = yPixel;
        relocate(xPixel, yPixel);
    }

    /**
     * Converts the given number to a pawn color enum.
     * @param num the number representing the pawn color
     * @return the pawn color enum corresponding to the number
     */
    public static PawnColor intToColor(int num) {
        PawnColor[] colors = PawnColor.values();
        PawnColor enumVal = null;
        if (num >= 0 && num < PawnColor.values().length) {
            enumVal = colors[num];
        }
        return enumVal;
    }

    /**
     * Converts the given number to a pawn type enum.
     * @param num the number representing the pawn type
     * @return the pawn type enum corresponding to the number
     */
    public static PawnType intToType(int num) {
        PawnType[] types = PawnType.values();
        PawnType enumVal = PawnType.HUMAN;
        if (num >= 0 && num < PawnType.values().length) {
            enumVal = types[num];
        }
        return enumVal;
    }

    public void setOnMousePressed(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }

    public void setOnMouseDragged(MouseEvent event) {
        // Continually calculates horizontal distance mouse has moved since last update.
        // getLayoutX: current X coordinate of the node within its parent's coordinate system.
        relocate(getLayoutX() + (event.getSceneX() - mouseX), getLayoutY() + (event.getSceneY() - mouseY));
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();

    }

    /**
     * Returns a string representation of the pawn, including its type and color.
     * @return a string representation of the pawn
     */
    @Override
    public String toString() {
        return "Pawn{" + type.name() +
                " " + color.name() +
                '}';
    }

    public enum PawnColor {
        BLUE, RED
    }

    public enum PawnType {
        HUMAN, AI
    }


}

