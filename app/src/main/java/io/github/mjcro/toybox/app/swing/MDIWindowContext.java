package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.AbstractToy;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.events.ShowToyEvent;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

@Slf4j
class MDIWindowContext extends AbstractWindowContext<MDIWindow> {
    MDIWindowContext(Environment environment, MDIWindow mdi) {
        super(environment, mdi, new JPopupMenu(), null);
        this.popupMenu.addPopupMenuListener(new OnPopup());
    }

    private MDIWindowContext(MDIWindowContext previous, Object initialData) {
        super(previous.getEnvironment(), previous.mainWindow, previous.popupMenu, initialData);
    }

    MDIWindowContext withInitialData(Object data) {
        return new MDIWindowContext(this, data);
    }

    @Override
    public void sendEvent(Event event) {
        if (event instanceof ShowToyEvent) {
            ShowToyEvent e = (ShowToyEvent) event;
            showToyWindow(e.getToy(), e.getInitialData().orElse(null));
        }

        getEnvironment().handleEvent(this, event);
    }

    private void showToyWindow(AbstractToy toy, Object data) {
        Context context = this.withInitialData(data);

        log.info(Slf4jUtil.TOYBOX_MARKER, "Showing toy \"{}\" backed by \"{}\"", toy.getLabel().getName(), toy.getClass().getSimpleName());
        JPanel panel = buildToyPanel(toy);
        attachPopup(panel);

        JInternalFrame internalFrame = new JInternalFrame(toy.getLabel().getName());
        toy.getLabel().getIconURI().flatMap(ToyBoxIcons::getSmall).ifPresent(internalFrame::setFrameIcon);
        internalFrame.setClosable(true);
        internalFrame.setIconifiable(true);
        internalFrame.setMaximizable(true);
        internalFrame.setResizable(true);
        internalFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container contentPane = internalFrame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);

        internalFrame.pack();
        internalFrame.updateUI();
        internalFrame.setVisible(true);
        mainWindow.desktop.add(internalFrame);

        try {
            internalFrame.setMaximum(true);
        } catch (PropertyVetoException e) {
            // ignore
        }
    }
}
