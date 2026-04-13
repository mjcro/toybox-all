package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.services.EnvironmentModifier;
import io.github.mjcro.toybox.api.services.ToysProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.ServiceLoader;

/**
 * {@link EnvironmentModifier} that discovers {@link Toy} implementations via
 * {@link ServiceLoader} and registers them with the environment.
 */
public class ToysLocator implements EnvironmentModifier {
    private final @NonNull ArrayList<@NonNull Toy> toys = new ArrayList<>();

    /**
     * Constructs a new locator, immediately loading toys from ServiceLoader.
     */
    public ToysLocator() {
        ServiceLoader<Toy> toysLoader = ServiceLoader.load(Toy.class);
        toysLoader.forEach(this::add);

        ServiceLoader<ToysProvider> toysProvidersLoader = ServiceLoader.load(ToysProvider.class);
        toysProvidersLoader.forEach(toysProvider -> addAll(toysProvider.getToys()));
    }

    /**
     * Registers all discovered toys with the given environment.
     *
     * @param environment the environment to register toys into
     */
    @Override
    public void modify(@NonNull Environment environment) {
        toys.forEach(environment::registerToys);
    }

    /**
     * Adds a single toy to the internal list. Null values are ignored.
     *
     * @param toy the toy to add, may be {@code null}
     */
    private void add(@Nullable Toy toy) {
        if (toy != null) {
            toys.add(toy);
        }
    }

    /**
     * Adds all toys from the given iterable. Null iterables are ignored.
     *
     * @param toys the toys to add, may be {@code null}
     */
    private void addAll(@Nullable Iterable<@NonNull Toy> toys) {
        if (toys != null) {
            toys.forEach(this::add);
        }
    }
}
