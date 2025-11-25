package structures;
import exceptions.CollectionError;
public class Stack<T> {
    private final DynamicArray<T> data = new DynamicArray<>();

    public boolean isEmpty() { return data.size() == 0; }

    public void push(T value) { data.append(value); }

    public T pop() {
        if (isEmpty()) throw new CollectionError("Стек пуст");
        return data.removeAt(data.size() - 1); //забираем последний добавленный
    }

    public T peek() {
        if (isEmpty()) return null;
        return data.get(data.size() - 1);
    }
}