package View.pieces;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static Controller.Controller.TILE_SIZE;
import static Controller.Controller.BOARD_DIMENSION;


public class NotationLabel extends Label {
    int row;
    int col;
    public NotationLabel(Tile tile) {
        this.col = col; // letter
        this.row = row; // number
        setText(tile.notation());

        setText(generateNotation());
        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);
        relocate(col * TILE_SIZE, (BOARD_DIMENSION-(row+1)) * TILE_SIZE);
        setTextFill(Color.BLACK);
        setOpacity(0.25);
        setTranslateX(20);
        setTranslateY(20);
        setFont(Font.font("Verdana", FontWeight.BOLD, 12));
    }
    public String generateNotation() {
        char row = (char) ('1' + this.row);
        char col = (char) ('a' + this.col);
        return ""+col+row;
    }
}
