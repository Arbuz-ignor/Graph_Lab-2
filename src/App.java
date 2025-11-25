import cli.GraphCLI;

public class App {
    public static void main(String[] args) {
        System.out.println("Запуск приложения Графы...");
        GraphCLI cli = new GraphCLI();
        cli.run();
    }
}