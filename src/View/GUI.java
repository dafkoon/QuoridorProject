package View;

import Controller.ClientHandler;
import Controller.ViewUpdater;
import View.pieces.InfoPane;
import View.pieces.Pawn;
import View.pieces.Pawn.PawnColor;
import View.pieces.Pawn.PawnType;
import View.pieces.Tile;
import View.pieces.Walls.HorizontalWall;
import View.pieces.Walls.VerticalWall;
import View.pieces.Walls.Wall;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.util.Optional;

import static Utilities.Constants.*;


public class GUI extends Application{
    public static int startingPlayer;
    private final Group tileGroup = new Group();
    private final Group pawnGroup = new Group();
    private final Group horizontalWallGroup = new Group();
    private final Group verticalWallGroup = new Group();
    private InfoPane infoPane;
    private Pawn[] pawnList;
    private ClientHandler clientHandler;

    /**
     * Initializes and starts the Quoridor game.
     * @param primaryStage the primary stage for displaying the game GUI
     */
    public void start(Stage primaryStage) {
        startingPlayer = selectStartingPlayer(); // 0 - Human starts     1 - AI starts
        createPawns();

        ViewUpdater viewUpdater = new ViewUpdater(this); // Create the class that the controller uses to update the view.
        clientHandler = new ClientHandler(viewUpdater, startingPlayer); // Create the class that handles events from the GUI
        clientHandler.initPlayers(pawnList);

        Pane root = populateBoard();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Quoridor");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        clientHandler.startGame();
    }

    private int selectStartingPlayer() {
        String[] players = {"Human", "AI"};
        ChoiceDialog<String> d = new ChoiceDialog<>(players[0], players);
        d.setHeaderText("Who should start the game?");
        d.setContentText("Choose a player:");

        // Show the dialog and wait for the user's choice
        Optional<String> result = d.showAndWait();

        // Check if the user made a choice
        if (result.isPresent()) {
            String choice = result.get();
            return choice.equals("Human") ? 0 : 1;
        } else {
            Platform.exit();
            return -1;
        }
    }

    /**
     * Initializes the pawns for the Quoridor game.
     * The method sets up the pawns for both players, including their types, colors, and initial positions.
     * Additionally, it registers mouse events for human pawns and adds players and opponents to the input handler.
     */
    private void createPawns() {
        int currentPlayer = startingPlayer;     // Start from the starting player

        // Set initial positions for pawns
        int[] xPixel = new int[]{BOARD_SIZE/2-TILE_SIZE/2, BOARD_SIZE/2-TILE_SIZE/2};
        int[] yPixel = new int[]{0, BOARD_SIZE-TILE_SIZE};
        pawnList = new Pawn[xPixel.length]; // initialize the pawn list by how many positions are there.
        for(int i = 0; i < pawnList.length; i++) {
            // Determine pawn type and color based on the current player
            PawnType pawnType = Pawn.intToType(currentPlayer);
            PawnColor pawnColor = Pawn.intToColor(currentPlayer);
            Pawn pawn = new Pawn(pawnType, pawnColor, xPixel[currentPlayer], yPixel[currentPlayer]);

            // Register mouse events for human pawns
            if(pawn.getType() == PawnType.HUMAN)
                pawnMouseEvents(pawn);
            pawnGroup.getChildren().add(pawn);
            pawnList[currentPlayer] = pawn;
            currentPlayer = (currentPlayer + 1) % 2;
        }
    }

    /**
     * Registers mouse events for a pawn, allowing human input handling for pawn movements.
     * @param pawn the pawn for which mouse events are registered
     */
    private void pawnMouseEvents(Pawn pawn) {
        pawn.setOnMousePressed(event -> {
            pawn.mousePressed(event);
            clientHandler.showReachableTiles();
        });
//        pawn.setOnMousePressed(pawn::mousePressed);
        pawn.setOnMouseDragged(pawn::mouseDragged);
        pawn.setOnMouseReleased(event ->  {
            clientHandler.hideReachableTiles();
            clientHandler.mouseReleasedPawn(pawn);
        });
    }

