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
    public NotationLabel(int rowIndex, int colIndex) {
        this.row = BOARD_DIMENSION-(rowIndex+1); // number
        this.col = colIndex; // letter
        //4
        //3
        //2
        //1
        //  a  b  c  d
//        this.row = y;
//        this.col = x;

        setText(generateNotation());

        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);
        relocate(colIndex * TILE_SIZE, rowIndex * TILE_SIZE);
        setTextFill(Color.BLACK);
        setOpacity(0.25);
        setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        setTranslateX(25);
        setTranslateY(25);
    }

    public String generateNotation() {
        char row = (char) ('a' + this.row);
        char col = (char) ('1' + this.col);
        return ""+row+col;
    }
}
