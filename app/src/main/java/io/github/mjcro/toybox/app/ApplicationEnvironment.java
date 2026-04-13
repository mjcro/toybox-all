package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.SettingsStorage;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.events.EventListener;
import io.github.mjcro.toybox.app.settings.ToyBoxWorkingDirSetting;
import io.github.mjcro.toybox.swing.util.Slf4jUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * Spring-managed implementation of {@link Environment} that provides
 * the global application state including toy registry, event dispatch,
 * settings storage, clipboard access, and file dialogs.
 */
@Component
public class ApplicationEnvironment implements Environment {

    private static final @NonNull Logger log = LoggerFactory.getLogger(ApplicationEnvironment.class);

    private final @NonNull ConcurrentLinkedQueue<@NonNull PopupHook> popupHooks = new ConcurrentLinkedQueue<>();
    private final @NonNull ConcurrentLinkedQueue<@NonNull EventListener> eventListeners = new ConcurrentLinkedQueue<>();
    private final @NonNull ConcurrentLinkedQueue<@NonNull Toy> registeredToys = new ConcurrentLinkedQueue<>();
    private final @NonNull SettingsStorage settingsStorage;
    private final @NonNull ExecutorService executorService;
    private java.awt.@Nullable Component modalParent = null;

    /**
     * Creates a new application environment.
     *
     * @param settingsStorage the settings storage backend
     * @param executorService the executor for background tasks
     */
    public ApplicationEnvironment(
            @NonNull SettingsStorage settingsStorage,
            @NonNull ExecutorService executorService
    ) {
        this.settingsStorage = settingsStorage;
        this.executorService = executorService;
    }

    /**
     * Returns the modal parent component for dialogs.
     *
     * @return the modal parent, or {@code null}
     */
    public java.awt.@Nullable Component getModalParent() {
        return modalParent;
    }

    /**
     * Sets the modal parent component for dialogs.
     *
     * @param modalParent the modal parent, or {@code null}
     */
    public void setModalParent(java.awt.@Nullable Component modalParent) {
        this.modalParent = modalParent;
    }

    @Override
    public void execute(@Nullable Runnable r) {
        if (r != null) {
            executorService.submit(r);
        }
    }

    @Override
    public @NonNull SettingsStorage getSettingsStorage() {
        return settingsStorage;
    }

    @Override
    public void registerToys(@NonNull Toy @Nullable ... toys) {
        if (toys != null) {
            for (Toy toy : toys) {
                registeredToys.add(toy);
            }
        }
    }

    @Override
    public @NonNull List<@NonNull Toy> getRegisteredToys() {
        return new ArrayList<>(registeredToys);
    }

    @Override
    public @NonNull Optional<@NonNull Toy> findRegisteredToy(@NonNull Class<? extends Toy> clazz) {
        for (Toy registeredToy : registeredToys) {
            if (registeredToy.getClass().equals(clazz)) {
                return Optional.of(registeredToy);
            }
        }

        return Optional.empty();
    }

    @Override
    public @NonNull Optional<@NonNull Toy> findRegisteredToy(@NonNull String name) {
        // Full class name lookup
        for (Toy registeredToy : registeredToys) {
            if (registeredToy.getClass().getName().equals(name)) {
                return Optional.of(registeredToy);
            }
        }

        // Simple class name lookup
        for (Toy registeredToy : registeredToys) {
            if (registeredToy.getClass().getSimpleName().equals(name)) {
                return Optional.of(registeredToy);
            }
        }

        return Optional.empty();
    }

    @Override
    public void addEventListener(@Nullable EventListener listener) {
        if (listener != null) {
            eventListeners.add(listener);
        }
    }

    @Override
    public void handleEvent(@Nullable Context context, @Nullable Event event) {
        if (context != null && event != null) {
            log.info(Slf4jUtil.TOYBOX_MARKER, "Handling event {}", event);
            for (final EventListener listener : eventListeners) {
                SwingUtilities.invokeLater(() -> listener.handleEvent(context, event));
            }
        }
    }

    @Override
    public void addPopupHook(@NonNull PopupHook hook) {
        popupHooks.add(hook);
    }

