package com.tilemarkermetronome.ui;

import com.tilemarkermetronome.TileMarkerMetronomeGroup;
import com.tilemarkermetronome.TileMarkerMetronomePlugin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ImageUtil;

import static com.tilemarkermetronome.ui.util.ComponentUtil.configureMouseListener;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.NORTH;
import static java.awt.FlowLayout.LEFT;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static net.runelite.client.ui.ColorScheme.DARKER_GRAY_COLOR;
import static net.runelite.client.ui.ColorScheme.LIGHT_GRAY_COLOR;
import static net.runelite.client.ui.ColorScheme.PROGRESS_COMPLETE_COLOR;
import static net.runelite.client.ui.ColorScheme.PROGRESS_ERROR_COLOR;
import static net.runelite.client.ui.FontManager.getRunescapeFont;

@Slf4j
public class TileMarkerGroupPanel extends JPanel {
    private static final Border LABEL_BOTTOM_BORDER = BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR);

    private static final BufferedImage DELETE_IMAGE = ImageUtil.loadImageResource(TileMarkerMetronomePlugin.class, "delete_icon.png");
    private static final ImageIcon DELETE_ICON = new ImageIcon(DELETE_IMAGE);
    private static final ImageIcon DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(DELETE_IMAGE, -100));

    private static final BufferedImage EDIT_IMAGE = ImageUtil.loadImageResource(TileMarkerMetronomePlugin.class, "edit_icon.png");
    private static final ImageIcon EDIT_ICON = new ImageIcon(EDIT_IMAGE);
    private static final ImageIcon EDIT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(EDIT_IMAGE, -100));

    private final JLabel saveLabel = new JLabel("Save");
    private final JLabel cancelLabel = new JLabel("Cancel");
    private final JLabel renameLabel = new JLabel(EDIT_ICON);
    private final JLabel deleteLabel = new JLabel(DELETE_ICON);

    private final FlatTextField labelInput = new FlatTextField();


    private final TileMarkerMetronomePlugin plugin;
    private final TileMarkerMetronomeGroup group;

    public TileMarkerGroupPanel(TileMarkerMetronomePlugin plugin, TileMarkerMetronomeGroup group) {
        this.plugin = plugin;
        this.group = group;

        setLayout(new BorderLayout());
        setBackground(DARKER_GRAY_COLOR);

        createGroupNameComponent();
        createGroupConfigComponent();
    }

    private void createGroupNameComponent() {
        JPanel labelWrapper = new JPanel(new BorderLayout());
        labelWrapper.setBackground(DARKER_GRAY_COLOR);
        labelWrapper.setBorder(LABEL_BOTTOM_BORDER);

        JPanel labelActionsContainer = new JPanel(new BorderLayout(3, 0));
        JPanel labelActions = new JPanel(new FlowLayout());
        labelActionsContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        labelActionsContainer.setBackground(DARKER_GRAY_COLOR);
        labelActions.setBorder(new EmptyBorder(0, 0, 0, 8));
        labelActions.setBackground(DARKER_GRAY_COLOR);

        labelInput.setText(group.getLabel());
        labelInput.setBorder(null);
        labelInput.setEditable(false);
        labelInput.setBackground(DARKER_GRAY_COLOR);
        labelInput.setPreferredSize(new Dimension(0, 24));
        labelInput.getTextField().setForeground(Color.WHITE);
        labelInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));
        labelInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                switch (keyEvent.getKeyChar()) {
                    case VK_ENTER: {
                        save();
                        break;
                    }
                    case VK_ESCAPE: {
                        cancel();
                        break;
                    }
                }
            }
        });

        saveLabel.setVisible(false);
        saveLabel.setFont(getRunescapeFont());
        saveLabel.setForeground(PROGRESS_COMPLETE_COLOR);
        configureMouseListener(
                saveLabel,
                ignored -> save(),
                ignored -> saveLabel.setForeground(PROGRESS_COMPLETE_COLOR.darker()),
                ignored -> saveLabel.setForeground(PROGRESS_COMPLETE_COLOR));

        cancelLabel.setVisible(false);
        cancelLabel.setFont(getRunescapeFont());
        cancelLabel.setForeground(PROGRESS_ERROR_COLOR);
        configureMouseListener(
                cancelLabel,
                ignored -> cancel(),
                ignored -> cancelLabel.setForeground(PROGRESS_ERROR_COLOR.darker()),
                ignored -> cancelLabel.setForeground(PROGRESS_ERROR_COLOR));

        renameLabel.setVisible(true);
        renameLabel.setForeground(LIGHT_GRAY_COLOR.darker());
        configureMouseListener(
                renameLabel,
                ignored -> {
                    labelInput.setEditable(true);
                    showSaveAndCancelActions();
                },
                ignored -> renameLabel.setIcon(EDIT_HOVER_ICON),
                ignored -> renameLabel.setIcon(EDIT_ICON));

        deleteLabel.setToolTipText("Delete group");
        deleteLabel.setVisible(true);
        configureMouseListener(
                deleteLabel,
                ignored -> confirmDeleteGroup(),
                ignored -> deleteLabel.setIcon(DELETE_HOVER_ICON),
                ignored -> deleteLabel.setIcon(DELETE_ICON)
        );

        labelActions.add(saveLabel);
        labelActions.add(cancelLabel);
        labelActions.add(renameLabel);
        labelActions.add(deleteLabel);
        labelActionsContainer.add(labelActions);

        labelWrapper.add(labelInput, CENTER);
        labelWrapper.add(labelActionsContainer, EAST);
        add(labelWrapper, NORTH);
    }

    private void createGroupConfigComponent() {
        JPanel centerContainer = new JPanel(new GridLayout(0, 1));
        centerContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        centerContainer.setBackground(DARKER_GRAY_COLOR);

        JPanel colorPickerContainer = new JPanel(new FlowLayout(LEFT));
        colorPickerContainer.setBorder(new EmptyBorder(0, 0, 0, 0));
        colorPickerContainer.setBackground(DARKER_GRAY_COLOR);

        JLabel colorLabel = new JLabel("Tile colors");
        colorLabel.setBorder(new EmptyBorder(0, 8, 0, 0));

        centerContainer.add(colorLabel, LEFT_ALIGNMENT);
        centerContainer.add(colorPickerContainer, LEFT_ALIGNMENT);

        //TODO Change to hex text field with PreviewPanel and Delete ICON
        //TODO add `new` same as above but with `add` icon
        group.getColors()
                .stream()
                .map(this::createColorIndicator)
                .forEach(colorIndicator -> colorPickerContainer.add(colorIndicator, LEFT_ALIGNMENT));

        add(centerContainer);
    }

    private JLabel createColorIndicator(Color color) {
        JLabel colorIndicator = new JLabel(EDIT_ICON);
        colorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, color));
        colorIndicator.setToolTipText("Edit tile color");
        configureMouseListener(colorIndicator,
                ignored -> editColor(colorIndicator, color),
                ignored -> colorIndicator.setIcon(EDIT_HOVER_ICON),
                ignored -> colorIndicator.setIcon(EDIT_ICON)
        );
        return colorIndicator;
    }


    private void save() {
        group.setLabel(labelInput.getText());
        plugin.saveGroups();

        labelInput.setEditable(false);
        showRenameAndDeleteActions();
        requestFocusInWindow();
    }

    private void cancel() {
        labelInput.setEditable(false);
        labelInput.setText(group.getLabel());
        showRenameAndDeleteActions();
        requestFocusInWindow();
    }

    private void showSaveAndCancelActions() {
        saveLabel.setVisible(true);
        cancelLabel.setVisible(true);
        renameLabel.setVisible(false);
        deleteLabel.setVisible(false);

        labelInput.getTextField().requestFocusInWindow();
        labelInput.getTextField().selectAll();
    }

    private void showRenameAndDeleteActions() {
        renameLabel.setVisible(true);
        deleteLabel.setVisible(true);
        saveLabel.setVisible(false);
        cancelLabel.setVisible(false);
    }

    private void confirmDeleteGroup() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete this group and all grouped tiles?",
                "Warning", JOptionPane.OK_CANCEL_OPTION);

        if (confirm == 0) {
            plugin.removeGroup(group);
        }
    }

    private void editColor(JLabel colorPickerLabel, Color previousColor) {
        RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this),
                previousColor,
                group.getLabel() + " Border",
                false);
        colorPicker.setLocationRelativeTo(this);
        colorPicker.setOnClose(newColor -> {
            group.getColors().remove(previousColor);
            group.getColors().add(newColor);
            plugin.saveGroups();
            colorPickerLabel.setBorder(new MatteBorder(0, 0, 3, 0, newColor));
        });
        colorPicker.setVisible(true);
    }
}
