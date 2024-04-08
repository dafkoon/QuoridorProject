package View;

import Controller.GameHandler;
import View.pieces.InfoPane;
import View.pieces.Pawn;
import View.pieces.Pawn.PawnColor;
import View.pieces.Pawn.PawnType;
import View.pieces.Tile;
import View.pieces.Walls.HorizontalWall;
import View.pieces.Walls.VerticalWall;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static Utilities.Constants.*;


public class GUI extends Application{
    public static int startingPlayer;
    private final Group tileGroup = new Group();
    private final Group pawnGroup = new Group();
    private final Group horizontalWallGroup = new Group();
    private final Group verticalWallGroup = new Group();
    private InfoPane infoPane;
    private Pawn[] pawnList;
    private GameHandler gameHandler;

    /**
     * Initializes and starts the Quoridor game.
     *
     * @param primaryStage the primary stage for displaying the game GUI
     */
    public void start(Stage primaryStage) {
        startingPlayer = 1; // 0 - Human starts     1 - AI starts
        createPawns();

        gameHandler = new GameHandler(this, startingPlayer);
        gameHandler.initPlayers(pawnList);

        Pane root = populateBoard();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Quoridor");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        gameHandler.startGame();
    }

    /**
     * Initializes the pawns for the Quoridor game.
     * The method sets up the pawns for both players, including their types, colors, and initial positions.
     * Additionally, it registers mouse events for human pawns and adds players and opponents to the input handler.
     */
    public void createPawns() {
        int currentPlayer = startingPlayer;     // Determine the starting player
        // Set initial positions for pawns
        int[] xPixel = new int[]{BOARD_SIZE/2-TILE_SIZE/2, BOARD_SIZE/2-TILE_SIZE/2};
        int[] yPixel = new int[]{0, BOARD_SIZE-TILE_SIZE};
        pawnList = new Pawn[xPixel.length];
        for(int i = 0; i < pawnList.length; i++) {
            // Determine pawn type and color based on the current player
            PawnType pawnType = Pawn.intToType(currentPlayer);
            PawnColor pawnColor = Pawn.intToColor(currentPlayer);
            Pawn pawn = new Pawn(pawnType, pawnColor, xPixel[currentPlayer], yPixel[currentPlayer]);

            // Register mouse events for human pawns
            if(pawn.getType() == PawnType.HUMAN)
                pawnMouseEvents(pawn);
            // Add the pawn to the pawn group for display
            pawnGroup.getChildren().add(pawn);
            // Store the pawn in the pawn list
            pawnList[currentPlayer] = pawn;
            // Switch to the next player
            currentPlayer = (currentPlayer + 1) % 2;
        }
    }

    /**
     * Registers mouse events for a pawn, allowing human input handling for pawn movements.
     * @param pawn the pawn for which mouse events are registered
     */
    public void pawnMouseEvents(Pawn pawn) {
        pawn.setOnMousePressed(pawn::setOnMousePressed);
        pawn.setOnMouseDragged(pawn::setOnMouseDragged);
        pawn.setOnMouseReleased(event -> gameHandler.pawnReleased(pawn));
    }

    /**
     * Registers mouse events for a horizontal wall, allowing human input handling for wall movements.
     * @param wall the horizontal wall for which mouse events are registered
     */
    public void horizontalWallMouseEvents(HorizontalWall wall) {
        wall.setOnMouseEntered(event -> gameHandler.handleHorizontalWallMovement(event, wall));
        wall.setOnMousePressed(event -> gameHandler.handleHorizontalWallMovement(event, wall));
        wall.setOnMouseExited(event -> gameHandler.handleHorizontalWallMovement(event, wall));
    }

    /**
     * Registers mouse events for a vertical wall, allowing human input handling for wall movements.
     * @param wall the vertical wall for which mouse events are registered
     */
    public void verticalWallMouseEvents(VerticalWall wall) {
        wall.setOnMouseEntered(event -> gameHandler.handleVerticalWallMovement(event, wall));
        wall.setOnMousePressed(event -> gameHandler.handleVerticalWallMovement(event, wall));
        wall.setOnMouseExited(event -> gameHandler.handleVerticalWallMovement(event, wall));
    }

    /**
     * Populates the game board with tiles, vertical walls, and horizontal walls.
     * Additionally, registers mouse events for wall movements.
     * @return the pane containing the populated game board
     */
    private Pane populateBoard() {
        // Create a new pane to contain the game board
        Pane root = new Pane();
        root.setPrefSize((BOARD_DIMENSION * TILE_SIZE) + 120, BOARD_DIMENSION * TILE_SIZE);

        // Populate the board with tiles
        for (int row = 1; row <= BOARD_DIMENSION; row++) {
            for (int col = 1; col <= BOARD_DIMENSION; col++) {
                Tile tile = new Tile(row, col);
                tileGroup.getChildren().add(tile);
            }
        }

        // Add vertical walls to the board and register mouse events for wall movements
        for (int row = 1; row <= BOARD_DIMENSION; row++) {
            for (int col = 1; col < BOARD_DIMENSION; col++) {
                VerticalWall wall = new VerticalWall(row, col);
                verticalWallGroup.getChildren().add(wall);
                verticalWallMouseEvents(wall);
            }
        }

        // Add horizontal walls to the board and register mouse events for wall movements
        for (int row = 1; row < BOARD_DIMENSION; row++) {
            for (int col = 1; col <= BOARD_DIMENSION; col++) {
                HorizontalWall wall = new HorizontalWall(row, col);
                horizontalWallGroup.getChildren().add(wall);
                horizontalWallMouseEvents(wall);
            }
        }

        // Populate the info panel and add it to the root pane
        infoPane = populateInfoPanel();
        root.getChildren().addAll(tileGroup, pawnGroup, horizontalWallGroup, verticalWallGroup, infoPane);
        return root;
    }

