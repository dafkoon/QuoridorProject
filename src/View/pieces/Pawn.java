package View.pieces;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import static Controller.Controller.TILE_SIZE;
import static View.Game.BOARD_SIZE;


/**
 * Class represents a pawn in the board.
 */
public class Pawn extends StackPane{

    private final PawnType type;
    private final PawnColor color;
    public double mouseX, mouseY;
    private double oldX, oldY;

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
        Circle ellipse = new Circle(TILE_SIZE * 0.3125);
        ellipse.setFill(Color.web(color.name()));
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);
        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);

        Text text = new Text(type.name());
        text.setTranslateX(8);
        text.setTranslateY(-10);
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        getChildren().add(text);
        getChildren().addAll(ellipse);
    }

    public void reverse() {
        relocate(oldX, oldY);
    }

    public void move(double xPixel, double yPixel) {
        oldX = xPixel;
        oldY = yPixel;
        relocate(xPixel, yPixel);
    }

    public enum PawnType {
        HUMAN, AI;
    }

    public enum PawnColor {
        BLUE, RED
    }

}
