import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;  // Import the Scanner class


// Step 1: Define Observable (Subject)
class Model {
    private List<Observer> observers = new ArrayList<>();
    private int data;

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void setData(int newData) {
        this.data = newData;
        notifyObservers();
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(data);
        }
    }
}

// Step 2: Define Observer Interface
interface Observer {
    void update(int data);
}

// Step 3: Implement Observable (Subject)
// Step 4: Implement Observer(s)
class View implements Observer {
    @Override
    public void update(int data) {
        System.out.println("View updated with new data: " + data);
    }
}

public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        View view = new View();

        // Register the view as an observer
        model.addObserver(view);
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter number:");

        int data = myObj.nextInt();  // Read user input
        // Change data in the model
        model.setData(data); // This should trigger an update in the view
    }
}