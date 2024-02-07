package View.pieces;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;

import static Controller.Controller.TILE_SIZE;
import javafx.scene.input.MouseEvent;

import java.sql.SQLOutput;


/**
 * Class represents a pawn in the board.
 */
public class Pawn extends StackPane{

    private final PawnType type;
    private final PawnColor color;
    public double mouseX, mouseY;
    //private int oldX, oldY;

    public PawnColor getColor() { return color;}
    public PawnType getType() {
        return type;
    }
//    public int getOldX() {
//        return oldX;
//    } // Pixel on x axis.
//    public int getOldY() {
//        return oldY;
//    } // Pixel on y axis.

    public Pawn(PawnType type, PawnColor color, int x, int y) {
        this.type = type;
        this.color = color;
        addPawn();
        relocate(x*TILE_SIZE, y*TILE_SIZE);
        pawnActions();
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

    public void pawnActions() {
        // When the pawn is pressed store is location.
        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();

        });

        // When the pawn is dragged continually update its location.
        setOnMouseDragged(e -> {
            if(type == PawnType.HUMAN) {
            // getLayoutX() + (e.getSceneX()-mouseX)
            // e.getSceneX()-mouseX continually calculates horizontal distance mouse has moved since last update.
            // getLayoutX current X coordinate of the node within its parent's coordinate system.
            relocate(getLayoutX() + (e.getSceneX()-mouseX), getLayoutY() + (e.getSceneY() - mouseY));
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
             }
        });

    }

    /**
     * Moves the pawn object to a square.
     * @param x index of square (column).
     * @param y index of square (row).
     */
//    public void moveTo(int x, int y) {
//        oldX = x * TILE_SIZE;
//        oldY = y * TILE_SIZE;
//        relocate(oldX, oldY);
//    }

    public enum PawnType {
        HUMAN, AI;
    }

    public enum PawnColor {
        BLUE, RED
    }

}