    /**
     * Populates the info panel with information about each player's name, remaining walls, and pawn color.
     * @return the populated info panel
     */
    public InfoPane populateInfoPanel() {
        InfoPane panel = new InfoPane();

        // Add information about each pawn to the info panel
        for (Pawn pawn : pawnList) {
            int id = pawn.getType().ordinal();
            panel.addInfo(pawn.getType().name(), gameHandler.getPlayerWallsLeft(id), pawn.getColor().name());
        }
        // Set the position of the info panel relative to the game board
        panel.setTranslateX(BOARD_DIMENSION * TILE_SIZE + 10);
        return panel;
    }

    /**
     * Updates the information displayed in the info panel based on the current player's turn.
     * @param playerTurn the index of the current player in the pawn list
     */
    public void updateInfoPanel(int playerTurn) {
        // Iterate through each pawn in the pawn list
        for (Pawn pawn : pawnList) {
            int id = pawn.getType().ordinal();
            // Check if the pawn corresponds to the current player's turn
            if (id == playerTurn) {
                // Update the information displayed in the info panel for the current player
                infoPane.updateInfo(id, pawn.getType().name(), gameHandler.getPlayerWallsLeft(id), pawn.getColor().name());
            }
        }
    }

    /**
     * Finds and returns the vertical wall located at the specified row and column on the game board.
     * @param row the row of the vertical wall
     * @param col the column of the vertical wall
     * @return the vertical wall if found, or {@code null} if not found
     */
    public VerticalWall findVerticalWall(int row, int col) {
        // Iterate through each node in the vertical wall group
        for (Node node : verticalWallGroup.getChildren()) {
            VerticalWall wall = (VerticalWall) node;
            // Check if the wall's row and column match the specified row and column
            if (wall.getCol() == col && wall.getRow() == row) {
                return wall;
            }
        }
        return null;
    }

    /**
     * Finds and returns the horizontal wall located at the specified row and column on the game board.
     * @param row the row of the horizontal wall
     * @param col the column of the horizontal wall
     * @return the horizontal wall if found, or {@code null} if not found
     */
    public HorizontalWall findHorizontalWall(int row, int col) {
        // Iterate through each node in the horizontal wall group
        for (Node node : horizontalWallGroup.getChildren()) {
            // Cast the node to HorizontalWall
            HorizontalWall wall = (HorizontalWall) node;
            if (wall.getCol() == col && wall.getRow() == row) {
                return wall;
            }
        }
        return null;
    }

    /**
     * Updates the location of the pawn with the specified ID on the game board.
     * If the provided xPixel and yPixel values are not -1, the pawn's position is updated.
     * Otherwise, the pawn is reversed (moved back to its previous position).
     * Additionally, the information panel is updated with the latest information about the pawn.
     *
     * @param id the ID of the pawn to update
     * @param xPixel the x-coordinate of the new position, or -1 to reverse the pawn
     * @param yPixel the y-coordinate of the new position, or -1 to reverse the pawn
     */
    public void updatePawnLocation(int id, double xPixel, double yPixel) {
        // Retrieve the pawn with the specified ID from the pawn list
        Pawn pawn = pawnList[id];

        // Check if the provided xPixel and yPixel values indicate a new position
        if (xPixel != -1 && yPixel != -1) {
            pawn.move(xPixel, yPixel);
            updateInfoPanel(id);
        } else {
            // Reverse the pawn (move it back to its previous position)
            pawn.reverse();
        }
    }

    /**
     * Fills the specified vertical walls with black color and marks them as placed if indicated.
     * @param wall1 the first vertical wall
     * @param wall2 the second vertical wall
     * @param isPressed indicates whether the walls are being placed
     */
    public void fillVerticalWall(VerticalWall wall1, VerticalWall wall2, boolean isPressed) {
        wall1.setFill(Color.BLACK);
        wall2.setFill(Color.BLACK);
        if (isPressed) {
            wall1.setIsPlaced(true);
            wall2.setIsPlaced(true);
        }
    }

    /**
     * Removes the fill color from the specified vertical walls.
     * @param wall1 the first vertical wall
     * @param wall2 the second vertical wall
     */
    public void removeFillVerticalWall(VerticalWall wall1, VerticalWall wall2) {
        wall1.setFill(Color.SILVER);
        wall2.setFill(Color.SILVER);
    }

    /**
     * Fills the specified horizontal walls with black color and marks them as placed if indicated.
     * @param wall1 the first horizontal wall
     * @param wall2 the second horizontal wall
     * @param isPressed indicates whether the walls are being placed
     */
    public void fillHorizontalWall(HorizontalWall wall1, HorizontalWall wall2, boolean isPressed) {
        wall1.setFill(Color.BLACK);
        wall2.setFill(Color.BLACK);
        if (isPressed) {
            wall1.setIsPlaced(true);
            wall2.setIsPlaced(true);
        }
    }

    /**
     * Removes the fill color from the specified horizontal walls.
     * @param wall1 the first horizontal wall
     * @param wall2 the second horizontal wall
     */
    public void removeFillHorizontalWall(HorizontalWall wall1, HorizontalWall wall2) {
        wall1.setFill(Color.SILVER);
        wall2.setFill(Color.SILVER);
    }


    public static void main(String[] args) {
        launch(args);
    }


}

