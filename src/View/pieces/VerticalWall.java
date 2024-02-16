package View.pieces;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import static Controller.Controller.TILE_SIZE;

public class VerticalWall extends Rectangle{
    private int x;
    private int y;
    private boolean pressCommit;
    public VerticalWall(int x, int y)
    {
        this.x = x;
        this.y = y;
        setWidth(((double) TILE_SIZE / 10));
        setHeight(((double) TILE_SIZE / 5) + 40);
        relocate((x * TILE_SIZE) + 45, y * TILE_SIZE);
        setFill(Color.SILVER);
        setStrokeWidth(0.1);
    }

    public int getCol() {
        return x;
    }
    public int getRow() {
        return y;
    }
    public void setPressCommit(boolean pressed) {
        this.pressCommit = pressed;
    }
    public boolean isPressCommit() {
        return this.pressCommit;
    }

    public String toAlgebraic(int r, int c) {
        char row = (char) ('1' + r);
        char col = (char) ('a' + c);
        return ""+col+row;
    }
}