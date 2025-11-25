package graph;
import exceptions.GraphError;
import structures.DynamicArray;
import structures.HashMap;
public class Graph<V> {
    private final boolean directed;
    //cписок смежности ключ - вершина, значение - список исходящих ребер
    private final HashMap<V, DynamicArray<Edge<V>>> adj;
    public Graph(boolean directed) {
        this.directed = directed;
        this.adj = new HashMap<>();
    }

    public Graph() { this(true); } //по умолчанию ориентированный

    public boolean isDirected() { return directed; }

    public void addVertex(V v) {
        if (adj.containsKey(v)) throw new GraphError("Вершина '" + v + "' уже существует");
        adj.put(v, new DynamicArray<>());
    }

    //важно проверить наличие вершины перед любой операцией
    private void ensureVertexExists(V v) {
        if (!adj.containsKey(v)) throw new GraphError("Вершина '" + v + "' не найдена");
    }

    public Iterable<V> vertices() { return adj.keys(); }
}