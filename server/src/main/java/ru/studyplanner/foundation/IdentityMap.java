package ru.studyplanner.foundation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class IdentityMap<K, V> {

    private final Map<K, V> values = new LinkedHashMap<>();

    public Optional<V> find(K key) {
        return Optional.ofNullable(values.get(key));
    }

    public V getOrPut(K key, Function<K, V> factory) {
        return values.computeIfAbsent(key, factory);
    }

    public Collection<V> values() {
        return values.values();
    }

    public int size() {
        return values.size();
    }
}
