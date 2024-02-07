package View;

import java.util.ArrayList;
import java.util.List;

import Model.Board;
import View.pieces.*;
import View.pieces.Pawn.PawnType;
import View.pieces.Pawn.PawnColor;

import Model.GameSession;
import Model.Player;

import Controller.Controller;
import Controller.EventHandler;
import static Controller.Controller.TILE_SIZE;
import static Controller.Controller.BOARD_DIMENSION;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;



public class Game extends Application {
    private final Controller controller = new Controller(); // Access to controller class
    private EventHandler eventHandler;
    //private final Tile[][] board = new Tile[BOARD_DIMENSION][BOARD_DIMENSION];   NOT USED.
    //private final HorizontalWall[][] horizontalWalls = new HorizontalWall[BOARD_DIMENSION][BOARD_DIMENSION];
    //private final VerticalWall[][] verticalWalls = new VerticalWall[BOARD_DIMENSION][BOARD_DIMENSION];


    private final Group tileGroup = new Group();
    private final Group pawnGroup = new Group();
    private final Group horizontalWallGroup = new Group();
    private final Group verticalWallGroup = new Group();
    private final Group labelGroup = new Group();
    private Label currentTurnLabel;
    private Label wallLabel;


    public void start(Stage primaryStage) {
        controller.startup();
        this.eventHandler = controller.startup();
        initPawns();

        currentTurnLabel = new Label();
        wallLabel = new Label();
        Scene scene = new Scene(createBoard());
        primaryStage.getIcons().add(new Image("zres/icon.png"));
        primaryStage.setTitle("Quoridor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Initializes the pawn's location and calls for a method to create the pawn.
     * Calls the setupPawns method.
     */
    public void initPawns() {
        int currentType = 0;
        int[] colPos = new int[]{BOARD_DIMENSION/2, BOARD_DIMENSION/2};
        int[] rowPos = new int[]{BOARD_DIMENSION-1, 0};
        PawnType[] pawnTypes = PawnType.values();
        for(PawnType pawnType : pawnTypes) {
            PawnColor color = (pawnType == PawnType.AI) ? PawnColor.RED : PawnColor.BLUE;
            Pawn pawn = new Pawn(pawnType, color, colPos[currentType], rowPos[currentType]);
            pawnGroup.getChildren().add(pawn);
            currentType++;
            pawnMouseEvents(pawn);
        }
    }

    public void pawnMouseEvents(Pawn pawn) {
        pawn.setOnMousePressed(event -> eventHandler.handlePawnMovement(event, pawn));
        pawn.setOnMouseDragged(event -> eventHandler.handlePawnMovement(event, pawn));
        pawn.setOnMouseReleased(event -> eventHandler.handlePawnMovement(event, pawn));
    }


    private Parent createBoard() {
        Pane root = new Pane();
        //root.setPrefSize((BOARD_DIMENSION * TILE_SIZE) + 150, BOARD_DIMENSION * TILE_SIZE + 30);

        //Add tiles to the board
        for (int row = 0; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION; col++) {
                Tile tile = new Tile(row, col);
                NotationLabel label = new NotationLabel(row, col);
                //board[x][y] = tile;
                tileGroup.getChildren().add(tile);
                labelGroup.getChildren().add(label);
            }
        }
        // Add vertical walls.
        for(int y = 0; y < BOARD_DIMENSION; y++) {
            for(int x = 0; x < BOARD_DIMENSION; x++) {
                VerticalWall wall = new VerticalWall(x, y);
                //verticalWalls[x][y] = wall;
                verticalWallGroup.getChildren().add(wall);
                int thisX = x;
                int thisY = y;
                int nextX = x;
                int nextY = y+1;
                wall.setOnMouseEntered(e -> {
                    if(nextX == BOARD_DIMENSION-1) {
                        return;
                    }
                    if(nextY < BOARD_DIMENSION) {
                        wall.setFill(Color.valueOf("000000"));
                        //verticalWalls[nextX][nextY].setFill(Color.valueOf("000000"));
                    }
                });

            }
        }

        root.getChildren().addAll(tileGroup, labelGroup , pawnGroup, horizontalWallGroup, verticalWallGroup, generateInfoPanel());
        return root;
    }

    public Pane generateInfoPanel() {
        Pane panel = new Pane();
        currentTurnLabel.setText(controller.getCurrentPlayerName() + "'s turn");
        currentTurnLabel.setTextFill(Color.valueOf(controller.getCurrentPlayerColor()));
        currentTurnLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        currentTurnLabel.setTranslateY(10);

        wallLabel.setText("Walls left: " + controller.getCurrentPlayerWallsLeft());
        wallLabel.setTextFill(Color.valueOf(controller.getCurrentPlayerColor()));
        wallLabel.setTranslateY(20);

        panel.getChildren().addAll(currentTurnLabel, wallLabel);
        panel.setTranslateX(BOARD_DIMENSION*TILE_SIZE +10);
        return panel;
    }

    public void validMoves() {;}

    /**
     * Converts pixel value to coordinate.
     * @param pixel pixel location.
     * @return a coordinate.
     */
    private int pixelToBoard(double pixel) {
        return (int)(pixel + TILE_SIZE /2 ) / TILE_SIZE;
    }


    public static void main(String[] args) {
        launch(args);
    }
}

