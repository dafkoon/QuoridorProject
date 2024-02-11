package View.pieces;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;

import static Controller.Controller.TILE_SIZE;
import static Controller.Controller.BOARD_DIMENSION;


public class NotationLabel extends Label {
    int row;
    int col;
    public NotationLabel(int col, int row) {
        this.col = col; // letter
        this.row = BOARD_DIMENSION - (row+1); // number

        setText(generateNotation());
        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);
        relocate(col * TILE_SIZE, row * TILE_SIZE);
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
