import java.util.HashMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class Main {

    static Scanner myObj = new Scanner(System.in);
    static HashMap<String, Method> automate;

    public static void main(String args[]) {
        automate = new HashMap<String, Method>();
        try {
            automate.put("start", Main.class.getMethod("fun1",String.class));
            automate.put("move", Main.class.getMethod("fun2",String.class));
            automate.put("check", Main.class.getMethod("fun3",String.class));
            automate.put("end", Main.class.getMethod("fun4",String.class));
        }
        catch (NoSuchMethodException | SecurityException e1) {}
        Game();
    }

    private static void Game()
    {
        String operation = myObj.nextLine();
        while ( !operation.equals("quit") )
            try {
                automate.get(operation).invoke(new Main(), operation);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}

    }


    public static void fun4(String n)
    {
        System.out.println(n);
    }

    public static  void fun3(String n)
    {
        System.out.println(n);
    }

    public static void fun2(String n)
    {
        System.out.println(n);
    }

    public static void fun1(String n)
    {
        System.out.println(n);
    }

}