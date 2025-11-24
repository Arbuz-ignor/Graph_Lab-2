package structures;
import exceptions.CollectionError;
import java.util.Iterator;
//простой динамический массив
//увеличивает ёмкость в 2 раза при переполнении
public class DynamicArray<T> implements Iterable<T> {
    private T[] data;
    private int size;
    @SuppressWarnings("unchecked")
    public DynamicArray() {
        data = (T[]) new Object[4];
        size = 0;
    }
    public int size() {
        return size;
    }
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new CollectionError("Индекс вышел за диапазон: " + index);
        }
        return data[index];
    }
    public void set(int index, T value) {
        if (index < 0 || index >= size) {
            throw new CollectionError("Индекс вышел за диапазон: " + index);
        }
        data[index] = value;
    }
    public void append(T value) {
        if (size == data.length) resize();
        data[size++] = value;
    }
    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newData = (T[]) new Object[data.length * 2];
        for (int i = 0; i < size; i++) newData[i] = data[i];
        data = newData;
    }
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            int idx = 0;
            public boolean hasNext() { return idx < size; }
            public T next() { return data[idx++]; }
        };
    }
}
