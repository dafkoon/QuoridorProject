package View.pieces;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static Controller.Controller.TILE_SIZE;
public class Tile extends Rectangle{
    private int row;
    private int col;

    public Tile(int x, int y) {
        this.row = x;
        this.col = y;

        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);
        relocate(x * TILE_SIZE, y * TILE_SIZE);
        setFill(Color.BURLYWOOD);
        setStroke(Color.BLACK);
        setStrokeWidth(1.0);


//        String notation = algebraicNotation();
//        Text text = new Text(notation);
//        text.setFill(Color.WHITE);
//        text.setX(x * TILE_SIZE - TILE_SIZE/2);
//        text.setY(y * TILE_SIZE - TILE_SIZE/2);
//
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String algebraicNotation() {
        char row = (char) ('1' + this.row);
        char col = (char) ('a' + this.col);
        return ""+col+row;
        }
    }
