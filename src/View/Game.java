package View;

import java.util.ArrayList;
import java.util.List;

import View.pieces.HorizontalWall;
import View.pieces.Pawn;
import View.pieces.Pawn.PawnType;
import View.pieces.Tile;
import View.pieces.VerticalWall;

import Model.GameSession;
import Model.Player;

<<<<<<< Updated upstream
=======
import Controller.Controller;
import Controller.EventHandler;
import static Controller.Controller.TILE_SIZE;
import static Controller.Controller.BOARD_DIMENSION;

>>>>>>> Stashed changes
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;



public class Game extends Application {
<<<<<<< Updated upstream
    public static final int TILE_SIZE = 50;
    public static final int BOARD_DIMENSION = 9;
=======
    private final Controller controller = new Controller(); // Access to controller class
    private EventHandler eventHandler;
    //private final Tile[][] board = new Tile[BOARD_DIMENSION][BOARD_DIMENSION];   NOT USED.
    //private final HorizontalWall[][] horizontalWalls = new HorizontalWall[BOARD_DIMENSION][BOARD_DIMENSION];
    //private final VerticalWall[][] verticalWalls = new VerticalWall[BOARD_DIMENSION][BOARD_DIMENSION];
>>>>>>> Stashed changes


    private List<Pawn> pawnList = new ArrayList<Pawn>(2);
    private GameSession gameSession;

    private int turnIndex;

    private Tile[][] board;
    private HorizontalWall[][] horizontalWalls;
    private VerticalWall[][] verticalWalls;
    private Group tileGroup = new Group();
    private Group pawnGroup = new Group();
    private Group horizontalWallGroup = new Group();
    private Group verticalWallGroup = new Group();
    private Label currentTurnLabel;
    private Label wallLabel;
    private Scene scene;

    public void start(Stage primaryStage) {
<<<<<<< Updated upstream
        //setupModel();
        // GameSession gameSession, List<Player> players
        setupModel();
=======
        this.eventHandler = controller.startup();
        initPawns();

>>>>>>> Stashed changes
        currentTurnLabel = new Label();
        wallLabel = new Label();
        Scene scene = new Scene(createBoard());
        primaryStage.getIcons().add(new Image("res/icon.png"));
        primaryStage.setTitle("Quoridor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Starts a new game session and adds 2 players (Updates model).
     * Initializes attributes related to dimensions of the board.
     */
    public void setupModel() {
        gameSession = new GameSession();
        gameSession.addPlayer(new Player("RED"));
        gameSession.addPlayer(new Player("BLUE"));

        board = new Tile[BOARD_DIMENSION][BOARD_DIMENSION];
        horizontalWalls = new HorizontalWall[BOARD_DIMENSION][BOARD_DIMENSION];
        verticalWalls = new VerticalWall[BOARD_DIMENSION][BOARD_DIMENSION];
        turnIndex = 0;
        setupPawns();
    }

    /**
     * Sets up the pawns in the game in hardcoded locations.
     */
    public void setupPawns() {
        int currentType = 0;
        int[] colPos = new int[]{BOARD_DIMENSION/2, BOARD_DIMENSION/2};
        int[] rowPos = new int[]{BOARD_DIMENSION-1, 0};
        PawnType[] pawnTypes = PawnType.values();
        for(PawnType pawnType : pawnTypes) {
<<<<<<< Updated upstream
            Pawn pawn = makePawn(pawnType, pawnType.name(), xStartPos[currentType], yStartPos[currentType]);
            pawnList.add(pawn);
=======
            PawnColor color = (pawnType == PawnType.AI) ? PawnColor.RED : PawnColor.BLUE;
            Pawn pawn = new Pawn(pawnType, color, colPos[currentType], rowPos[currentType]);
            pawnGroup.getChildren().add(pawn);
>>>>>>> Stashed changes
            currentType++;
            pawnMouseEvents(pawn);
        }

    }

<<<<<<< Updated upstream
    public Pawn makePawn(PawnType type, String color, int x, int y) {
        Pawn pawn = new Pawn(type, color, x, y);
        pawn.setOnMouseReleased(event -> {
            int newX = pixelToBoard(pawn.getLayoutX());
            int newY = pixelToBoard(pawn.getLayoutY());
            Tile currentTile = new Tile(pixelToBoard(pawn.getOldX()), pixelToBoard(pawn.getOldY()));
            Tile nextTILE = new Tile(newX, newY);

        });
        return pawn;
=======
    public void pawnMouseEvents(Pawn pawn) {
        pawn.setOnMousePressed(event -> eventHandler.handlePawnMovement(event, pawn));
        pawn.setOnMouseDragged(event -> eventHandler.handlePawnMovement(event, pawn));
        pawn.setOnMouseReleased(event -> eventHandler.handlePawnMovement(event, pawn));
>>>>>>> Stashed changes
    }


    private Parent createBoard() {
        Pane root = new Pane();
        root.setPrefSize((BOARD_DIMENSION * TILE_SIZE) + 85, BOARD_DIMENSION * TILE_SIZE);
        //root.getChildren().addAll(tileGroup, pawnGroup, horizontalWallGroup, verticalWallGroup, infoPanel());
        root.getChildren().addAll(tileGroup, pawnGroup, horizontalWallGroup, verticalWallGroup);

        //Add tiles to the board
        for (int y = 0; y < BOARD_DIMENSION; y++) {
            for (int x = 0; x < BOARD_DIMENSION; x++) {
                Tile tile = new Tile(x, y);
                board[x][y] = tile;
                tileGroup.getChildren().add(tile);
            }
        }
<<<<<<< Updated upstream
        pawnGroup.getChildren().addAll(pawnList);
=======
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

        root.getChildren().addAll(tileGroup, labelGroup , horizontalWallGroup, verticalWallGroup, pawnGroup, generateInfoPanel());
>>>>>>> Stashed changes
        return root;
    }

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

