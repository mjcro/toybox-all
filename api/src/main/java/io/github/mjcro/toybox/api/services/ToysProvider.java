package io.github.mjcro.toybox.api.services;

import io.github.mjcro.toybox.api.Toy;

import java.util.List;

public interface ToysProvider {
    List<Toy> getToys();
}
