package structures;
import exceptions.CollectionError;
public class Queue<T> {
    private final DynamicArray<T> data = new DynamicArray<>();

    public boolean isEmpty() { return data.size() == 0; }

    //добавить в конец
    public void enqueue(T value) {
        data.append(value);
    }
    public T dequeue() {
        if (isEmpty()) throw new CollectionError("Очередь пуста");
        return data.removeAt(0);
    }

    public T peek() {
        if (isEmpty()) return null;
        return data.get(0);
    }
}