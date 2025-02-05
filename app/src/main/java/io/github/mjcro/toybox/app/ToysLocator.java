package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.services.EnvironmentModifier;
import io.github.mjcro.toybox.api.services.ToysProvider;

import java.util.ArrayList;
import java.util.ServiceLoader;

public class ToysLocator implements EnvironmentModifier {
    private final ArrayList<Toy> toys = new ArrayList<>();

    public ToysLocator() {
        ServiceLoader<Toy> toysLoader = ServiceLoader.load(Toy.class);
        toysLoader.forEach(this::add);

        ServiceLoader<ToysProvider> toysProvidersLoader = ServiceLoader.load(ToysProvider.class);
        toysProvidersLoader.forEach(toysProvider -> addAll(toysProvider.getToys()));
    }

    @Override
    public void modify(Environment environment) {
        toys.forEach(environment::registerToys);
    }

    private void add(Toy toy) {
        if (toy != null) {
            toys.add(toy);
        }
    }

    private void addAll(Iterable<Toy> toys) {
        if (toys != null) {
            toys.forEach(this::add);
        }
    }
}
