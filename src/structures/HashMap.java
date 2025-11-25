package structures;
import exceptions.CollectionError;
import java.util.Iterator;
import java.util.NoSuchElementException;
//cвоя хеш-таблица с открытой адресацией
public class HashMap<K, V> {
    private static final int INITIAL_CAPACITY = 8;    //начальная ёмкость
    private static final double MAX_LOAD_FACTOR = 0.7;
    //внутренняя ячейка таблицы
    private static class Entry<K, V> {
        K key;
        V value;
        boolean isActive;  // true  запись, false логически удалена

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.isActive = true;
        }
    }
    private int capacity;      //текущая ёмкость массива
    private int size;          //количество активных элементов
    private Entry<K, V>[] data;//массив ячеек

    @SuppressWarnings("unchecked")
    public HashMap() {
        this.capacity = INITIAL_CAPACITY;
        this.size = 0;
        this.data = (Entry<K, V>[]) new Entry[capacity];
    }
    public int size() {
        return size;
    }
    //первичный индекс
    private int probeIndex(K key) {
        int h = (key.hashCode() & 0x7fffffff);
        return h % capacity;
    }
    //не пора ли увеличивать таблицу
    private boolean shouldResize() {
        return size + 1 > (int) (capacity * MAX_LOAD_FACTOR);
    }
    //перестраиваем таблицу
    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Entry<K, V>[] old = data;
        capacity = newCapacity;
        data = (Entry<K, V>[]) new Entry[capacity];
        size = 0;
        for (Entry<K, V> entry : old) {
            if (entry != null && entry.isActive) {
                put(entry.key, entry.value);
            }
        }
    }
    //вставка/обновление значения по ключу
    public void put(K key, V value) {
        if (key == null) {
            throw new CollectionError("Ключ не может быть пустым");
        }
        if (shouldResize()) {
            resize(capacity * 2);
        }

        int index = probeIndex(key);
        int firstDeletedIndex = -1;  //первая удалённая ячейка, куда можно засунуть новый элемент

        while (true) {
            Entry<K, V> entry = data[index];
            if (entry == null) {
                //дошли до пустой ячейк либо используем её, либо первую удалённую
                int targetIndex = (firstDeletedIndex != -1) ? firstDeletedIndex : index;
                data[targetIndex] = new Entry<>(key, value);
                size++;
                return;
            }
            if (!entry.isActive && firstDeletedIndex == -1) {
                firstDeletedIndex = index;
            } else if (entry.isActive && entry.key.equals(key)) {
                //просто обновляем значение
                entry.value = value;
                return;
            }
            index = (index + 1) % capacity;
        }
    }
    //получить значение по ключу
    public V get(K key) {
        int idx = findIndex(key);
        if (idx == -1) {
            throw new CollectionError("Ключ '" + key + "' не найден");
        }
        return data[idx].value;
    }

    //вернуть значение или null
    public V tryGet(K key) {
        int idx = findIndex(key);
        if (idx == -1) {
            return null;
        }
        return data[idx].value;
    }

    //найти индекс ячейки с лючом
    private int findIndex(K key) {
        if (key == null) {
            return -1;
        }
        int index = probeIndex(key);
        int steps = 0;
        while (steps < capacity) {
            Entry<K, V> entry = data[index];
            if (entry == null) {
                //дошли до пустой ячейки
                return -1;
            }
            if (entry.isActive && entry.key.equals(key)) {
                return index;
            }
            index = (index + 1) % capacity;
            steps++;
        }
        return -1;
    }

    //логическое удаление по ключу
    public boolean remove(K key) {
        int idx = findIndex(key);
        if (idx == -1) {
            return false;
        }
        Entry<K, V> entry = data[idx];
        entry.isActive = false;
        size--;
        return true;
    }

    public boolean containsKey(K key) {
        return findIndex(key) != -1;
    }

    //итератор по ключам
    public Iterable<K> keys() {
        return () -> new Iterator<K>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                while (index < capacity) {
                    Entry<K, V> entry = data[index];
                    if (entry != null && entry.isActive) {
                        return true;
                    }
                    index++;
                }
                return false;
            }

            @Override
            public K next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Ошибка");
                }
                return data[index++].key;
            }
        };
    }

    //итератор по значениям
    public Iterable<V> values() {
        return () -> new Iterator<V>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                while (index < capacity) {
                    Entry<K, V> entry = data[index];
                    if (entry != null && entry.isActive) {
                        return true;
                    }
                    index++;
                }
                return false;
            }

            @Override
            public V next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Нет значений");
                }
                return data[index++].value;
            }
        };
    }
}
