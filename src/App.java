import cli.GraphCLI;

public class App {
    public static void main(String[] args) {
        System.out.println("Запуск приложения");
        GraphCLI cli = new GraphCLI();
        cli.run();
    }
}