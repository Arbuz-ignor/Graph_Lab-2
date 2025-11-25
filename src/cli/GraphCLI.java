package cli;
import graph.Edge;
import exceptions.GraphError;
import graph.Graph;
import graph.GraphIO;
import structures.HashMap;
import structures.DynamicArray;
import java.util.Scanner;

public class GraphCLI {

    public interface Action {
        void run();
    }

    private final Scanner scanner;
    private Graph<String> graph;
    private final HashMap<Integer, Action> actions;

    public GraphCLI() {
        this.scanner = new Scanner(System.in);
        this.graph = chooseGraphType();        //выбираем 1 или 2
        this.actions = new HashMap<>();
        initActions();
    }


    private void initActions() {
        actions.put(1, this::actionAddVertex);
        actions.put(2, this::actionAddEdge);
        actions.put(3, this::actionRemoveVertex);
        actions.put(4, this::actionRemoveEdge);
        actions.put(5, this::actionDFS);
        actions.put(6, this::actionBFS);
        actions.put(7, this::actionShortestPath);
        actions.put(8, this::actionShowAdjacent);
        actions.put(9, this::actionShowAllEdges);
        actions.put(10, this::actionSaveToFile);
        actions.put(11, this::actionLoadFromFile);
        actions.put(0, this::actionExit);

    }

    public void run() {
        System.out.println("Меню:");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Введите номер команды: ");

            String line = scanner.nextLine().trim();
            int selection;

            try {
                selection = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно ввести целое число");
                continue;
            }

            Action action = actions.tryGet(selection);
            if (action == null) {
                System.out.println("Неизвестная команда");
                continue;
            }

            try {
                action.run();
                if (selection == 0) running = false;
            } catch (GraphError e) {
                System.out.println("Ошибка графа " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Неожиданная ошибка " + e.getMessage());
            }
            if (running) { //чтобы при выходе сразу не ждать Enter
                System.out.println();
                System.out.println("Нажмите Enter, чтобы вернуться в меню");
                scanner.nextLine();    //ждём, пока пользователь просто нажмёт Enter
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("1. Добавить вершину");
        System.out.println("2. Добавить ребро");
        System.out.println("3. Удалить вершину");
        System.out.println("4. Удалить ребро");
        System.out.println("5. DFS (обход в глубину)");
        System.out.println("6. BFS (обход в ширину)");
        System.out.println("7. Кратчайший путь (Дейкстра)");
        System.out.println("8. Показать смежные вершины");
        System.out.println("9. Показать все рёбра");
        System.out.println("10. Сохранить граф");
        System.out.println("11. Загрузить граф");
        System.out.println("0. Выход");

    }

    private void actionAddVertex() {
        System.out.print("Введите имя вершины: ");
        String v = scanner.nextLine().trim();
        if (v.isEmpty()) {
            throw new GraphError("Название вершины не может быть пустым");
        }
        graph.addVertex(v);
        System.out.println("Вершина добавлена");
    }

    private void actionAddEdge() {
        System.out.print("Из вершины: ");
        String from = scanner.nextLine().trim();

        System.out.print("В вершину: ");
        String to = scanner.nextLine().trim();

        System.out.print("Вес: ");
        String w = scanner.nextLine().trim();

        int weight;
        try {
            weight = Integer.parseInt(w);
        } catch (NumberFormatException e) {
            throw new GraphError("Вес ребра должен быть целым числом");
        }

        graph.addEdge(from, to, weight);
        System.out.println("Ребро добавлено");


    }
    private void actionRemoveVertex() {
        System.out.print("Введите имя вершины: ");
        String v = scanner.nextLine().trim();
        graph.removeVertex(v);
        System.out.println("Вершина удалена");
    }
    private void actionRemoveEdge() {
        System.out.print("Из вершины: ");
        String from = scanner.nextLine().trim();
        System.out.print("В вершину: ");
        String to = scanner.nextLine().trim();
        graph.removeEdge(from, to);
        System.out.println("Ребро удалено");
    }
    private void actionDFS() {
        System.out.print("Старт: ");
        String start = scanner.nextLine().trim();
        System.out.print("DFS порядок: ");
        for (String v : graph.dfsOrder(start)) {
            System.out.print(v + " ");
        }
        System.out.println();
    }

    private void actionBFS() {
        System.out.print("Старт: ");
        String start = scanner.nextLine().trim();
        System.out.print("BFS порядок: ");
        for (String v : graph.bfsOrder(start)) {
            System.out.print(v + " ");
        }
        System.out.println();
    }
    private void actionShortestPath() {
        System.out.print("Старт: ");
        String start = scanner.nextLine().trim();
        if (start.isEmpty()) {
            throw new GraphError("Название вершины не может быть пустым");
        }
        System.out.print("Конец: ");
        String end = scanner.nextLine().trim();
        if (end.isEmpty()) {
            throw new GraphError("Название вершины не может быть пустым");
        }
        DynamicArray<String> path = graph.shortestPath(start, end);

        if (path.size() == 0) {
            System.out.println("Пути от '" + start + "' до '" + end + "' не существует");
            return;
        }

        System.out.print("Путь: ");
        for (String v : path) {
            System.out.print(v + " ");
        }
        System.out.println();

        int dist = graph.pathWeight(path);
        System.out.println("Общая длина пути (сумма весов): " + dist);
    }
    private void actionSaveToFile() {
        System.out.print("Файл: ");
        String file = scanner.nextLine().trim();
        GraphIO.saveToFile(graph, file);
        System.out.println("Граф сохранён");
    }
    private void actionLoadFromFile() {
        System.out.print("Файл: ");
        String file = scanner.nextLine().trim();
        graph = GraphIO.loadFromFile(d -> new Graph<>(d), file, s -> s);
        System.out.println("Граф загружен");
    }
    private void actionExit() {
        System.out.println("Выход");
    }
    private void actionShowAdjacent() {
        System.out.print("Введите вершину: ");
        String v = scanner.nextLine().trim();

        //получаем список смежных вершин
        System.out.print("Смежные вершины: ");
        for (String x : graph.getAdjacent(v)) {
            System.out.print(x + " ");
        }
        System.out.println();
    }
    private void actionShowAllEdges() {
        System.out.println("Все рёбра (from -> to, вес):");
        for (String from : graph.vertices()) {
            for (Edge<String> e : graph.edgesFrom(from)) {
                System.out.println(from + " -> " + e.getTo() + " (вес " + e.getWeight() + ")");
            }
        }
    }
    private Graph<String> chooseGraphType() {
        System.out.println("Выберите тип графа:");
        System.out.println("1. Ориентированный");
        System.out.println("2. Неориентированный");

        while (true) {
            System.out.print("Ваш выбор: ");
            String input = scanner.nextLine().trim();

            if (input.equals("1")) {
                System.out.println("Создан ориентированный граф");
                return new Graph<>(true);
            }
            if (input.equals("2")) {
                System.out.println("Создан неориентированный граф");
                return new Graph<>(false);
            }

            System.out.println("Ошибка");
        }
    }


}