    /**
     * Registers mouse events for a wall, allowing human input handling for wall movements.
     * @param wall the wall for which mouse events are registered
     */
    private void registerWallMouseEvents(Wall wall) {
        wall.setOnMouseEntered(event -> clientHandler.wallEvents(event, wall));
        wall.setOnMousePressed(event -> clientHandler.wallEvents(event, wall));
        wall.setOnMouseExited(event -> clientHandler.wallEvents(event, wall));
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

        // Add tiles to the board.
        for (int row = 1; row <= BOARD_DIMENSION; row++) {
            for (int col = 1; col <= BOARD_DIMENSION; col++) {
                Tile tile = new Tile(row, col);
                tileGroup.getChildren().add(tile);
            }
        }

        // Add vertical walls to the board and register mouse events for wall events
        for (int row = 1; row <= BOARD_DIMENSION; row++) {
            for (int col = 1; col < BOARD_DIMENSION; col++) {
                VerticalWall wall = new VerticalWall(row, col);
                verticalWallGroup.getChildren().add(wall);
                registerWallMouseEvents(wall);
            }
        }

        // Add horizontal walls to the board and register mouse events for wall events
        for (int row = 1; row < BOARD_DIMENSION; row++) {
            for (int col = 1; col <= BOARD_DIMENSION; col++) {
                HorizontalWall wall = new HorizontalWall(row, col);
                horizontalWallGroup.getChildren().add(wall);
                registerWallMouseEvents(wall);
            }
        }

        // Populate the info panel and add it to the root pane
        infoPane = populateInfoPanel();
        root.getChildren().addAll(tileGroup, pawnGroup, horizontalWallGroup, verticalWallGroup, infoPane);
        return root;
    }

    /**
     * Creates the info panel, adds  information about each player's name, remaining walls, and pawn color.
     * @return the populated info panel
     */
    private InfoPane populateInfoPanel() {
        InfoPane panel = new InfoPane();

        // Add information about each pawn to the info panel
        for (Pawn pawn : pawnList) {
            int id = pawn.getType().ordinal();
            panel.addInfo(pawn.getType().name(), clientHandler.getPlayerWallsLeft(id), pawn.getColor().name());

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
                infoPane.updateInfo(id, pawn.getType().name(), clientHandler.getPlayerWallsLeft(id), pawn.getColor().name());
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

    public Tile findTile(String stringTile) {
        for (Node node : tileGroup.getChildren()) {
            Tile tile = (Tile) node;
            // Check if the wall's row and column match the specified row and column
            if(tile.toString().equals(stringTile)) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Updates the location of the pawn with the specified ID on the game board.
     * If the provided xPixel and yPixel values are not -1, the pawn's position is updated.
     * Otherwise, the pawn is reversed (moved back to its last position before being dragged).
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
            // Reverse the pawn (move it back to its last position)
            pawn.reverse();
        }
    }

    /**
     * Fills the specified walls with as placed if indicated.
     * @param wall1 the first wall
     * @param wall2 the second wall
     * @param isPressed indicates whether the walls are being placed
     */
    public void fillWall(Wall wall1, Wall wall2, boolean isPressed) {
        wall1.fill();
        wall2.fill();
        if (isPressed) {
            wall1.setIsPlaced(true);
            wall2.setIsPlaced(true);
        }
    }

    /**
     * Removes the fill color from the specified walls.
     * @param wall1 the first wall
     * @param wall2 the second wall
     */
    public void removeFill(Wall wall1, Wall wall2) {
        wall1.removeFill();
        wall2.removeFill();
    }


    public void showWinner(int id) {
        // Create a new stage for the pop-up window
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Ensures that the pop-up window blocks user interaction with other windows

        // Create a label to display the winner's name
        Label winnerLabel = new Label("Congratulations!\nThe winner is: " + pawnList[id]);
        winnerLabel.setTextFill(Color.valueOf(Pawn.intToColor(id).name()));
        winnerLabel.setStyle("-fx-font-size: 18pt; -fx-font-weight: bold;");

        // Create a VBox to hold the label
        VBox vbox = new VBox(winnerLabel);
        vbox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20px; -fx-spacing: 10px;");

        // Create a scene with the VBox
        Scene scene = new Scene(vbox);

        // Set the scene on the stage
        popupStage.setScene(scene);
        popupStage.setTitle("Winner!");
        popupStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }


}

