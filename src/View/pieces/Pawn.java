package View.pieces;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import static View.Game.TILE_SIZE ;


/**
 * Class represents a pawn in the board.
 */
public class Pawn extends StackPane{

    private PawnType type;
    private double mouseX, mouseY;
    private double oldX, oldY;

    public PawnType getType() {
        return type;
    }
    public double getOldX() {
        return oldX;
    }
    public double getOldY() {
        return oldY;
    }

    public Pawn(PawnType type, String color, int x, int y) {
        this.type = type;
        moveTo(x, y);

        Circle ellipse = new Circle(TILE_SIZE * 0.3215);
        ellipse.setFill(Color.web(color));
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);

        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        Text text = new Text(color);
        text.setTranslateX(8);
        text.setTranslateY(-10);
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        getChildren().add(text);

        getChildren().addAll(ellipse);
        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            // getLayoutX() + (e.getSceneX()-mouseX)
            // (e.getSceneX()-mouseX) calculates horizontal distance mouse has moved since the drag operation started.
            // getLayoutX current X coordinate of the node within its parent's coordinate system.
            relocate(getLayoutX() + (e.getSceneX()-mouseX), getLayoutY() + (e.getSceneY() - mouseY));

            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });
    }

    public void moveTo(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    public enum PawnType {
        RED, BLUE;
    }

}
