package io.github.mjcro.toybox.app.vars;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mjcro.toybox.app.VariablesStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class JsonVariableStorage implements VariablesStorage {
    private final ObjectMapper mapper = new ObjectMapper();
    private final File file;

    public JsonVariableStorage(File file) {
        this.file = Objects.requireNonNull(file, "file");
    }

    private boolean fileNotReadable() {
        return !(file.exists() && file.canRead() && !file.isDirectory());
    }

    private boolean fileNotWritable() {
        return !((!file.exists() || file.canWrite()) && !file.isDirectory());
    }

    protected InputStream getInputStream() throws Exception {
        return new FileInputStream(file);
    }

    protected OutputStream getOutputStream() throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        }
        return new FileOutputStream(file);
    }

    private HashMap<String, String> readAll() throws Exception {
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (InputStream is = getInputStream()) {
            return mapper.readValue(is, Entity.class);
        }
    }

    @Override
    public Optional<String> getVariable(String name) {
        if (fileNotReadable()) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(readAll().get(name));
        } catch (Exception e) {
            throw new JsonVariableStorageException(e);
        }
    }

    @Override
    public void setVariable(String name, String value) {
        if (fileNotWritable()) {
            return;
        }

        try {
            HashMap<String, String> map = readAll();
            map.put(name, value);
            try (OutputStream os = getOutputStream()) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(os, map);
            }
        } catch (Exception e) {
            throw new JsonVariableStorageException(e);
        }
    }

    @Override
    public Iterator<String> iterator() {
        try {
            return readAll().keySet().iterator();
        } catch (Exception e) {
            throw new JsonVariableStorageException(e);
        }
    }

    private static final class Entity extends HashMap<String, String> {

    }

    private static final class JsonVariableStorageException extends RuntimeException {
        public JsonVariableStorageException(Exception cause) {
            super(cause);
        }
    }
}
