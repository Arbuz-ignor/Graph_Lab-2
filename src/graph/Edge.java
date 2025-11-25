package graph;
public class Edge<V> {
    private final V to;
    private final int weight;
    public Edge(V to, int weight) {
        this.to = to;
        this.weight = weight;
    }
    public V getTo() {
        return to;
    }
    public int getWeight() {
        return weight;
    }
}
