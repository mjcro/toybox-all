package io.github.mjcro.toybox.swing.widgets;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Insertion-ordered data model for {@link KeyValueJPanel}.
 * Values may be primitives, arrays, collections, or maps with string keys.
 * Maps inside arrays or collections are rejected.
 */
public final class OrderedKeyValueModel {
    private final @NonNull LinkedHashMap<@NonNull String, @Nullable Object> values = new LinkedHashMap<>();
    private final @NonNull EventListenerList listeners = new EventListenerList();

    /**
     * Creates an empty model.
     */
    public OrderedKeyValueModel() {
    }

    /**
     * Creates a model containing a validated copy of the supplied values.
     * Iteration order is retained and later changes to the supplied map have no effect.
     *
     * @param values non-null values to copy.
     * @throws IllegalArgumentException if a nested map has a non-string key, a map occurs inside
     *                                  an array or collection, or the data has a container cycle.
     */
    public OrderedKeyValueModel(@NonNull Map<@NonNull String, ?> values) {
        this.values.putAll(copyMap(
                Objects.requireNonNull(values, "values"),
                false,
                new IdentityHashMap<>()
        ));
    }

    /**
     * Adds or replaces a value while retaining insertion order.
     * Replacing an existing key does not move it.
     *
     * @param key non-null key.
     * @param value nullable primitive, array, collection, or map value.
     * @throws IllegalArgumentException if the value violates the supported data structure.
     */
    public void put(@NonNull String key, @Nullable Object value) {
        final String checkedKey = Objects.requireNonNull(key, "key");
        final @Nullable Object normalized = normalize(value, false, new IdentityHashMap<>());
        if (values.containsKey(checkedKey) && Objects.equals(values.get(checkedKey), normalized)) {
            return;
        }
        values.put(checkedKey, normalized);
        fireChanged();
    }

    /**
     * Adds or replaces all supplied values in their iteration order.
     * The operation validates every value before changing the model.
     *
     * @param newValues non-null values to add.
     * @throws IllegalArgumentException if any value violates the supported data structure.
     */
    public void putAll(@NonNull Map<@NonNull String, ?> newValues) {
        final Map<@NonNull String, @Nullable Object> copy = copyMap(
                Objects.requireNonNull(newValues, "newValues"),
                false,
                new IdentityHashMap<>()
        );
        if (!hasChanges(copy)) {
            return;
        }
        values.putAll(copy);
        fireChanged();
    }

    /**
     * Removes a key if present.
     *
     * @param key non-null key to remove.
     */
    public void remove(@NonNull String key) {
        if (values.containsKey(Objects.requireNonNull(key, "key"))) {
            values.remove(key);
            fireChanged();
        }
    }

    /**
     * Removes every value from the model.
     */
    public void clear() {
        if (!values.isEmpty()) {
            values.clear();
            fireChanged();
        }
    }

    /**
     * Returns an immutable insertion-ordered snapshot of the values.
     * Nested maps and collections are also immutable copies.
     *
     * @return non-null model snapshot.
     */
    public @NonNull Map<@NonNull String, @Nullable Object> asMap() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    /**
     * Returns the number of top-level entries.
     *
     * @return entry count.
     */
    public int size() {
        return values.size();
    }

    /**
     * Reports whether the model has no entries.
     *
     * @return {@code true} when empty.
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * Registers a non-null listener for model changes.
     *
     * @param listener non-null listener to register.
     */
    public void addChangeListener(@NonNull ChangeListener listener) {
        listeners.add(ChangeListener.class, Objects.requireNonNull(listener, "listener"));
    }

    /**
     * Unregisters a non-null model listener.
     *
     * @param listener non-null listener to unregister.
     */
    public void removeChangeListener(@NonNull ChangeListener listener) {
        listeners.remove(ChangeListener.class, Objects.requireNonNull(listener, "listener"));
    }

    private void fireChanged() {
        final ChangeEvent event = new ChangeEvent(this);
        for (final ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
            listener.stateChanged(event);
        }
    }

    private boolean hasChanges(@NonNull Map<@NonNull String, @Nullable Object> newValues) {
        for (final Map.Entry<@NonNull String, @Nullable Object> entry : newValues.entrySet()) {
            if (!values.containsKey(entry.getKey())
                    || !Objects.equals(values.get(entry.getKey()), entry.getValue())) {
                return true;
            }
        }
        return false;
    }

    private static @NonNull Map<@NonNull String, @Nullable Object> copyMap(
            @NonNull Map<?, ?> source,
            boolean insideCollection,
            @NonNull IdentityHashMap<@NonNull Object, @NonNull Boolean> activeContainers
    ) {
        if (insideCollection) {
            throw new IllegalArgumentException("Maps cannot be nested in arrays or collections");
        }
        enterContainer(source, activeContainers);
        final LinkedHashMap<@NonNull String, @Nullable Object> copy = new LinkedHashMap<>();
        try {
            for (final Map.Entry<?, ?> entry : source.entrySet()) {
                if (!(entry.getKey() instanceof String)) {
                    throw new IllegalArgumentException("Map keys must be strings");
                }
                copy.put(
                        (String) entry.getKey(),
                        normalize(entry.getValue(), false, activeContainers)
                );
            }
        } finally {
            activeContainers.remove(source);
        }
        return Collections.unmodifiableMap(copy);
    }

    private static @Nullable Object normalize(
            @Nullable Object value,
            boolean insideCollection,
            @NonNull IdentityHashMap<@NonNull Object, @NonNull Boolean> activeContainers
    ) {
        if (value instanceof Map<?, ?>) {
            return copyMap((Map<?, ?>) value, insideCollection, activeContainers);
        }
        if (value instanceof Collection<?>) {
            return copyCollection((Collection<?>) value, activeContainers);
        }
        if (value != null && value.getClass().isArray()) {
            return copyArray(value, activeContainers);
        }
        return value;
    }

    private static @NonNull List<@Nullable Object> copyCollection(
            @NonNull Collection<?> source,
            @NonNull IdentityHashMap<@NonNull Object, @NonNull Boolean> activeContainers
    ) {
        enterContainer(source, activeContainers);
        final List<@Nullable Object> copy = new ArrayList<>(source.size());
        try {
            for (final Object item : source) {
                copy.add(normalize(item, true, activeContainers));
            }
        } finally {
            activeContainers.remove(source);
        }
        return Collections.unmodifiableList(copy);
    }

    private static @NonNull List<@Nullable Object> copyArray(
            @NonNull Object source,
            @NonNull IdentityHashMap<@NonNull Object, @NonNull Boolean> activeContainers
    ) {
        enterContainer(source, activeContainers);
        final int length = Array.getLength(source);
        final List<@Nullable Object> copy = new ArrayList<>(length);
        try {
            for (int index = 0; index < length; index++) {
                copy.add(normalize(Array.get(source, index), true, activeContainers));
            }
        } finally {
            activeContainers.remove(source);
        }
        return Collections.unmodifiableList(copy);
    }

    private static void enterContainer(
            @NonNull Object container,
            @NonNull IdentityHashMap<@NonNull Object, @NonNull Boolean> activeContainers
    ) {
        if (activeContainers.put(container, Boolean.TRUE) != null) {
            throw new IllegalArgumentException("Container cycles are not supported");
        }
    }
}
