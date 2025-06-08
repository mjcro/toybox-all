package io.github.mjcro.toybox.swing.widgets;

import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.swing.BorderLayoutMaster;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FileChooserInput extends JPanel {
    private final Environment environment;
    private final String label;
    private final JButton
            chooseFileButton = ToyBoxButtons.create("Choose", this::onChooseFileButtonClick),
            clearButton = ToyBoxButtons.create("Clear", this::onClearButtonClick);
    private final JTextField chosenFileNameTextField = ToyBoxTextComponents.createJTextField();
    private final FileFilter[] fileFilters;
    private final Runnable onFileChange;
    private volatile File file;

    public FileChooserInput(Environment environment, String label, Runnable onFileChange, FileFilter... fileFilters) {
        this.environment = Objects.requireNonNull(environment, "environment");
        this.label = label;
        this.fileFilters = fileFilters;
        this.onFileChange = onFileChange == null ? () -> {
        } : onFileChange;

        initComponents();
    }

    public FileChooserInput(Environment environment, String label, FileFilter... fileFilters) {
        this(environment, label, null, fileFilters);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        chooseFileButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        chosenFileNameTextField.setEnabled(enabled);
    }

    private void initComponents() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        buttons.add(chooseFileButton);
        buttons.add(clearButton);

        clearButton.setEnabled(false);

        chosenFileNameTextField.setText(label);
        chosenFileNameTextField.setEditable(false);

        chosenFileNameTextField.setDropTarget(new DropTarget() {
            @SuppressWarnings("unchecked")
            @Override
            public synchronized void drop(final DropTargetDropEvent e) {
                e.acceptDrop(DnDConstants.ACTION_COPY);
                try {
                    java.util.List<File> droppedFiles = (List<File>) e.getTransferable()
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

        JPanel paddedTextField = new JPanel(new BorderLayout());
        chosenFileNameTextField.setMinimumSize(new Dimension(200, chosenFileNameTextField.getMinimumSize().height));
        paddedTextField.add(chosenFileNameTextField);

        BorderLayoutMaster.addCenterRight(this, paddedTextField, buttons);
    }

    public void onChooseFileButtonClick(ActionEvent e) {
        chooseFileButton.setEnabled(false);
        environment.chooseFileToRead(new Environment.FileCallback() {
            @Override
            public void onFileChosen(File file) {
                setFile(file);
                chooseFileButton.setEnabled(true);
            }

            @Override
            public void onNoFileChosen() {
                chooseFileButton.setEnabled(true);
            }
        }, fileFilters);
    }

    public void onClearButtonClick(ActionEvent e) {
        setFile(null);
    }

    public void setFile(File file) {
        this.file = file;
        chosenFileNameTextField.setText(
                file == null
                        ? label
                        : file.getName()
        );
        clearButton.setEnabled(file != null);
        onFileChange.run();
    }

    public Optional<File> getFile() {
        return Optional.ofNullable(file);
    }
}
