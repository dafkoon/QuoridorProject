package View;

import View.pieces.*;
import View.pieces.Pawn.PawnType;
import View.pieces.Pawn.PawnColor;

import Controller.HumanInputHandler;

import static Controller.Controller.TILE_SIZE;
import static Controller.Controller.BOARD_DIMENSION;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Scanner;




public class Game extends Application{
    public static final int BOARD_SIZE = TILE_SIZE * BOARD_DIMENSION;
    public static int startingPlayer;
    private final Group tileGroup = new Group();
    private final Group pawnGroup = new Group();
    private final Group horizontalWallGroup = new Group();
    private final Group verticalWallGroup = new Group();
    private InfoPane infoPane;
    private Pawn[] pawnList;
    private HumanInputHandler humanInputHandler;


    public void start(Stage primaryStage) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Pick who's starting \n0 - Human\t1 - AI");
//        startingPlayer = scanner.nextInt();
        startingPlayer = 0;

        humanInputHandler = new HumanInputHandler(this, startingPlayer);
        initPawns();

        Pane root = createBoard();
        Scene scene = new Scene(root);
        primaryStage.getIcons().add(new Image("zres/icon.png"));
        primaryStage.setTitle("Quoridor");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        if(startingPlayer == PawnType.AI.ordinal())
            humanInputHandler.onHumanMoveCompleted();
    }


    /**
     * Initializes the pawn's location and calls for a method to create the pawn.
     * Calls the setupPawns method.
     */
    public void initPawns() {
        int currentPlayer = startingPlayer;
        int[] xPixel = new int[]{BOARD_SIZE/2-TILE_SIZE/2, BOARD_SIZE/2-TILE_SIZE/2};
        int[] yPixel = new int[]{0, BOARD_SIZE-TILE_SIZE};
        pawnList = new Pawn[xPixel.length];
        for(int i = 0; i < pawnList.length; i++) {
            PawnType pawnType = Pawn.intToType(currentPlayer);
            PawnColor pawnColor = Pawn.intToColor(currentPlayer);
            Pawn pawn = new Pawn(pawnType, pawnColor, xPixel[currentPlayer], yPixel[currentPlayer]);
            humanInputHandler.addPlayer(pawnType.name(), pawnColor.name(), currentPlayer);
            pawnGroup.getChildren().add(pawn);
            pawnList[currentPlayer] = pawn;
            if(pawn.getType() == PawnType.HUMAN) {
                pawnMouseEvents(pawn);
            }
            currentPlayer = (currentPlayer + 1) % 2;
        }
        humanInputHandler.addOpponent(PawnType.AI.ordinal());
//        for(PawnType pawnType : pawnTypes) {
//            PawnColor color = (pawnType == PawnType.AI) ? PawnColor.RED : PawnColor.BLUE;
//            Pawn pawn = new Pawn(pawnType, color, xPixel[currentPlayer], yPixel[currentPlayer]);
//            pawnGroup.getChildren().add(pawn);
//            pawnList[pawnType.ordinal()] = pawn;
//            humanInputHandler.addPlayer(pawnType.name(), color.name(), currentPlayer);
//            currentPlayer = (currentPlayer + 1) % 2;
//            if(pawn.getType() == PawnType.HUMAN) {
//                pawnMouseEvents(pawn);
//            }
//        }
    }
    public void pawnMouseEvents(Pawn pawn) {
        pawn.setOnMousePressed(event -> humanInputHandler.handlePawnMovement(event, pawn));
        pawn.setOnMouseDragged(event -> humanInputHandler.handlePawnMovement(event, pawn));
        pawn.setOnMouseReleased(event -> humanInputHandler.handlePawnMovement(event, pawn));
    }
    public void horizontalWallMouseEvents(HorizontalWall wall) {
//        System.out.print(wall.toAlgebraic() + " ");
        wall.setOnMouseEntered(event -> humanInputHandler.handleHorizontalWallMovement(event, wall));
        wall.setOnMousePressed(event -> humanInputHandler.handleHorizontalWallMovement(event, wall));
        wall.setOnMouseExited(event -> humanInputHandler.handleHorizontalWallMovement(event, wall));
    }
    public void verticalWallMouseEvents(VerticalWall wall) {
        wall.setOnMouseEntered(event -> humanInputHandler.handleVerticalWallMovement(event, wall));
        wall.setOnMousePressed(event -> humanInputHandler.handleVerticalWallMovement(event, wall));
        wall.setOnMouseExited(event -> humanInputHandler.handleVerticalWallMovement(event, wall));
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
        infoPane = generateInfoPanel();
        root.getChildren().addAll(tileGroup, pawnGroup, horizontalWallGroup, verticalWallGroup, infoPane);
        return root;
    }

    public InfoPane generateInfoPanel() {
        InfoPane panel = new InfoPane();
        for(Pawn pawn : pawnList) {
            int id = pawn.getType().ordinal();
            panel.addInfo(humanInputHandler.getPlayerName(id),
                    humanInputHandler.getPlayerWallsLeft(id),
                    humanInputHandler.getPlayerColor(id));
        }
        panel.setTranslateX(BOARD_DIMENSION*TILE_SIZE +10);
        return panel;
    }
    public void updateInfoPanel(int playerTurn) {
        for(Pawn pawn : pawnList) {
            int pawnId = pawn.getType().ordinal();
            if(pawnId == playerTurn) { // each infoText corresponds to a pawn in pawnList
                infoPane.updateInfo(pawnId, humanInputHandler.getPlayerName(playerTurn),
                        humanInputHandler.getPlayerWallsLeft(playerTurn),
                        humanInputHandler.getPlayerColor(playerTurn));
                break;
            }
        }
    }

    public VerticalWall findVerticalWallObject(int row, int col) {
        for(Node node : verticalWallGroup.getChildren()) {
            VerticalWall wall = (VerticalWall) node;
            if (wall.getCol() == col && wall.getRow() == row) {
                return wall;
            }
        }
        return null;
    }
    public HorizontalWall findHorizontalWallObject(int row, int col) {
        for(Node node : horizontalWallGroup.getChildren()) {
            HorizontalWall wall = (HorizontalWall) node;
            if (wall.getCol() == col && wall.getRow() == row) {
                return wall;
            }
        }
        return null;
    }

    public void updatePawnLocation(int id, double xPixel, double yPixel) {
        Pawn pawn = pawnList[id];
        if(xPixel != -1 && yPixel != -1) {
            pawn.move(xPixel, yPixel);
            updateInfoPanel(id);
        }
        else {
            pawn.reverse();
        }
    }

    public void decideWinner(int winnerID) {
        Alert alert = new Alert(AlertType.INFORMATION);

        alert.setTitle("GAME OVER!");
        alert.setHeaderText("WINNER");
        alert.setContentText(pawnList[winnerID].toString());
        alert.showAndWait();
    }
    public void fillVerticalWall(VerticalWall wall1, VerticalWall wall2, boolean isPressed) {
        wall1.setFill(Color.BLACK);
        wall2.setFill(Color.BLACK);
        if(isPressed) {
            wall1.setPressCommit(true);
            wall2.setPressCommit(true);
        }
    }
    public void removeFillVerticalWall(VerticalWall wall1, VerticalWall wall2) {
        wall1.setFill(Color.SILVER);
        wall2.setFill(Color.SILVER);
    }
    public void fillHorizontalWall(HorizontalWall wall1, HorizontalWall wall2, boolean isPressed) {
        wall1.setFill(Color.BLACK);
        wall2.setFill(Color.BLACK);
        if(isPressed) {
            wall1.setPressCommit(true);
            wall2.setPressCommit(true);
        }
    }
    public void removeFillVerticalWall(HorizontalWall wall1, HorizontalWall wall2) {
        wall1.setFill(Color.SILVER);
        wall2.setFill(Color.SILVER);
    }

    public static void main(String[] args) {
        launch(args);
    }


}

