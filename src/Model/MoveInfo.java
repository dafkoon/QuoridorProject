package Model;

/**
 * Represents a move taken during the game.
 */
public class MoveInfo {
    private final int playerNum;
    private final String move;
    private final int moveNum;
    private static int moveCounter = 1;

    public MoveInfo(int playerID, String move) {
        this.playerNum = playerID;
        this.move = move;
        this.moveNum = moveCounter;
        moveCounter++;
    }

    public int getPlayerNum() {
        return playerNum;
    }
    public String getMove() {
        return move;
    }
    public int getMoveNum() {
        return moveNum;
    }
}
