package graph;
import exceptions.GraphError;
import java.io.*;
//класс для сохранения и загрузки графа из текстового файла
public class GraphIO {
    public interface GraphFactory<V> {
        Graph<V> create(boolean directed); }
    public interface VertexParser<V> {
        V parse(String s); }
    public static <V> void saveToFile(Graph<V> graph, String filename) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filename))) {
            out.write(graph.isDirected() ? "directed" : "undirected");
            out.newLine();

            for (V v : graph.vertices()) {
                for (Edge<V> e : graph.edgesFrom(v)) {
                    out.write(v + "\t" + e.getTo() + "\t" + e.getWeight());
                    out.newLine();
                }
            }
        } catch (IOException e) {
            throw new GraphError("Ошибка записи файла: " + e.getMessage(), e);
        }
    }
    public static <V> Graph<V> loadFromFile(
            GraphFactory<V> factory,
            String filename,
            VertexParser<V> parser
    ) {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {

            String header = in.readLine();
            boolean directed = header.equals("directed");

            Graph<V> graph = factory.create(directed);

            String line;
            while ((line = in.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] parts = line.split("\t");
                V from = parser.parse(parts[0]);
                V to = parser.parse(parts[1]);
                int w = Integer.parseInt(parts[2]);
                graph.addEdge(from, to, w);
            }
            return graph;

        } catch (IOException e) {
            throw new GraphError("Ошибка чтения: " + e.getMessage(), e);
        }
    }
}
