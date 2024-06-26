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
     *
     * @param type   the type of the pawn
     * @param color  the color of the pawn
     * @param xPixel the x-coordinate of the initial position
     * @param yPixel the y-coordinate of the initial position
     */
    public Pawn(PawnType type, PawnColor color, int xPixel, int yPixel) {
        this.type = type;
        this.color = color;
        move(xPixel, (BOARD_SIZE - TILE_SIZE) - yPixel);

        Circle circle = new Circle(TILE_SIZE * 0.3125);
        circle.setFill(Color.web(color.name()));
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(TILE_SIZE * 0.03);
        circle.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        circle.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);

        Text text = new Text(type.name());
        text.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        text.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 8));
        text.setFill(Color.BEIGE);
        getChildren().addAll(circle, text);
    }

    /**
     * Gets the color of the pawn.
     *
     * @return the color of the pawn
     */
    public PawnColor getColor() {
        return color;
    }

    /**
     * Gets the type of the pawn.
     *
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
     *
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
     *
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
     *
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

    /**
     * When the pawn is pressed, keep its x and y coordinates.
     *
     * @param event the event object.
     */
    public void mousePressed(MouseEvent event) {
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();
    }

    /**
     * When the pawn is dragged, continually calculate its location while moving.
     *
     * @param event the event object.
     */
    public void mouseDragged(MouseEvent event) {
        relocate(getLayoutX() + (event.getSceneX() - mouseX), getLayoutY() + (event.getSceneY() - mouseY));
        mouseX = event.getSceneX();
        mouseY = event.getSceneY();

    }

    /**
     * Returns a string representation of the pawn, including its type and color.
     *
     * @return a string representation of the pawn
     */
    @Override
    public String toString() {
        return type.name() + "/" + color.name() + " player";
    }

    public enum PawnColor {
        BLUE, RED
    }

    public enum PawnType {
        HUMAN, AI
    }


}