    @Override
    public @NonNull List<@NonNull PopupHook> getPopupHooks() {
        return new ArrayList<>(popupHooks);
    }

    @Override
    public void openUrl(@Nullable String url) {
        if (url != null) {
            log.info(Slf4jUtil.TOYBOX_MARKER, "Opening URL {}", url);
            try {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                }
            } catch (Exception error) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Error opening URL {}", url, error);
            }
        }
    }

    @Override
    public void clipboardPut(@NonNull StringSelection selection) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            log.info(Slf4jUtil.TOYBOX_MARKER, "Clipping \"{}\"", selection.getTransferData(DataFlavor.stringFlavor));
        } catch (UnsupportedFlavorException | IOException ignore) {
            // ignore
        }
        clipboard.setContents(selection, null);
    }

    @Override
    public @NonNull Optional<@NonNull String> clipboardGetString() {
        try {
            Object data = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            return data instanceof String
                    ? Optional.of(data.toString())
                    : Optional.empty();
        } catch (UnsupportedFlavorException | IOException e) {
            log.error(Slf4jUtil.TOYBOX_MARKER, "Error reading data from clipboard", e);
        }

        return Optional.empty();
    }

    @Override
    public void chooseFileToRead(@NonNull FileCallback callback, @NonNull FileFilter @Nullable ... fileFilters) {
        Objects.requireNonNull(callback, "callback");
        JFileChooser fileChooser = new JFileChooser();
        getSettingsStorage().get(ToyBoxWorkingDirSetting.class)
                .map(ToyBoxWorkingDirSetting::getValue)
                .ifPresent(s -> fileChooser.setCurrentDirectory(new File(s)));
        fileChooser.setDialogTitle("Select a file");
        if (fileFilters != null) {
            for (FileFilter filter : fileFilters) {
                fileChooser.addChoosableFileFilter(filter);
            }
        }
        int userSelection = fileChooser.showOpenDialog(getModalParent());
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToRead = fileChooser.getSelectedFile();
            getSettingsStorage().put(new ToyBoxWorkingDirSetting(fileToRead.getParentFile()));
            log.info(Slf4jUtil.TOYBOX_MARKER, "Reading file {}", fileToRead.getAbsolutePath());
            try {
                callback.onFileChosen(fileToRead);
            } catch (IOException e) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Error reading file {}", fileToRead.getAbsolutePath(), e);
            }
        } else {
            callback.onNoFileChosen();
        }
    }

    @Override
    public void chooseFileToSave(@NonNull FileCallback callback, @Nullable File file) {
        Objects.requireNonNull(callback, "callback");
        JFileChooser fileChooser = new JFileChooser();
        if (file != null) {
            fileChooser.setSelectedFile(file);
        }
        getSettingsStorage().get(ToyBoxWorkingDirSetting.class)
                .map(ToyBoxWorkingDirSetting::getValue)
                .ifPresent(s -> fileChooser.setCurrentDirectory(new File(s)));
        fileChooser.setDialogTitle("Specify a file to save");
        int userSelection = fileChooser.showSaveDialog(getModalParent());
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (fileToSave.exists()) {
                int result = JOptionPane.showConfirmDialog(
                        getModalParent(),
                        String.format("File %s already exists, overwrite it?", fileToSave.getName()),
                        "Confirm file overwrite",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (result == JOptionPane.CANCEL_OPTION) {
                    log.info(Slf4jUtil.TOYBOX_MARKER, "Cancelled overwrite");
                    callback.onNoFileChosen();
                    return;
                }
            }

            getSettingsStorage().put(new ToyBoxWorkingDirSetting(fileToSave.getParentFile()));
            log.info(Slf4jUtil.TOYBOX_MARKER, "Saving file {}", fileToSave.getAbsolutePath());
            try {
                callback.onFileChosen(fileToSave);
            } catch (IOException e) {
                log.error(Slf4jUtil.TOYBOX_MARKER, "Error saving file {}", fileToSave.getAbsolutePath(), e);
            }
        } else {
            callback.onNoFileChosen();
        }
    }
}
