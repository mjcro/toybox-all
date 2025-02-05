package io.github.mjcro.toybox.app.vars;

import io.github.mjcro.toybox.app.VariablesStorage;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryVariableStorage implements VariablesStorage {
    private final ConcurrentHashMap<String, String> values = new ConcurrentHashMap<>();

    @Override
    public Optional<String> getVariable(String name) {
        return Optional.ofNullable(values.get(name));
    }

    @Override
    public void setVariable(String name, String value) {
        values.put(name, value);
    }

    @Override
    public Iterator<String> iterator() {
        return values.keys().asIterator();
    }
}
