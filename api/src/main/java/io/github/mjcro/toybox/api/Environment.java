package io.github.mjcro.toybox.api;

import io.github.mjcro.toybox.api.events.EventListener;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Central application environment providing access to toy registry, event handling,
 * settings storage, clipboard operations, file dialogs, and background execution.
 */
public interface Environment extends Executor {
    /**
     * Executes given runnable in separate thread.
     *
     * @param r Runnable to run.
     */
    @Override
    void execute(@NonNull Runnable r);

    /**
     * Executes given supplier in separate thread.
     * If supplier returns a runnable it will be invoked in UI thread using
     * {@link SwingUtilities#invokeLater(Runnable)}.
     *
     * @param s Supplier to run.
     */
    default void execute(@NonNull Supplier<@Nullable Runnable> s) {
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
    default void execute(@NonNull Consumer<@NonNull Consumer<@NonNull Runnable>> c) {
        this.execute(() -> c.accept(SwingUtilities::invokeLater));
    }

    /**
     * Returns the settings storage for persisting application settings.
     *
     * @return Settings storage.
     */
    @NonNull SettingsStorage getSettingsStorage();

    /**
     * Registers collections of toys.
     * Toys being registered will be rendered by application in a way
     * the application does it - it can be menu, tree or other
     * navigation entity.
     *
     * @param toys Toys to register.
     */
    void registerToys(@NonNull Toy @NonNull ... toys);

    /**
     * Returns all registered toys.
     *
     * @return Collection of registered toys.
     */
    @NonNull List<@NonNull Toy> getRegisteredToys();

    /**
     * Searches for toy with given class.
     *
     * @param clazz Toy class.
     * @return Toy, if found.
     */
    @NonNull Optional<@NonNull Toy> findRegisteredToy(@NonNull Class<? extends @NonNull Toy> clazz);

    /**
     * Searches for toy with given class name.
     *
     * @param name Toy class name.
     * @return Toy, if found.
     */
    @NonNull Optional<@NonNull Toy> findRegisteredToy(@NonNull String name);

    /**
     * Registers event listener.
     *
     * @param listener Event listener.
     */
    void addEventListener(@NonNull EventListener listener);

    /**
     * Handles given event.
     *
     * @param context Current toybox context.
     * @param event   Event to handle.
     */
    void handleEvent(@NonNull Context context, @NonNull Event event);

    /**
     * Registers new popup hook.
     *
     * @param hook Hook to register.
     */
    void addPopupHook(@NonNull PopupHook hook);

    /**
     * Returns all registered popup hooks.
     *
     * @return List of registered popup hooks.
     */
    @NonNull List<@NonNull PopupHook> getPopupHooks();

    /**
     * Opens URL using system browser.
     *
     * @param url URL to open.
     */
    void openUrl(@NonNull String url);

    /**
     * Puts string selection to clipboard.
     *
     * @param selection Data to put to clipboard.
     */
    void clipboardPut(@NonNull StringSelection selection);

    /**
     * Puts char sequence to clipboard.
     *
     * @param s Data to put to clipboard.
     */
    default void clipboardPut(@NonNull CharSequence s) {
        clipboardPut(new StringSelection(s.toString()));
    }

    /**
     * Returns the current string content from the system clipboard, if available.
     *
     * @return String from clipboard, if any.
     */
    @NonNull Optional<@NonNull String> clipboardGetString();

    /**
     * Displays file chooser and then invokes callback when file is chosen.
     *
     * @param callback    Callback to invoke.
     * @param fileFilters File filters.
     */
    void chooseFileToRead(@NonNull FileCallback callback, @NonNull FileFilter @NonNull ... fileFilters);

    /**
     * Displays file chooser and then invokes callback if approve option is chosen.
     *
     * @param callback Callback to invoke.
     * @param file     Initial file, optional, nullable.
     */
    void chooseFileToSave(@NonNull FileCallback callback, @Nullable File file);

    /**
     * Defines popup hook for contributing actions to context menus.
     */
    interface PopupHook {
        /**
         * Returns list of actions to add to popup.
         *
         * @param ctx    ToyBox context.
         * @param target Popup event source.
         * @return List of actions.
         */
        @NonNull List<@NonNull Labeled> onPopup(@NonNull Context ctx, @NonNull Object target);
    }

    /**
     * Defines callback to invoke on file read/write dialog operations.
     */
    interface FileCallback {
        /**
         * Invoked when a file has been chosen by the user.
         *
         * @param file Chosen file.
         * @throws IOException If an I/O error occurs during processing.
         */
        void onFileChosen(@NonNull File file) throws IOException;

        /**
         * Invoked when no file was chosen (dialog cancelled).
         */
        void onNoFileChosen();
    }
}
