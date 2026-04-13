package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.swing.BorderLayoutMaster;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Input panel that lets users choose a file via a file dialog, drag-and-drop, or clear the selection.
 */
public class FileChooserInput extends JPanel {
    private final @NonNull Environment environment;
    private final @Nullable String label;
    private final @NonNull JButton
            chooseFileButton = ToyBoxButtons.create("Choose", this::onChooseFileButtonClick),
            clearButton = ToyBoxButtons.create("Clear", this::onClearButtonClick);
    private final @NonNull JTextField chosenFileNameTextField = ToyBoxTextComponents.createJTextField();
    private final @NonNull FileFilter @NonNull [] fileFilters;
    private final @NonNull Runnable onFileChange;
    private volatile @Nullable File file;

    /**
     * Creates a file chooser input with a change callback and optional file filters.
     *
     * @param environment  the application environment for file dialog access
     * @param label        the placeholder text shown when no file is selected
     * @param onFileChange callback invoked when the selected file changes, or {@code null} for no-op
     * @param fileFilters  optional file filters for the file chooser dialog
     */
    public FileChooserInput(@NonNull Environment environment, @Nullable String label, @Nullable Runnable onFileChange, @NonNull FileFilter @NonNull ... fileFilters) {
        this.environment = Objects.requireNonNull(environment, "environment");
        this.label = label;
        this.fileFilters = fileFilters;
        this.onFileChange = onFileChange == null ? () -> {
        } : onFileChange;

        initComponents();
    }

    /**
     * Creates a file chooser input without a change callback.
     *
     * @param environment the application environment for file dialog access
     * @param label       the placeholder text shown when no file is selected
     * @param fileFilters optional file filters for the file chooser dialog
     */
    public FileChooserInput(@NonNull Environment environment, @Nullable String label, @NonNull FileFilter @NonNull ... fileFilters) {
        this(environment, label, null, fileFilters);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        chooseFileButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        chosenFileNameTextField.setEnabled(enabled);
    }

    /**
     * Initializes and lays out the sub-components.
     */
    private void initComponents() {
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        buttons.add(chooseFileButton);
        buttons.add(clearButton);

        clearButton.setEnabled(false);

        chosenFileNameTextField.setText(label);
        chosenFileNameTextField.setEditable(false);

        chosenFileNameTextField.setDropTarget(new DropTarget() {
            @SuppressWarnings("unchecked")
            @Override
            public synchronized void drop(final @NonNull DropTargetDropEvent e) {
                e.acceptDrop(DnDConstants.ACTION_COPY);
                try {
                    final List<@NonNull File> droppedFiles = (List<File>) e.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (droppedFiles != null && !droppedFiles.isEmpty()) {
                        setFile(droppedFiles.get(0));
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }
        });
        chooseFileButton.setDropTarget(chosenFileNameTextField.getDropTarget());

        final JPanel paddedTextField = new JPanel(new BorderLayout());
        chosenFileNameTextField.setMinimumSize(new Dimension(200, chosenFileNameTextField.getMinimumSize().height));
        paddedTextField.add(chosenFileNameTextField);

        BorderLayoutMaster.addCenterRight(this, paddedTextField, buttons);
    }

    /**
     * Handles the "Choose" button click by opening the file dialog.
     *
     * @param e the action event
     */
    public void onChooseFileButtonClick(@NonNull ActionEvent e) {
        chooseFileButton.setEnabled(false);
        environment.chooseFileToRead(new Environment.FileCallback() {
            @Override
            public void onFileChosen(@NonNull File file) {
                setFile(file);
                chooseFileButton.setEnabled(true);
            }

            @Override
            public void onNoFileChosen() {
                chooseFileButton.setEnabled(true);
            }
        }, fileFilters);
    }

    /**
     * Handles the "Clear" button click by removing the selected file.
     *
     * @param e the action event
     */
    public void onClearButtonClick(@NonNull ActionEvent e) {
        setFile(null);
    }

    /**
     * Sets or clears the currently selected file and notifies the change callback.
     *
     * @param file the file to set, or {@code null} to clear
     */
    public void setFile(@Nullable File file) {
        this.file = file;
        chosenFileNameTextField.setText(
                file == null
                        ? label
                        : file.getName()
        );
        clearButton.setEnabled(file != null);
        onFileChange.run();
    }

    /**
     * Returns the currently selected file, if any.
     *
     * @return an optional containing the selected file
     */
    public @NonNull Optional<@NonNull File> getFile() {
        return Optional.ofNullable(file);
    }
}
