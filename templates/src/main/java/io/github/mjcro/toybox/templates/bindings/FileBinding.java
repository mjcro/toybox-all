package io.github.mjcro.toybox.templates.bindings;

import io.github.mjcro.toybox.api.Environment;
import io.github.mjcro.toybox.swing.BorderLayoutMaster;
import io.github.mjcro.toybox.swing.prefab.ToyBoxButtons;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class FileBinding extends AbstractJPanelContainerBinding {
    private final Environment environment;
    private JTextField chosenFileNameTextField = new JTextField();
    private JButton chooseFileButton = ToyBoxButtons.create("Choose", this::onChooseFileButtonClick);
    private JButton clearButton = ToyBoxButtons.create("Clear", this::onClearButtonClick);
    private File file;

    public FileBinding(@NonNull Environment environment, @NonNull Object target, @NonNull Field field) {
        super(target, field);
        this.environment = environment;
        initComponents();
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
        });
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
