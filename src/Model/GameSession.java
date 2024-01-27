package Model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;


/**
 * Represents a game session consisting of 2 players.
 */
public class GameSession {
    public static int MAX_PLAYERS = 2;
    private Board board;
    private List<Player> players;
    private Player winner;

    private Stack<Move> moves;

    public GameSession(Board board) {
        this.board = board;
        this.players = new ArrayList<Player>();
        this.moves = new Stack<Move>();
    }

    public GameSession() {
        this.players = new ArrayList<Player>();
        this.moves = new Stack<Move>();
    }

    public Board getBoard() { return board; }

    /**
     * Adds a player to the session.
     * @param player player to add.
     * @throws IllegalStateException if more players than the limit are added.
     * @throws IllegalArgumentException if player is null.
     */
    public void addPlayer(Player player) throws IllegalStateException, IllegalArgumentException{
        if(players.size() > MAX_PLAYERS) {
            throw new IllegalStateException("Tried to surpass max amount of player: " + MAX_PLAYERS);
        }
        if(player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        players.add(player);
    }

    /**
     * Gets the list of all players in the session.
     * @return players in the session.
     */
    public List<Player> getPlayers() { return players; }


    /**
     * Gets the player with the specified id (turn of the player).
     * @param id the player to get.
     * @return player with the specified id.
     */
    public Player getPlayer(int id) { return players.get(id); }

    /**
     * Adds a move to the moves stack.
     * @param move the move to add.
     * @return true.
     */
    public boolean addMove(Move move) {
        moves.push(move);
        return true;
    }
    /**
     * Gets all previous moves is taken in the session.
     * @return a stack of the moves.
     */
    public Stack<Move> getMoves() { return moves; }


}
