package graph;
import exceptions.GraphError;
import structures.DynamicArray;
import structures.HashMap;
import structures.Queue;
import structures.Stack;
import java.util.function.Consumer;
public class Graph<V> {
    private final boolean directed;
    //cписок смежности ключ - вершина, значение - список исходящих ребер
    private final HashMap<V, DynamicArray<Edge<V>>> adj;
    public Graph(boolean directed) {
        this.directed = directed;
        this.adj = new HashMap<>();
    }

    public Graph() {
        this(true); } //по умолчанию ориентированный

    public boolean isDirected() {
        return directed; }

    public void addVertex(V v) {
        if (adj.containsKey(v)) throw new GraphError("Вершина '" + v + "' уже существует");
        adj.put(v, new DynamicArray<>());
    }

    //важно проверить наличие вершины перед любой операцией
    private void ensureVertexExists(V v) {
        if (!adj.containsKey(v)) {
            throw new GraphError("Вершина '" + v + "' не существует в графе");
        }
    }

    public DynamicArray<Edge<V>> edgesFrom(V v) {
        ensureVertexExists(v);
        return adj.get(v);
    }

    public Iterable<V> vertices() {
        return adj.keys(); }

    public void addEdge(V from, V to, int weight)
    {
        if (weight < 0) throw new GraphError("Вес не может быть отрицательным");
        //автоматически создаем вершины, если их нет
        if (!adj.containsKey(from)) addVertex(from);
        if (!adj.containsKey(to)) addVertex(to);

        //проверка дубликатов
        for (Edge<V> e : adj.get(from))
        {
            if (e.getTo().equals(to)) throw new GraphError("Ребро уже есть");
        }

        adj.get(from).append(new Edge<>(to, weight));

        //если граф неориентированный, добавляем обратное ребро
        if (!directed && !from.equals(to))
        {
            adj.get(to).append(new Edge<>(from, weight));
        }
    }

    public void removeVertex(V v)
    {
        ensureVertexExists(v);
        adj.remove(v); //удаляем саму вершину и исходящие
        //проходим по всем остальным вершинам и удаляем ребра, ведущие в v
        for (V u : vertices())
        {
            DynamicArray<Edge<V>> edges = adj.get(u);
            if (edges == null) continue;
            int i = 0;
            while (i < edges.size()) {
                if (edges.get(i).getTo().equals(v)) {
                    edges.removeAt(i);
                } else {
                    i++;
                }
            }
        }
    }

    // удалить направленное ребро из u в v
    private void removeSingleDirected(V u, V v) {
        DynamicArray<Edge<V>> edges = adj.get(u);
        int idx = 0;
        boolean removed = false;
        while (idx < edges.size()) {
            Edge<V> e = edges.get(idx);
            if (e.getTo().equals(v)) {
                edges.removeAt(idx);
                removed = true;
                break;
            } else {
                idx++;
            }
        }
        if (!removed) {
            throw new GraphError("Ребро '" + u + "' -> '" + v + "' не найдено в графе");
        }
    }

    public void removeEdge(V from, V to) {
        ensureVertexExists(from);
        ensureVertexExists(to);

        removeSingleDirected(from, to);
        if (!directed && !from.equals(to)) {
            removeSingleDirected(to, from);
        }
    }

    //получить список соседей вершины
    public DynamicArray<V> getAdjacent(V v) {
        ensureVertexExists(v);
        DynamicArray<Edge<V>> edges = adj.get(v);
        DynamicArray<V> result = new DynamicArray<>();
        for (Edge<V> e : edges) {
            result.append(e.getTo());
        }
        return result;
    }

