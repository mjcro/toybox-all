package io.github.mjcro.toybox.app;

import io.github.mjcro.interfaces.strings.WithUri;
import io.github.mjcro.toybox.api.Menu;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A tree structure used for organizing toys into a hierarchical navigation menu.
 */
public class NavigationTree {
    private final @NonNull Node root = new Node("toybox://root", Menu.text("Root"));

    /**
     * Adds a path of menu entries to the tree.
     *
     * @param path the ordered collection of menu entries forming the path
     */
    public void addPath(@NonNull Collection<@NonNull Menu> path) {
        root.addPath(path);
    }

    /**
     * Returns the root node of the navigation tree.
     *
     * @return the root node
     */
    public @NonNull Node getRoot() {
        return root;
    }

    /**
     * Extracts the URI from a menu entry, falling back to the name if the entry
     * does not implement {@link WithUri}.
     *
     * @param action the menu entry
     * @return the URI or name string
     */
    private static @NonNull String extractURI(@NonNull Menu action) {
        if (action instanceof WithUri) {
            return ((WithUri) action).getURI();
        }

        return action.getName();
    }

    /**
     * A node in the navigation tree, holding a menu entry and optional children.
     */
    public static class Node {
        private final @NonNull String uri;
        private final @NonNull Menu menu;
        private final @NonNull List<@NonNull Node> nested;

        private Node(@NonNull String uri, @NonNull Menu menu) {
            this.uri = uri;
            this.menu = menu;
            this.nested = new ArrayList<>();
        }

        /**
         * Recursively adds a path of menu entries as children of this node.
         * A single-element path adds a leaf; longer paths create or reuse branches.
         *
         * @param path the ordered collection of menu entries forming the remaining path
         */
        public void addPath(@Nullable Collection<@NonNull Menu> path) {
            if (path == null || path.isEmpty()) {
                return;
            }

            Menu first = path.stream().findFirst().get();
            String uri = extractURI(first);

            Node found = null;
            for (Node node : nested) {
                if (node.uri.equalsIgnoreCase(uri)) {
                    found = node;
                    break;
                }
            }

            if (path.size() == 1) {
                // Need to add leaf
                if (found != null) {
                    throw new RuntimeException("Unable to add " + first.getName() + " - already exists");
                }
                nested.add(new Node(uri, first));
            } else {
                // Need to add branch
                if (found == null) {
                    found = new Node(uri, first);
                    nested.add(found);
                }
                found.addPath(new ArrayList<>(path).subList(1, path.size()));
            }
        }

        /**
         * Returns the menu entry associated with this node.
         *
         * @return the menu entry
         */
        public @NonNull Menu getMenu() {
            return menu;
        }

        /**
         * Returns {@code true} if this node has child nodes.
         *
         * @return whether this node has nested children
         */
        public boolean hasNested() {
            return !nested.isEmpty();
        }

        /**
         * Returns the child nodes sorted by order then name.
         *
         * @return an unmodifiable list of child nodes
         */
        public @NonNull List<@NonNull Node> getNested() {
            if (nested.isEmpty()) {
                return List.of();
            } else if (nested.size() == 1) {
                return Collections.singletonList(nested.iterator().next());
            }

            ArrayList<Node> sorted = new ArrayList<>(nested);
            sorted.sort((a, b) -> {
                int ia = a.getMenu().getOrder();
                int ib = b.getMenu().getOrder();
                if (ia != ib) {
                    return Integer.compare(ia, ib);
                }
                return a.getMenu().getName().compareTo(b.getMenu().getName());
            });
            return Collections.unmodifiableList(sorted);
        }

        @Override
        public @NonNull String toString() {
            return getMenu().getName();
        }
    }

    /**
     * Demo main method for testing tree construction.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(@NonNull String[] args) {
        var nt = new NavigationTree();

        nt.addPath(List.of(Menu.text("Hello")));
        nt.addPath(List.of(Menu.text("Menu"), Menu.text("Item 1")));
        nt.addPath(List.of(Menu.text("Menu"), Menu.text("Item 2")));
        nt.addPath(List.of(Menu.text("Menu"), Menu.text("Submenu"), Menu.text("Item 3")));
        System.out.println();
    }
}
