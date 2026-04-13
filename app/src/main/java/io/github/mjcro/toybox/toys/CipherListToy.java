package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;
import org.jspecify.annotations.NonNull;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.BorderLayout;
import java.security.Provider;
import java.security.Security;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Toy that displays all available cipher algorithms grouped by provider and type
 * in a tree view.
 */
public class CipherListToy implements Toy {
    @Override
    public @NonNull List<@NonNull Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU, Menu.TOYBOX_BASIC_TOOLS_CRYPTO_SUBMENU);
    }

    @Override
    public @NonNull Label getLabel() {
        return Label.ofIconAndName("fam://shield", "Cipher List");
    }

    @Override
    public @NonNull JPanel build(@NonNull Context context) {
        return new Panel();
    }

    /**
     * Panel that builds and displays the cipher algorithm tree.
     */
    private static class Panel extends JPanel {
        /**
         * Constructs the panel, populating the tree from the JCA security providers.
         */
        public Panel() {
            super(new BorderLayout());
            var root = new DefaultMutableTreeNode("Cipher List");
            for (Provider provider : Security.getProviders()) {
                Map<String, List<Provider.Service>> algs = provider.getServices().stream()
                        .sorted(Comparator.comparing(Provider.Service::getAlgorithm))
                        .collect(Collectors.groupingBy(Provider.Service::getType));

                if (!algs.isEmpty()) {
                    var providerNode = new DefaultMutableTreeNode(provider.getName());
                    for (Map.Entry<String, List<Provider.Service>> entry : algs.entrySet()) {
                        var typeNode = new DefaultMutableTreeNode(entry.getKey());
                        for (Provider.Service service : entry.getValue()) {
                            typeNode.add(new DefaultMutableTreeNode(service.getAlgorithm()));
                        }
                        providerNode.add(typeNode);
                    }
                    root.add(providerNode);
                }
            }

            JTree tree = new JTree(root);
            add(new JScrollPane(tree));
        }
    }
}
