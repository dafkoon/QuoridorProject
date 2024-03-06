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

import java.util.Scanner;

public class Game extends Application {
    public static final int BOARD_SIZE = TILE_SIZE * BOARD_DIMENSION;
    private Controller controller; // Access to controller class
    private final Group tileGroup = new Group();
    private final Group pawnGroup = new Group();
    private final Group horizontalWallGroup = new Group();
    private final Group verticalWallGroup = new Group();
    private Label currentTurnLabel;
    private Label wallLabel;
    private Pane root;
    private Pawn pawnList[];


    public void start(Stage primaryStage) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("0 for HUMAN VS AI      1 for AI VS AI");
//        int gameType = scanner.nextInt();
        controller = new Controller(this);
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
            if(pawn.getType() == PawnType.HUMAN)
                pawnMouseEvents(pawn);
        }
    }
    public void pawnMouseEvents(Pawn pawn) {
        pawn.setOnMousePressed(event -> controller.handlePawnMovement(event, pawn));
        pawn.setOnMouseDragged(event -> controller.handlePawnMovement(event, pawn));
        pawn.setOnMouseReleased(event -> controller.handlePawnMovement(event, pawn));
    }
    public void horizontalWallMouseEvents(HorizontalWall wall) {
//        System.out.print(wall.toAlgebraic() + " ");
        wall.setOnMouseEntered(event -> controller.handleHorizontalWallMovement(event, wall));
        wall.setOnMousePressed(event -> controller.handleHorizontalWallMovement(event, wall));
        wall.setOnMouseExited(event -> controller.handleHorizontalWallMovement(event, wall));
    }
    public void verticalWallMouseEvents(VerticalWall wall) {
//        System.out.print(wall.toAlgebraic() + " ");
        wall.setOnMouseEntered(event -> controller.handleVerticalWallMovement(event, wall));
        wall.setOnMousePressed(event -> controller.handleVerticalWallMovement(event, wall));
        wall.setOnMouseExited(event -> controller.handleVerticalWallMovement(event, wall));
    }
    private Pane createBoard() {
        Pane root = new Pane();
        root.setPrefSize((BOARD_DIMENSION * TILE_SIZE) + 120, BOARD_DIMENSION * TILE_SIZE);

        for(int row = 0; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION; col++) {
                Tile tile = new Tile(row, col);
                tileGroup.getChildren().add(tile);
            }
        }
        for(int row = 0; row < BOARD_DIMENSION; row++) { // 0-8
            for(int col = 0; col < BOARD_DIMENSION-1; col++) { // a-i
                VerticalWall wall = new VerticalWall(row, col);
                verticalWallGroup.getChildren().add(wall);
                verticalWallMouseEvents(wall);
            }
        }
        for(int row = 0; row < BOARD_DIMENSION; row++) {
            for(int col = 0; col < BOARD_DIMENSION-1; col++) {
                HorizontalWall wall = new HorizontalWall(col, row);
                horizontalWallGroup.getChildren().add(wall);
                horizontalWallMouseEvents(wall);
            }
        }
        root.getChildren().addAll(tileGroup, pawnGroup, horizontalWallGroup, verticalWallGroup, generateInfoPanel());
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

    public void updatePawn(int id, double xPixel, double yPixel) {
        Pawn pawn = pawnList[id];
        if(xPixel != -1 && yPixel != -1) {
            pawn.move(xPixel, yPixel);
            updateInfoPanel();
        }
        else {
            pawn.reverse();
        }
    }
    public void updateVertWall(VerticalWall wall1, VerticalWall wall2) {
//        System.out.println("updated walls: " + wall1.toAlgebraic() + " " + wall2.toAlgebraic());
        wall1.setFill(Color.BLACK);
        wall2.setFill(Color.BLACK);
        wall1.setPressCommit(true);
        wall2.setPressCommit(true);
        updateInfoPanel();
    }
    public void updateHorzWall(HorizontalWall wall1, HorizontalWall wall2) {
//        System.out.println("updated walls: " + wall1.toAlgebraic() + " " + wall2.toAlgebraic());
        wall1.setFill(Color.BLACK);
        wall2.setFill(Color.BLACK);
        wall1.setPressCommit(true);
        wall2.setPressCommit(true);
        updateInfoPanel();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

