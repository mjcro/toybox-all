package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.interfaces.Decorator;
import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.swing.BorderLayoutMaster;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import io.github.mjcro.toybox.swing.prefab.ToyBoxTextComponents;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FileBinding extends AbstractJPanelContainerBinding {
    private final Environment environment;
    private final JTextField chosenFileNameTextField = ToyBoxTextComponents.createJTextField();
    private final JButton chooseFileButton = ToyBoxButtons.create("Choose", this::onChooseFileButtonClick);
    private final JButton clearButton = ToyBoxButtons.create("Clear", this::onClearButtonClick);
    private File file;
    private FileFilter[] fileFilters = null;

    public FileBinding(
            @NonNull Environment environment,
            @NonNull Object target,
            @NonNull Field field
    ) {
        super(target, field);
        this.environment = environment;

        // Reading options
        Class<?> options = annotation.options();
        if (options != null && options != Void.class) {
            readOptions(options);
        }

        initComponents();
    }

    private void readOptions(Class<?> options) {
        if (options.isEnum()) {
            Object[] constants = options.getEnumConstants();
            ArrayList<FileFilter> filters = new ArrayList<>();
            for (Object c : constants) {
                if (c instanceof Decorator<?>) {
                    c = ((Decorator<?>) c).getDecorated();
                }
                if (c instanceof FileFilter) {
                    filters.add((FileFilter) c);
                }
            }

            if (!filters.isEmpty()) {
                fileFilters = filters.toArray(new FileFilter[0]);
            }
        }
    }

    private void initComponents() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        buttons.add(chooseFileButton);
        buttons.add(clearButton);

        clearButton.setEnabled(false);

        chosenFileNameTextField.setText(getLabelName());
        chosenFileNameTextField.setEditable(false);

        chosenFileNameTextField.setDropTarget(new DropTarget() {
            @SuppressWarnings("unchecked")
            @Override
            public synchronized void drop(final DropTargetDropEvent e) {
                e.acceptDrop(DnDConstants.ACTION_COPY);
                try {
                    List<File> droppedFiles = (List<File>) e.getTransferable()
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

    private void setFile(File file) {
        this.file = file;
        chosenFileNameTextField.setText(
                file == null
                        ? getLabelName()
                        : file.getName()
        );
        clearButton.setEnabled(file != null);
        fireSubmit();
    }

    @Override
    public void setEnabled(boolean enabled) {
        chooseFileButton.setEnabled(enabled);
        clearButton.setEnabled(enabled && file != null);
    }

    @Override
    public void applyCurrentValue() throws IllegalAccessException {
        field.set(target, file);
    }
}
