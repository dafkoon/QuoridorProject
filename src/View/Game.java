package View;

import View.pieces.*;
import View.pieces.Pawn.PawnType;
import View.pieces.Pawn.PawnColor;

import Controller.Controller;
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

    private final Controller controller = new Controller(this); // Access to controller class
    private Pawn pawnList[];
    private final Group tileGroup = new Group();
    private final Group pawnGroup = new Group();
    private final Group horizontalWallGroup = new Group();
    private final Group verticalWallGroup = new Group();
    private final Group labelGroup = new Group();
    private Label currentTurnLabel;
    private Label wallLabel;
    private Pane root;


    public void start(Stage primaryStage) {
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
        pawnList = new Pawn[xPixel.length];
        PawnType[] pawnTypes = PawnType.values();
        for(PawnType pawnType : pawnTypes) {
            PawnColor color = (pawnType == PawnType.AI) ? PawnColor.RED : PawnColor.BLUE;
            Pawn pawn = new Pawn(pawnType, color, xPixel[currentType], yPixel[currentType]);
            controller.addPlayer(pawnType.name(), color.name(), currentType);
            pawnGroup.getChildren().add(pawn);
            pawnList[pawnType.ordinal()] = pawn;
            currentType++;
            pawnMouseEvents(pawn);
        }
    }
    public void pawnMouseEvents(Pawn pawn) {
        pawn.setOnMousePressed(event -> controller.handlePawnMovement(event, pawn));
        pawn.setOnMouseDragged(event -> controller.handlePawnMovement(event, pawn));
        pawn.setOnMouseReleased(event -> controller.handlePawnMovement(event, pawn));
    }
    public void horizontalWallMouseEvents(HorizontalWall wall) {
        wall.setOnMouseEntered(event -> controller.handleHorizontalWallMovement(event, wall));
        wall.setOnMousePressed(event -> controller.handleHorizontalWallMovement(event, wall));
        wall.setOnMouseExited(event -> controller.handleHorizontalWallMovement(event, wall));
    }
    public void verticalWallMouseEvents(VerticalWall wall) {
        wall.setOnMouseEntered(event -> controller.handleVerticalWallMovement(event, wall));
        wall.setOnMousePressed(event -> controller.handleVerticalWallMovement(event, wall));
        wall.setOnMouseExited(event -> controller.handleVerticalWallMovement(event, wall));
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
                verticalWallMouseEvents(wall);
            }
        }
        // Add horizontal walls.
        for (int row = BOARD_DIMENSION-1; row > 0; row--) { // from top to bottom
            for (int col = 0; col < BOARD_DIMENSION; col++) { // from left to right
                int thisRow = row;
                int thisCol = col;
                HorizontalWall wall = new HorizontalWall(thisCol, thisRow);
                horizontalWallGroup.getChildren().add(wall);
                horizontalWallMouseEvents(wall);

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
        wallLabel.setText("Walls left: " + controller.getCurrentPlayerWalls());
        wallLabel.setTextFill(Color.valueOf(controller.getCurrentPlayerColor()));
        currentTurnLabel.setTranslateY(10);
        wallLabel.setTranslateY(20);
        panel.setTranslateX(BOARD_DIMENSION*TILE_SIZE +10);
        panel.getChildren().addAll(currentTurnLabel, wallLabel);
        return panel;
    }
    public void updateInfoPanel() {
        currentTurnLabel.setText(controller.getCurrentPlayerName() + "'s turn");
        currentTurnLabel.setTextFill(Color.valueOf(controller.getCurrentPlayerColor()));
        wallLabel.setText("Walls left: " + controller.getCurrentPlayerWalls());
        wallLabel.setTextFill(Color.valueOf(controller.getCurrentPlayerColor()));

    }


    public VerticalWall findVwall(int row, int col) {
        for(Node node : verticalWallGroup.getChildren()) {
            VerticalWall wall = (VerticalWall) node;
            if (wall.getCol() == col && wall.getRow() == row) {
                return wall;
            }
        }
        return null;
    }
    public HorizontalWall findHwall(int row, int col) {
        for(Node node : horizontalWallGroup.getChildren()) {
            HorizontalWall wall = (HorizontalWall) node;
            if (wall.getCol() == col && wall.getRow() == row) {
                return wall;
            }
        }
        return null;
    }

    public void updatePawn(PawnType type, double xPixel, double yPixel) {
        Pawn pawn = null;
        for(int i = 0; i < pawnList.length; i++) {
            if(i == type.ordinal())
                pawn = pawnList[i];
        }
        if(xPixel != -1 && yPixel != -1) {
            pawn.move(xPixel, yPixel);
            updateInfoPanel();
        }
        else {
            pawn.reverse();
        }
    }
    public void updateVertWall(VerticalWall wall1, VerticalWall wall2) {
        wall1.setFill(Color.BLACK);
        wall2.setFill(Color.BLACK);
        wall1.setPressCommit(true);
        updateInfoPanel();
    }
    public void updateHorzWall(HorizontalWall wall1, HorizontalWall wall2) {
        wall1.setFill(Color.BLACK);
        wall2.setFill(Color.BLACK);
        wall1.setPressCommit(true);
        updateInfoPanel();
    }




    public static void main(String[] args) {
        launch(args);
    }
}

