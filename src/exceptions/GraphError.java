package exceptions;
public class GraphError extends RuntimeException {
    public GraphError(String msg) {
        super(msg);
    }
    public GraphError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
