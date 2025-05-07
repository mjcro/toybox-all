package io.github.mjcro.toybox.toys;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Label;
import io.github.mjcro.toybox.api.Menu;
import io.github.mjcro.toybox.api.Toy;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.security.Provider;
import java.security.Security;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CipherListToy implements Toy {
    @Override
    public List<Menu> getPath() {
        return List.of(Menu.TOYBOX_BASIC_TOOLS_MENU, Menu.TOYBOX_BASIC_TOOLS_CRYPTO_SUBMENU);
    }

    @Override
    public Label getLabel() {
        return Label.ofIconAndName("fam://shield", "Cipher List");
    }

    @Override
    public JPanel build(Context context) {
        return new Panel();
    }

    private static class Panel extends JPanel {
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
