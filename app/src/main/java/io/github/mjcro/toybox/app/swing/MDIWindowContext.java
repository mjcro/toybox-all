package io.github.mjcro.toybox.app.swing;

import io.github.mjcro.toybox.api.AbstractToy;
import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.events.ShowToyEvent;
import io.github.mjcro.toybox.swing.prefab.ToyBoxIcons;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Container;
import java.beans.PropertyVetoException;

/**
 * MDI-specific implementation of the window context.
 *
 * <p>Handles {@link ShowToyEvent} by opening each toy inside a new
 * {@link JInternalFrame} on the MDI desktop.
 */
class MDIWindowContext extends AbstractWindowContext<MDIWindow> {
    private static final @NonNull Logger log = LoggerFactory.getLogger(MDIWindowContext.class);

    /**
     * Creates a new root MDI window context.
     *
     * @param environment the application environment
     * @param mdi         the parent MDI window
     */
    MDIWindowContext(@NonNull Environment environment, @NonNull MDIWindow mdi) {
        super(environment, mdi, new JPopupMenu(), null);
        this.popupMenu.addPopupMenuListener(new OnPopup());
    }

    /**
     * Creates a child context carrying initial data.
     *
     * @param previous    the parent context to fork from
     * @param initialData optional data to seed the new context
     */
    private MDIWindowContext(@NonNull MDIWindowContext previous, @Nullable Object initialData) {
        super(previous.getEnvironment(), previous.mainWindow, previous.popupMenu, initialData);
    }

    /**
     * Returns a new context that carries the given initial data.
     *
     * @param data the initial data to attach
     * @return a new {@link MDIWindowContext} with the specified data
     */
    @NonNull MDIWindowContext withInitialData(@Nullable Object data) {
        return new MDIWindowContext(this, data);
    }

    /**
     * Dispatches the given event. {@link ShowToyEvent} instances are handled
     * by opening a new internal frame; all events are forwarded to the environment.
     *
     * @param event the event to dispatch
     */
    @Override
    public void sendEvent(@NonNull Event event) {
        if (event instanceof ShowToyEvent) {
            ShowToyEvent e = (ShowToyEvent) event;
            showToyWindow(e.getToy(), e.getInitialData().orElse(null));
        }

        getEnvironment().handleEvent(this, event);
    }

    /**
     * Opens a toy inside a new {@link JInternalFrame} on the MDI desktop.
     *
     * @param toy  the toy to display
     * @param data optional initial data for the toy context
     */
    private void showToyWindow(@NonNull AbstractToy toy, @Nullable Object data) {
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
