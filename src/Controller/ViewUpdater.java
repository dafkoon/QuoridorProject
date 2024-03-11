//package Controller;
//
//import Model.Gamestate.Player;
//import Model.Gamestate.Validator;
//
//public class ViewUpdater {
//    private Validator validator;
//    private Player[] players;
//    public ViewUpdater() {
//        validator = Validator.getInstance();
//        System.out.println(validator);
//        players = validator.getPlayers();
//    }
//
//    public String getCurrentPlayerName() {
//        int turn = this.validator.getTurn();
//        Player player = this.validator.getPlayer(turn);
//        return player.getName();
//    }
//
//    public String getCurrentPlayerColor() {
//        int turn = this.validator.getTurn();
//        Player player = this.validator.getPlayer(turn);
//        return player.getColor();
//    }
//
//    public int getCurrentPlayerWallsLeft() {
//        int turn = this.validator.getTurn();
//        Player player = this.validator.getPlayer(turn);
//        return player.getWallsLeft();
//
//    }
//
//    public String getPlayerName(int id) {
//        Player player = this.validator.getPlayer(id);
//        return player.getName();
//    }
//
//    public int getPlayerWallsLeft(int id) {
//        Player player = this.validator.getPlayer(id);
//        return player.getWallsLeft();
//    }
//
//    public String getPlayerColor(int id) {
//        Player player = this.validator.getPlayer(id);
//        return player.getColor();
//    }
//
//}
