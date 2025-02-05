package io.github.mjcro.toybox.api;

import io.github.mjcro.toybox.api.events.EventListener;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Environment {
    String
            VAR_TOYBOX_FILE_PATH = "toybox-file-path";

    /**
     * Executes given runnable in separate thread.
     *
     * @param r Runnable to run.
     */
    void execute(Runnable r);

    /**
     * Executes given supplier in separate thread.
     * If supplier returns runnable it will be invoked in UI thread using SwingUtilities.invokeLater.
     *
     * @param s Supplier to run.
     */
    default void execute(Supplier<Runnable> s) {
        this.execute(() -> {
            Runnable later = s.get();
            if (later != null) {
                SwingUtilities.invokeLater(later);
            }
        });
    }

    /**
     * Executes given consumer in separate thread.
     * UI thread invoker will be passed to given consumer on execution.
     *
     * @param c Consumer to run.
     */
    default void execute(Consumer<Consumer<Runnable>> c) {
        this.execute(() -> c.accept(SwingUtilities::invokeLater));
    }

    /**
     * @param name Variable name.
     * @return Toybox environment variable, if any.
     */
    Optional<String> getVariable(String name);

    /**
     * Sets Toybox environment variable.
     *
     * @param name  Variable name.
     * @param value Variable value.
     */
    void setVariable(String name, String value);

    /**
     * Registers collections of toys.
     * Toys being registered will be rendered by application in a way
     * the application does it - it can be menu, tree or other
     * navigation entity.
     *
     * @param toys Toys to register.
     */
    void registerToys(Toy... toys);

    /**
     * @return Collection of registered toys.
     */
    List<Toy> getRegisteredToys();

    /**
     * Registers event listener.
     *
     * @param listener Event listener.
     */
    void addEventListener(EventListener listener);

    /**
     * Handles given event.
     *
     * @param context Current toybox context.
     * @param event   Event to handle.
     */
    void handleEvent(Context context, Event event);

    /**
     * Registers new popup hook.
     *
     * @param hook Hook to register.
     */
    void addPopupHook(PopupHook hook);

    /**
     * @return List of registered popup hooks.
     */
    List<PopupHook> getPopupHooks();

    /**
     * Opens URL using system browser.
     *
     * @param url URL to open.
     */
    void openUrl(String url);

    /**
     * Puts string selection to clipboard.
     *
     * @param selection Data to put to clipboard.
     */
    void clipboardPut(StringSelection selection);

    /**
     * Puts char sequence to clipboard.
     *
     * @param s Data to put to clipboard.
     */
    default void clipboardPut(CharSequence s) {
        clipboardPut(new StringSelection(s.toString()));
    }

    /**
     * @return String from clipboard, if any.
     */
    Optional<String> clipboardGetString();

    /**
     * Displays file chooser and then invokes callback when file is chosen.
     *
     * @param callback    Callback to invoke.
     * @param fileFilters File filters.
     */
    void chooseFileToRead(FileCallback callback, FileFilter... fileFilters);

    /**
     * Displays file chooser and then invokes callback if approve options is chosen.
     *
     * @param callback Callback to invoke.
     * @param file     Initial file, optional, nullable.
     */
    void chooseFileToSave(FileCallback callback, File file);

    /**
     * Defines popup hook.
     */
    interface PopupHook {
        /**
         * Returns list of actions to add to popup.
         *
         * @param ctx    Toybox context.
         * @param target Popup event source.
         * @return List of actions.
         */
        List<Labeled> onPopup(Context ctx, Object target);
    }

    /**
     * Defines callback to invoke on file read/write dialog operations.
     */
    interface FileCallback {
        void onFileChosen(File file) throws IOException;

        void onNoFileChosen();
    }
}
