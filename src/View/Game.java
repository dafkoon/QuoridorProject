package View;
import java.util.List;

import View.pieces.*;
import View.pieces.Pawn.PawnType;
import View.pieces.Pawn.PawnColor;

import Controller.Controller;
import Controller.EventHandler;
import static Controller.Controller.TILE_SIZE;
import static Controller.Controller.BOARD_DIMENSION;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;



public class Game extends Application {
    public static final int BOARD_SIZE = TILE_SIZE * BOARD_DIMENSION;

    private Controller controller = new Controller(this); // Access to controller class
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
    private Pane root;


    public void start(Stage primaryStage) {
        this.eventHandler = controller.startup();
        initPawns();

        currentTurnLabel = new Label();
        wallLabel = new Label();
        root = createBoard();
        Scene scene = new Scene(root);
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
        int[] xPixel = new int[]{BOARD_SIZE/2-TILE_SIZE/2, BOARD_SIZE/2-TILE_SIZE/2};
        int[] yPixel = new int[]{0, BOARD_SIZE-TILE_SIZE};
        PawnType[] pawnTypes = PawnType.values();
        for(PawnType pawnType : pawnTypes) {
            PawnColor color = (pawnType == PawnType.AI) ? PawnColor.RED : PawnColor.BLUE;
            Pawn pawn = new Pawn(pawnType, color, xPixel[currentType], yPixel[currentType]);
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


    private Pane createBoard() {
        Pane root = new Pane();
        root.setPrefSize((BOARD_DIMENSION * TILE_SIZE) + 120, BOARD_DIMENSION * TILE_SIZE);

        //Add tiles to the board
        for (int row = BOARD_DIMENSION - 1; row >= 0; row--) {
            for (int col = 0; col < BOARD_DIMENSION; col++) {
                Tile tile = new Tile(col, row);
                NotationLabel label = new NotationLabel(col, row);
                tileGroup.getChildren().add(tile);
                labelGroup.getChildren().add(label);
            }
        }
        // Add vertical walls.
        for (int row = BOARD_DIMENSION-1; row >= 0; row--) { // from top to bottom
            for (int col = 0; col < BOARD_DIMENSION-1; col++) { // from left to right
                int thisRow = row;
                int thisCol = col;
                VerticalWall wall = new VerticalWall(thisCol, thisRow);
                verticalWallGroup.getChildren().add(wall);
                wall.setOnMouseEntered(e -> {
                    if(thisRow > 0) {
                        if(!controller.doesWallExist(toAlgebraic(BOARD_DIMENSION - (thisRow + 1), thisCol), false)) {
                            VerticalWall wallAbove = findVwall(thisRow - 1, thisCol);
                            wallAbove.setFill(Color.BLACK);
                            wall.setFill(Color.BLACK);
                        }
                    }
                });
                wall.setOnMouseExited(e -> {
                    if(thisRow > 0 && !wall.isPressCommit()) {
                        if(!controller.doesWallExist(toAlgebraic(BOARD_DIMENSION - (thisRow + 1), thisCol), false)) {
                            VerticalWall wallAbove = findVwall(thisRow - 1, thisCol);
                            wallAbove.setFill(Color.SILVER);
                            wall.setFill(Color.SILVER);
                        }
                    }
                });
                wall.setOnMousePressed((e -> {
                    if(thisRow == 0 || controller.wallsLeft() == 0 || controller.getTurn() != 0)
                        return;
                    if(e.isPrimaryButtonDown()) {
                        if(controller.doesWallExist(toAlgebraic(BOARD_DIMENSION - (thisRow + 1), thisCol), false)) {
                            System.out.println("There is already a wall here.");
                        }
                        else {
                            controller.addWall(toAlgebraic(BOARD_DIMENSION - (thisRow + 1), thisCol), false);
                            VerticalWall wallAbove = findVwall(thisRow - 1, thisCol);
                            wall.setFill(Color.BLACK);
                            wallAbove.setFill(Color.BLACK);
                            wall.setPressCommit(true);
                            generateInfoPanel();
                        }
                    }
                }));
            }
        }
        // Add horizontal walls.
        for (int row = BOARD_DIMENSION-1; row > 0; row--) { // from top to bottom
            for (int col = 0; col < BOARD_DIMENSION; col++) { // from left to right
                int thisRow = row;
                int thisCol = col;
                HorizontalWall wall = new HorizontalWall(thisCol, thisRow);
                horizontalWallGroup.getChildren().add(wall);
                wall.setOnMouseEntered(e -> {
                    if(thisCol < BOARD_DIMENSION-1) {
                        if(!controller.doesWallExist(toAlgebraic(BOARD_DIMENSION - (thisRow + 1), thisCol), true)) {
                            HorizontalWall rightWall = findHwall(thisRow, thisCol+1);
                            rightWall.setFill(Color.BLACK);
                            wall.setFill(Color.BLACK);
                        }

                    }
                });
                wall.setOnMouseExited(e -> {
                    if(thisCol < BOARD_DIMENSION-1 && !wall.isPressCommit()) {
                        if(!controller.doesWallExist(toAlgebraic(BOARD_DIMENSION - (thisRow + 1), thisCol), true)) {
                            HorizontalWall rightWall = findHwall(thisRow, thisCol+1);
                            rightWall.setFill(Color.SILVER);
                            wall.setFill(Color.SILVER);
                        }
                    }
                });

                wall.setOnMousePressed((e -> {
                    if(thisCol == BOARD_DIMENSION-1 || controller.wallsLeft() == 0 || controller.getTurn() != 0)
                        return;
                    if(controller.doesWallExist(toAlgebraic(BOARD_DIMENSION - (thisRow + 1), thisCol), true)) {
                        System.out.println("There is already a wall here.");
                    }
                    else {
                        controller.addWall(toAlgebraic(BOARD_DIMENSION - (thisRow + 1), thisCol), true);
                        HorizontalWall rightWall = findHwall(thisRow, thisCol + 1);
                        wall.setFill(Color.BLACK);
                        rightWall.setFill(Color.BLACK);
                        wall.setPressCommit(true);
                        updateInfoPanel();
                    }
                }));

            }
        }
        root.getChildren().addAll(tileGroup, labelGroup , pawnGroup, horizontalWallGroup, verticalWallGroup, generateInfoPanel());
        return root;
    }


    public Pane generateInfoPanel() {
        Pane panel = new Pane();
        currentTurnLabel.setText(controller.getPlayerName() + "'s turn");
        currentTurnLabel.setTextFill(Color.valueOf(controller.getPlayerColor()));
        currentTurnLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        wallLabel.setText("Walls left: " + controller.getPlayerWallLeft());
        wallLabel.setTextFill(Color.valueOf(controller.getPlayerColor()));
        currentTurnLabel.setTranslateY(10);
        wallLabel.setTranslateY(20);
        panel.setTranslateX(BOARD_DIMENSION*TILE_SIZE +10);
        panel.getChildren().addAll(currentTurnLabel, wallLabel);
        return panel;
    }

    public void updateInfoPanel() {
        currentTurnLabel.setText(controller.getPlayerName() + "'s turn");
        currentTurnLabel.setTextFill(Color.valueOf(controller.getPlayerColor()));
        wallLabel.setText("Walls left: " + controller.getPlayerWallLeft());
        wallLabel.setTextFill(Color.valueOf(controller.getPlayerColor()));
    }


    private VerticalWall findVwall(int row, int col) {
        for(Node node : verticalWallGroup.getChildren()) {
            VerticalWall wall = (VerticalWall) node;
            if (wall.getCol() == col && wall.getRow() == row) {
                return wall;
            }
        }
        return null;
    }

    private HorizontalWall findHwall(int row, int col) {
        for(Node node : horizontalWallGroup.getChildren()) {
            HorizontalWall wall = (HorizontalWall) node;
            if (wall.getCol() == col && wall.getRow() == row) {
                return wall;
            }
        }
        return null;
    }

    public void highlightMoves(List<String> moves) {
        System.out.println(moves);
    }

    public String toAlgebraic(int r, int c) {
        char row = (char) ('1' + r);
        char col = (char) ('a' + c);
        return ""+col+row;
    }


    public static void main(String[] args) {
        launch(args);
    }
}

