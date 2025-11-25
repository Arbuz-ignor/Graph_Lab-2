package structures;

import exceptions.CollectionError;
import java.util.Iterator;

public class HashMap<K, V> {

    private static class Pair<K, V> {
        K key;
        V value;
        boolean deleted = false;

        Pair(K k, V v) { key = k; value = v; }
    }

    private Pair<K, V>[] table;
    private int size;

    @SuppressWarnings("unchecked")
    public HashMap() {
        table = (Pair<K, V>[]) new Pair[16];
        size = 0;
    }

    private int index(K key) {
        return (key.hashCode() & 0x7fffffff) % table.length;
    }

    public boolean containsKey(K key) {
        int idx = index(key);
        for (int i = 0; i < table.length; i++) {
            int j = (idx + i) % table.length;
            Pair<K, V> p = table[j];
            if (p == null) return false;
            if (!p.deleted && p.key.equals(key)) return true;
        }
        return false;
    }

    public V get(K key) {
        int idx = index(key);
        for (int i = 0; i < table.length; i++) {
            int j = (idx + i) % table.length;
            Pair<K, V> p = table[j];
            if (p == null) return null;
            if (!p.deleted && p.key.equals(key)) return p.value;
        }
        return null;
    }

    public void put(K key, V value) {
        int idx = index(key);

        for (int i = 0; i < table.length; i++) {
            int j = (idx + i) % table.length;
            Pair<K, V> p = table[j];

            if (p == null || p.deleted) {
                table[j] = new Pair<>(key, value);
                size++;
                return;
            }

            if (p.key.equals(key)) {
                p.value = value;
                return;
            }
        }

        throw new CollectionError("Таблица переполнена");
    }

    public Iterable<K> keys() {
        return () -> new Iterator<>() {
            int i = 0;

            public boolean hasNext() {
                while (i < table.length &&
                        (table[i] == null || table[i].deleted)) i++;
                return i < table.length;
            }

            public K next() {
                return table[i++].key;
            }
        };
    }
}
