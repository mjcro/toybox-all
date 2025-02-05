package io.github.mjcro.toybox.app;

import io.github.mjcro.interfaces.strings.WithUri;
import io.github.mjcro.toybox.api.Menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NavigationTree {
    private final Node root = new Node("toybox://root", Menu.text("Root"));

    public void addPath(Collection<Menu> path) {
        root.addPath(path);
    }

    public Node getRoot() {
        return root;
    }

    private static String extractURI(Menu action) {
        if (action instanceof WithUri) {
            return ((WithUri) action).getURI();
        }

        return action.getName();
    }

    public static class Node {
        private final String uri;
        private final Menu menu;
        private final List<Node> nested;

        private Node(String uri, Menu menu) {
            this.uri = uri;
            this.menu = menu;
            this.nested = new ArrayList<>();
        }

        public void addPath(Collection<Menu> path) {
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

        public Menu getMenu() {
            return menu;
        }

        public boolean hasNested() {
            return !nested.isEmpty();
        }

        public List<Node> getNested() {
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
        public String toString() {
            return getMenu().getName();
        }
    }

    public static void main(String[] args) {
        var nt = new NavigationTree();

        nt.addPath(List.of(Menu.text("Hello")));
        nt.addPath(List.of(Menu.text("Menu"), Menu.text("Item 1")));
        nt.addPath(List.of(Menu.text("Menu"), Menu.text("Item 2")));
        nt.addPath(List.of(Menu.text("Menu"), Menu.text("Submenu"), Menu.text("Item 3")));
        System.out.println();
    }
}
