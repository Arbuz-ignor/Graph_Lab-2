package structures;
import exceptions.CollectionError;
import java.util.Iterator;
import java.util.NoSuchElementException;
//динамический массив когда место кончается, создаем новый массив в 2 раза больше
public class DynamicArray<T> implements Iterable<T> {
    private int size;
    private int capacity;
    private T[] data;
    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        if (initialCapacity <= 0) throw new CollectionError("Ошибка: отрицательная емкость");
        this.capacity = initialCapacity;
        this.size = 0;
        this.data = (T[]) new Object[initialCapacity]; //java не дает создавать массивы дженериков напрямую(
    }

    public DynamicArray() { this(4); }
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new CollectionError("Индекс " + index + " вне диапазона [0, " + size + ")");
    }

    //копируем всё в новый который в 2 раза больше
    private void resize(int newCapacity) {
        T[] newData = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) newData[i] = data[i];
        data = newData;
        capacity = newCapacity;
    }

    public void append(T value) {
        if (size == capacity) resize(capacity * 2);
        data[size++] = value;
    }

    public T get(int index) {
        checkIndex(index);
        return data[index];
    }

    public void set(int index, T value) {
        checkIndex(index);
        data[index] = value;
    }

    //сдвигаем все элементы справа на одну позицию влево
    public T removeAt(int index) {
        checkIndex(index);
        T value = data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null; //зануляем, чтобы сборщик мог забрать объект
        size--;
        return value;
    }

    public void clear() {
        for (int i = 0; i < size; i++) data[i] = null;
        size = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int i = 0;
            public boolean hasNext() { return i < size; }
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return data[i++];
            }
        };
    }
}