package View.pieces;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static Utilities.Constants.*;


/**
 * Class represents a pawn in the board.
 */
public class Pawn extends StackPane{

    private final PawnType type;
    private final PawnColor color;
    public double mouseX, mouseY;
    private double oldX, oldY;

    @Override
    public String toString() {
        return "Pawn{" + type.name() +
                " " + color.name() +
                '}';
    }

    public PawnColor getColor() { return color;}
    public PawnType getType() {
        return type;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public Pawn(PawnType type, PawnColor color, int xPixel, int yPixel) {
        this.type = type;
        this.color = color;
        //relocate(xIndex*TILE_SIZE, yIndex*TILE_SIZE);
//        relocate(xIndex * TILE_SIZE, (BOARD_DIMENSION - 1 - yIndex) * TILE_SIZE);
        move(xPixel, (BOARD_SIZE-TILE_SIZE) - yPixel);
        addPawn();
    }

    /**
     * Adds the pawn as ellipses and places them in their starting positions.
     * Allows moving the pawn across the board and when released positions them in the center of Square.
     */
    public void addPawn() {
//        Image pawnImage = new Image("quoridor/zres/pawn_image.png");


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

    public void reverse() {
        relocate(oldX, oldY);
    }

    public void move(double xPixel, double yPixel) {
        oldX = xPixel;
        oldY = yPixel;
        relocate(xPixel, yPixel);
    }

    public static PawnColor intToColor(int num) {
        PawnColor[] colors = PawnColor.values();
        PawnColor enumVal = null;
        if(num >= 0 && num < PawnColor.values().length) {
            enumVal = colors[num];
        }
        return enumVal;
    }

    public static PawnType intToType(int num) {
        PawnType[] types = PawnType.values();
        PawnType enumVal = PawnType.HUMAN;
        if(num >= 0 && num < PawnType.values().length) {
            enumVal = types[num];
        }
        return enumVal;
    }

    public enum PawnType {
        HUMAN, AI;
    }

    public enum PawnColor {
        BLUE, RED
    }

}