    //DFS используем Stack
    private DynamicArray<V> dfsOrder(V start, Consumer<V> visit) {
        ensureVertexExists(start);
        HashMap<V, Boolean> visited = new HashMap<>();
        DynamicArray<V> order = new DynamicArray<>();
        Stack<V> stack = new Stack<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            V v = stack.pop();
            if (visited.containsKey(v)) {
                continue;  // уже были
            }
            visited.put(v, Boolean.TRUE);
            order.append(v);
            if (visit != null) {
                visit.accept(v);
            }
            //добавляем соседей в стек
            //в обратном порядке
            DynamicArray<V> neighbors = getAdjacent(v);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                V u = neighbors.get(i);
                if (!visited.containsKey(u)) {
                    stack.push(u);
                }
            }
        }
        return order;
    }

    //порядок обхода
    public DynamicArray<V> dfsOrder(V start) {
        return dfsOrder(start, null);
    }
    public void dfs(V start) {
        dfsOrder(start, null);
    }


    //BFS используем очередь
    private DynamicArray<V> bfsOrder(V start, Consumer<V> visit) {
        ensureVertexExists(start);
        HashMap<V, Boolean> visited = new HashMap<>();
        DynamicArray<V> order = new DynamicArray<>();
        Queue<V> queue = new Queue<>();
        visited.put(start, Boolean.TRUE);
        queue.enqueue(start);
        while (!queue.isEmpty()) {
            V v = queue.dequeue();
            order.append(v);
            if (visit != null) {
                visit.accept(v);
            }
            DynamicArray<V> neighbors = getAdjacent(v);
            for (int i = 0; i < neighbors.size(); i++) {
                V u = neighbors.get(i);
                if (!visited.containsKey(u)) {
                    visited.put(u, Boolean.TRUE);
                    queue.enqueue(u);
                }
            }
        }
        return order;
    }
    //порядок обхода
    public DynamicArray<V> bfsOrder(V start) {
        return bfsOrder(start, null);
    }

    public void bfs(V start) {
        bfsOrder(start, null);
    }

    //Алгоритм Дейкстра
    public DynamicArray<V> shortestPath(V start, V goal) {
        ensureVertexExists(start);
        ensureVertexExists(goal);
        HashMap<V, Double> dist = new HashMap<>(); //расстояния до вершин
        HashMap<V, V> prev = new HashMap<>();      //предки для восстановления пути
        DynamicArray<V> unvisited = new DynamicArray<>(); //множество непосещённых
        for (V v : vertices()) {
            dist.put(v, Double.POSITIVE_INFINITY);
            prev.put(v, null);
            unvisited.append(v);
        }
        dist.put(start, 0.0);

        // пока есть непосещённые вершины
        while (unvisited.size() > 0) {
            //выбираем вершину с минимальной текущей дистанцией
            int minIndex = 0;
            V minVertex = unvisited.get(0);
            double minDist = dist.get(minVertex);

            for (int i = 1; i < unvisited.size(); i++) {
                V v = unvisited.get(i);
                double d = dist.get(v);
                if (d < minDist) {
                    minDist = d;
                    minVertex = v;
                    minIndex = i;
                }
            }

            V v = minVertex;
            unvisited.removeAt(minIndex);

            //если достигли цели выходим
            if (v.equals(goal) || minDist == Double.POSITIVE_INFINITY) {
                break;
            }
            DynamicArray<Edge<V>> edges = adj.get(v);
            //рассматриваем все рёбра v к u
            for (int i = 0; i < edges.size(); i++) {
                Edge<V> e = edges.get(i);
                V u = e.getTo();
                double alt = dist.get(v) + e.getWeight(); //альтернативный путь через v
                double du = dist.get(u);
                if (alt < du) {
                    dist.put(u, alt);
                    prev.put(u, v);
                }
            }
        }

        double dGoal = dist.get(goal);
        if (Double.isInfinite(dGoal)) {
            throw new GraphError(
                    "Кратчайший путь от '" + start + "' до '" + goal + "' не существует"
            );
        }

        //восстанавливаем путь от goal к start по prev
        DynamicArray<V> path = new DynamicArray<>();
        V cur = goal;
        while (cur != null) {
            path.append(cur);
            cur = prev.tryGet(cur);
        }

        //разворачиваем массив, чтобы путь шёл от start к goal
        int i = 0;
        int j = path.size() - 1;
        while (i < j) {
            V a = path.get(i);
            V b = path.get(j);
            path.set(i, b);
            path.set(j, a);
            i++;
            j--;
        }

        return path;
    }

    //посчитать суммарный вес пути
    public int pathWeight(DynamicArray<V> path) {
        if (path.size() < 2) {
            return 0;
        }

        int total = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            V from = path.get(i);
            V to = path.get(i + 1);

            DynamicArray<Edge<V>> edges = edgesFrom(from);
            boolean found = false;

            for (Edge<V> e : edges) {
                if (e.getTo().equals(to)) {
                    total += e.getWeight();
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new GraphError("Не найдено ребро " +
                        from + " -> " + to + " при подсчёте длины пути");
            }
        }

        return total;
    }

}






