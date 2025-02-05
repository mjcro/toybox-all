package io.github.mjcro.toybox.app;

import io.github.mjcro.toybox.api.Context;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.api.Event;
import io.github.mjcro.toybox.api.Toy;
import io.github.mjcro.toybox.api.events.EventListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationEnvironment implements Environment {
    private final ConcurrentLinkedQueue<PopupHook> popupHooks = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<EventListener> eventListeners = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Toy> registeredToys = new ConcurrentLinkedQueue<>();

    private final VariablesStorage variablesStorage;
    private final ExecutorService executorService;

    @Getter
    @Setter
    private java.awt.Component modalParent = null;

    @Override
    public void execute(Runnable r) {
        if (r != null) {
            executorService.submit(r);
        }
    }

    @Override
    public Optional<String> getVariable(String name) {
        log.info("Reading variable {}", name);
        return variablesStorage.getVariable(name);
    }

    @Override
    public void setVariable(String name, String value) {
        log.info("Setting variable {}", name);
        variablesStorage.setVariable(name, value);
    }

    @Override
    public void registerToys(Toy... toys) {
        if (toys != null) {
            for (Toy toy : toys) {
                registeredToys.add(toy);
            }
        }
    }

    @Override
    public List<Toy> getRegisteredToys() {
        return new ArrayList<>(registeredToys);
    }

    @Override
    public void addEventListener(EventListener listener) {
        if (listener != null) {
            eventListeners.add(listener);
        }
    }

    @Override
    public void handleEvent(Context context, Event event) {
        if (context != null && event != null) {
            log.info("Handling event {}", event);
            for (final EventListener listener : eventListeners) {
                SwingUtilities.invokeLater(() -> listener.handleEvent(context, event));
            }
        }
    }

    @Override
    public void addPopupHook(PopupHook hook) {
        popupHooks.add(hook);
    }

    @Override
    public List<PopupHook> getPopupHooks() {
        return new ArrayList<>(popupHooks);
    }

    @Override
    public void openUrl(String url) {
        if (url != null) {
            log.info("Opening URL {}", url);
            try {
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                }
            } catch (Exception error) {
                log.error("Error opening URL", error);
            }
        }
    }

    @Override
    public void clipboardPut(StringSelection selection) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            log.info("Clipping \"{}\"", selection.getTransferData(DataFlavor.stringFlavor));
        } catch (UnsupportedFlavorException | IOException ignore) {
            // ignore
        }
        clipboard.setContents(selection, null);
    }

    @Override
    public Optional<String> clipboardGetString() {
        try {
            Object data = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            return data instanceof String
                    ? Optional.of(data.toString())
                    : Optional.empty();
        } catch (UnsupportedFlavorException | IOException e) {
            log.error("Error reading data from clipboard", e);
        }

        return Optional.empty();
    }

    @Override
    public void chooseFileToRead(@NonNull FileCallback callback, FileFilter... fileFilters) {
        JFileChooser fileChooser = new JFileChooser();
        getVariable(Environment.VAR_TOYBOX_FILE_PATH).ifPresent(s -> fileChooser.setCurrentDirectory(new File(s)));
        fileChooser.setDialogTitle("Select a file");
        if (fileFilters != null) {
            for (FileFilter filter : fileFilters) {
                fileChooser.addChoosableFileFilter(filter);
            }
        }
        int userSelection = fileChooser.showOpenDialog(getModalParent());
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToRead = fileChooser.getSelectedFile();
            setVariable(Environment.VAR_TOYBOX_FILE_PATH, fileToRead.getParentFile().getAbsolutePath());
            log.info("Reading file {}", fileToRead.getAbsolutePath());
            try {
                callback.onFileChosen(fileToRead);
            } catch (IOException e) {
                log.error("Error reading file", e);
            }
        } else {
            callback.onNoFileChosen();
        }
    }

    @Override
    public void chooseFileToSave(@NonNull FileCallback callback, File file) {
        JFileChooser fileChooser = new JFileChooser();
        if (file != null) {
            fileChooser.setSelectedFile(file);
        }
        getVariable(Environment.VAR_TOYBOX_FILE_PATH).ifPresent(s -> fileChooser.setCurrentDirectory(new File(s)));
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
                    log.info("Cancelled overwrite");
                    callback.onNoFileChosen();
                    return;
                }
            }

            setVariable(Environment.VAR_TOYBOX_FILE_PATH, fileToSave.getParentFile().getAbsolutePath());
            log.info("Saving file {}", fileToSave.getAbsolutePath());
            try {
                callback.onFileChosen(fileToSave);
            } catch (IOException e) {
                log.error("Error saving file", e);
            }
        } else {
            callback.onNoFileChosen();
        }
    }
}
