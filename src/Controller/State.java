package Controller;

public abstract class State {
    static State start, travelShortestPath, calculatePathDifferences, findBestWall, current;

    void enter() {}
    void update() {}
}
class Start extends State {
    void enter() {}
    void update() {}
}

class TravelShortestPath extends State{

    void enter() {}
    void update() {}
}
class CalculatePathDifferences extends State{
    void enter() {}
    void update() {}
}
class findBestWall extends State{
    void enter() {}
    void update() {}
}
