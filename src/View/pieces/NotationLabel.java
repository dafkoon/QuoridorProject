package View.pieces;

import Model.Board;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static Controller.Controller.TILE_SIZE;
import static Controller.Controller.BOARD_DIMENSION;


public class NotationLabel extends Label {
    int row;
    int col;
    public NotationLabel(int x, int y) {
        this.row = BOARD_DIMENSION-(BOARD_DIMENSION-x); // number
        this.col = BOARD_DIMENSION-(y+1); // letter

        setText(generateNotation());

        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);
        relocate(x * TILE_SIZE, y * TILE_SIZE);
        setTextFill(Color.BLACK);
        setOpacity(0.25);
        setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        setTranslateX(25);
        setTranslateY(25);
    }

    public String generateNotation() {
        char row = (char) ('1' + this.row);
        char col = (char) ('a' + this.col);
        return ""+col+row;
    }
}
