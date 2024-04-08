//private boolean searchAndPlaceWall(ArrayList<Square> opponentPath, boolean opponentFastLane) {
//        ArrayList<Square> newAgentPath;
//        ArrayList<Square> newOpponentPath;
//        ArrayList<Wall> wallMoves = removeUselessWalls(generateWallMoves(), opponentPath);
//        Wall bestWall = null;
//        int minPathDifference = Integer.MAX_VALUE;
//        int pathDifference;
//        for(Wall wall : wallMoves) {
//        doVirtualMove(wall.toString());
//        newAgentPath = shortestPathToRow(getGraph(), agent.getPos(), agent.getDestRow());
//        newOpponentPath = shortestPathToRow(getGraph(), opponent.getPos(), opponent.getDestRow());
//        pathDifference = newAgentPath.size() - newOpponentPath.size();
//        System.out.println(wall + " " + (!opponentFastLane) + " " +  (newOpponentPath.size() - opponentPath.size() > 1) + " " + (newOpponentPath.size() - opponentPath.size()));
//        if (!opponentFastLane || newOpponentPath.size() - opponentPath.size() > 1) {
//        if (pathDifference < minPathDifference) {
//        minPathDifference = pathDifference;
//        bestWall = wall;
//        }
//        }
//        undoVirtualMove(wall.toString());
//        }
//        if(bestWall == null)
//        return false;
//        makeMove(bestWall.toString());
//        return true;
//        }


//    private void handleQuickPath(ArrayList<Square> opponentPath, ArrayList<Square> quickPath, boolean isOpponentAtStart) {
//        Square prev, next;
//        if(isOpponentAtStart) {
//            int prevIndex = quickPath.size();
//            prev = opponentPath.get(prevIndex);
//            next = opponentPath.get(prevIndex+1);
//        }
//        else {
//            prev = opponentPath.get(0);
//            next = quickPath.get(0);
//        }
//        ArrayList<Wall> blockingWalls = blockCrossing(prev, next);
//        for(Wall wall : blockingWalls) {
//            doVirtualMove(wall.toString());
//        }
//        System.out.println(blockingWalls);
//    }
