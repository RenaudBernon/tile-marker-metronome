package com.tilemarkermetronome.ui;

import com.tilemarkermetronome.TileMarkerMetronomeGroup;
import com.tilemarkermetronome.TileMarkerMetronomeGroup.AnimationType;
import com.tilemarkermetronome.TileMarkerMetronomePlugin;
import com.tilemarkermetronome.ui.util.HotkeyButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.NumberFormatter;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import static com.tilemarkermetronome.ui.util.ComponentUtil.configureMouseListener;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.WEST;
import static java.awt.Color.GRAY;
import static java.awt.Color.ORANGE;
import static java.awt.Color.WHITE;
import static java.awt.FlowLayout.LEFT;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static net.runelite.client.ui.ColorScheme.DARKER_GRAY_COLOR;
import static net.runelite.client.ui.ColorScheme.LIGHT_GRAY_COLOR;
import static net.runelite.client.ui.ColorScheme.PROGRESS_COMPLETE_COLOR;
import static net.runelite.client.ui.ColorScheme.PROGRESS_ERROR_COLOR;
import static net.runelite.client.ui.FontManager.getRunescapeFont;

public class TileMarkerGroupPanel extends JPanel {
    private static final Border LABEL_BOTTOM_BORDER = BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR);

    private static final BufferedImage DELETE_IMAGE = ImageUtil.loadImageResource(TileMarkerMetronomePlugin.class, "delete_icon.png");
    private static final ImageIcon DELETE_ICON = new ImageIcon(DELETE_IMAGE);
    private static final ImageIcon DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(DELETE_IMAGE, -100));

    private static final BufferedImage EDIT_IMAGE = ImageUtil.loadImageResource(TileMarkerMetronomePlugin.class, "edit_icon.png");
    private static final ImageIcon EDIT_ICON = new ImageIcon(EDIT_IMAGE);
    private static final ImageIcon EDIT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(EDIT_IMAGE, -100));

    private static final BufferedImage ADD_IMAGE = ImageUtil.loadImageResource(TileMarkerMetronomePlugin.class, "add_icon.png");
    private static final ImageIcon ADD_ICON = new ImageIcon(ADD_IMAGE);
    private static final ImageIcon ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(ADD_IMAGE, -100));

    private static final BufferedImage VISIBLE_IMAGE = ImageUtil.loadImageResource(TileMarkerMetronomePlugin.class, "visible_icon.png");
    private static final ImageIcon VISIBLE_ICON = new ImageIcon(VISIBLE_IMAGE);
    private static final ImageIcon VISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(VISIBLE_IMAGE, -100));

    private static final BufferedImage INVISIBLE_IMAGE = ImageUtil.loadImageResource(TileMarkerMetronomePlugin.class, "invisible_icon.png");
    private static final ImageIcon INVISIBLE_ICON = new ImageIcon(INVISIBLE_IMAGE);
    private static final ImageIcon INVISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(INVISIBLE_IMAGE, -100));

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

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(DARKER_GRAY_COLOR);
        if (group.isActive()) {
            setBorder(new MatteBorder(1, 1, 1, 1, ORANGE));
        } else {
            setBorder(new EmptyBorder(1, 1, 1, 1));
        }

        add(createGroupNameComponent());
        add(createGroupConfigComponent());
    }


    private JPanel createGroupNameComponent() {
        JPanel groupNamePanel = new JPanel(new BorderLayout());
        groupNamePanel.setBackground(DARKER_GRAY_COLOR);
        groupNamePanel.setBorder(LABEL_BOTTOM_BORDER);

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
        labelInput.getTextField().setForeground(WHITE);
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

        groupNamePanel.add(labelInput, CENTER);
        groupNamePanel.add(labelActionsContainer, EAST);
        return groupNamePanel;
    }

    private JPanel createGroupConfigComponent() {
        JPanel groupConfigPanel = new JPanel();
        groupConfigPanel.setLayout(new BoxLayout(groupConfigPanel, BoxLayout.Y_AXIS));
        groupConfigPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        groupConfigPanel.add(createColorConfigPanel());
        groupConfigPanel.add(createVisibilityConfigPanel());
        groupConfigPanel.add(createSettingsConfigPanel());

        return groupConfigPanel;
    }

    private JPanel createSettingsConfigPanel() {
        JPanel settingsConfigPanel = new JPanel();
        settingsConfigPanel.setLayout(new BoxLayout(settingsConfigPanel, BoxLayout.Y_AXIS));
        settingsConfigPanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        settingsConfigPanel.setBackground(DARKER_GRAY_COLOR);

        settingsConfigPanel.add(createHotkeysPanel());
        settingsConfigPanel.add(createRenderTypePanel());
        settingsConfigPanel.add(createTickCounterPanel());
        settingsConfigPanel.add(createFillOpacityPanel());
        settingsConfigPanel.add(createBorderWidthPanel());

        return settingsConfigPanel;
    }

    private JPanel createBorderWidthPanel() {
        JPanel borderWidthPanel = new JPanel(new GridLayout(1, 2));
        borderWidthPanel.setBorder(new EmptyBorder(1, 0, 0, 0));
        borderWidthPanel.setBackground(DARKER_GRAY_COLOR);

        JLabel borderWidthLabel = new JLabel("Border width");
        borderWidthLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        borderWidthLabel.setBackground(DARKER_GRAY_COLOR);

        JSpinner borderWidthSpinner = new JSpinner();
        borderWidthSpinner.setModel(new SpinnerNumberModel(group.getBorderWidth(), 0, 50, 0.1));
        JFormattedTextField txt = ((JSpinner.NumberEditor) borderWidthSpinner.getEditor()).getTextField();
        NumberFormatter formatter = (NumberFormatter) txt.getFormatter();
        formatter.setFormat(new DecimalFormat("0.0"));
        formatter.setAllowsInvalid(false);

        borderWidthSpinner.addChangeListener(action -> updateBorderWidth(borderWidthSpinner));

        borderWidthPanel.add(borderWidthLabel);
        borderWidthPanel.add(borderWidthSpinner);
        return borderWidthPanel;
    }

    private JPanel createFillOpacityPanel() {
        JPanel fillOpacityPanel = new JPanel(new GridLayout(1, 2));
        fillOpacityPanel.setBorder(new EmptyBorder(1, 0, 0, 0));
        fillOpacityPanel.setBackground(DARKER_GRAY_COLOR);

        JLabel fillOpacityLabel = new JLabel("Fill opacity");
        fillOpacityLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        fillOpacityLabel.setBackground(DARKER_GRAY_COLOR);

        JSpinner fillOpacitySpinner = new JSpinner();
        fillOpacitySpinner.setModel(new SpinnerNumberModel(group.getFillOpacity(), 0, 255, 10));
        JFormattedTextField txt = ((JSpinner.NumberEditor) fillOpacitySpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        fillOpacitySpinner.addChangeListener(action -> updateFillOpacity(fillOpacitySpinner));

        fillOpacityPanel.add(fillOpacityLabel);
        fillOpacityPanel.add(fillOpacitySpinner);
        return fillOpacityPanel;
    }

    private JPanel createTickCounterPanel() {
        JPanel tickCounterPanel = new JPanel(new GridLayout(1, 2));
        tickCounterPanel.setBorder(new EmptyBorder(1, 0, 0, 0));
        tickCounterPanel.setBackground(DARKER_GRAY_COLOR);

        JLabel tickCounterLabel = new JLabel("Tick counter");
        tickCounterLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        tickCounterLabel.setBackground(DARKER_GRAY_COLOR);

        JSpinner tickCounterSpinner = new JSpinner();
        tickCounterSpinner.setModel(new SpinnerNumberModel(group.getTickCounter(), 1, 100, 1));
        JFormattedTextField txt = ((JSpinner.NumberEditor) tickCounterSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        tickCounterSpinner.addChangeListener(action -> updateTickCounter(tickCounterSpinner));

        tickCounterPanel.add(tickCounterLabel);
        tickCounterPanel.add(tickCounterSpinner);
        return tickCounterPanel;
    }

    private JPanel createHotkeysPanel() {
        JPanel hotKeysPanel = new JPanel(new GridLayout(2, 1));

        hotKeysPanel.setBackground(DARKER_GRAY_COLOR);
        hotKeysPanel.setBorder(new EmptyBorder(1, 0, 10, 0));

        JLabel hotkeysLabel = new JLabel("Hotkeys");
        hotkeysLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        hotkeysLabel.setBackground(DARKER_GRAY_COLOR);


        JPanel hotkeysListPanel = new JPanel(new GridLayout(0, 2));
        hotkeysListPanel.setBackground(DARKER_GRAY_COLOR);
        hotkeysListPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel tickResetLabel = new JLabel("Cycle reset");
        tickResetLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        tickResetLabel.setBackground(DARKER_GRAY_COLOR);
        HotkeyButton tickResetHotkeyButton = new HotkeyButton(group.getTickResetHotkey(), group::setTickResetHotkey, false);

        JLabel visibilityLabel = new JLabel("Visibility");
        visibilityLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        visibilityLabel.setBackground(DARKER_GRAY_COLOR);
        HotkeyButton visibilityHotkeyButton = new HotkeyButton(group.getVisibilityHotkey(), group::setVisibilityHotkey, false);

        hotkeysListPanel.add(tickResetLabel);
        hotkeysListPanel.add(tickResetHotkeyButton);
        hotkeysListPanel.add(visibilityLabel);
        hotkeysListPanel.add(visibilityHotkeyButton);

        hotKeysPanel.add(hotkeysLabel);
        hotKeysPanel.add(hotkeysListPanel);
        return hotKeysPanel;
    }

    private JPanel createRenderTypePanel() {
        JPanel renderTypePanel = new JPanel(new GridLayout(1, 2));
        renderTypePanel.setBorder(new EmptyBorder(1, 0, 0, 0));
        renderTypePanel.setBackground(DARKER_GRAY_COLOR);

        JLabel renderTypeLabel = new JLabel("Render Type");
        renderTypeLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        renderTypeLabel.setBackground(DARKER_GRAY_COLOR);

        JComboBox<AnimationType> renderTypeDropdown = new JComboBox<>(AnimationType.values());
        renderTypeDropdown.setSelectedItem(group.getAnimationType());
        renderTypeDropdown.addActionListener(action -> updateRenderType(renderTypeDropdown));

        renderTypePanel.add(renderTypeLabel);
        renderTypePanel.add(renderTypeDropdown);
        return renderTypePanel;
    }

    private JPanel createVisibilityConfigPanel() {
        JPanel renderConfigPanel = new JPanel(new BorderLayout());
        renderConfigPanel.setBorder(new EmptyBorder(8, 10, 8, 0));
        renderConfigPanel.setBackground(DARKER_GRAY_COLOR);

        JButton setActiveButton = new JButton("Activate group");
        setActiveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        setActiveButton.setBackground(DARKER_GRAY_COLOR);

        setActiveButton.setToolTipText("Marking tiles will add them to the active group.");
        configureMouseListener(
                setActiveButton,
                ignored -> activateGroup(),
                ignored -> setForeground(getForeground().darker()),
                ignored -> setForeground(getForeground().brighter())
        );

        JLabel isVisibleIcon = new JLabel(group.isVisible() ? VISIBLE_ICON : INVISIBLE_ICON);
        isVisibleIcon.setBorder(new EmptyBorder(0, 0, 0, 0));
        isVisibleIcon.setBackground(DARKER_GRAY_COLOR);

        configureMouseListener(
                isVisibleIcon,
                ignored -> toggleVisibility(isVisibleIcon),
                ignored -> isVisibleIcon.setIcon(group.isVisible() ? VISIBLE_HOVER_ICON : INVISIBLE_HOVER_ICON),
                ignored -> isVisibleIcon.setIcon(group.isVisible() ? VISIBLE_ICON : INVISIBLE_ICON)
        );

        renderConfigPanel.add(setActiveButton, WEST);
        renderConfigPanel.add(isVisibleIcon, CENTER);
        return renderConfigPanel;
    }

    private JPanel createColorConfigPanel() {
        JPanel colorConfigPanel = new JPanel(new GridLayout(0, 1));
        colorConfigPanel.setBorder(new EmptyBorder(1, 0, 1, 0));
        colorConfigPanel.setBackground(DARKER_GRAY_COLOR);

        JLabel colorLabel = new JLabel("Tile colors");
        colorLabel.setBorder(new EmptyBorder(0, 8, 0, 0));

        colorConfigPanel.add(colorLabel, LEFT_ALIGNMENT);
        group.getColors()
                .stream()
                .map(this::createColorIndicator)
                .forEach(colorIndicator -> colorConfigPanel.add(colorIndicator, LEFT_ALIGNMENT));

        colorConfigPanel.add(createAddColorPanel());
        return colorConfigPanel;
    }

    private JPanel createColorIndicator(Color color) {
        JPanel colorConfigPanel = new JPanel(new BorderLayout(8, 0));
        colorConfigPanel.setBorder(new EmptyBorder(3, 8, 0, 8));
        colorConfigPanel.setBackground(DARKER_GRAY_COLOR);

        JLabel hexValueLabel = new JLabel(ColorUtil.toHexColor(color));
        hexValueLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
        hexValueLabel.setBackground(DARKER_GRAY_COLOR);

        ColorPanel colorPanel = new ColorPanel(color);
        colorPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        colorPanel.setBackground(DARKER_GRAY_COLOR);

        colorPanel.setColor(color);
        colorPanel.setToolTipText("Edit tile color");

        configureMouseListener(colorPanel,
                ignored -> editColor(colorPanel, color),
                ignored -> colorPanel.setColor(ColorUtil.colorWithAlpha(color, color.getAlpha() - 100)),
                ignored -> colorPanel.setColor(color)
        );

        JLabel deleteIcon = new JLabel(DELETE_ICON);
        deleteIcon.setBorder(new EmptyBorder(0, 0, 0, 0));
        deleteIcon.setBackground(DARKER_GRAY_COLOR);
        deleteIcon.setToolTipText("Remove color");

        configureMouseListener(
                deleteIcon,
                ignored -> confirmDeleteColor(color),
                ignored -> deleteIcon.setIcon(DELETE_HOVER_ICON),
                ignored -> deleteIcon.setIcon(DELETE_ICON)
        );

        colorConfigPanel.add(hexValueLabel, WEST);
        colorConfigPanel.add(colorPanel, CENTER);
        colorConfigPanel.add(deleteIcon, EAST);
        return colorConfigPanel;
    }

    private JPanel createAddColorPanel() {
        JPanel addColorPanel = new JPanel(new FlowLayout(LEFT));
        addColorPanel.setBorder(new EmptyBorder(0, 8, 0, 0));
        addColorPanel.setBackground(DARKER_GRAY_COLOR);

        JLabel addColorIcon = new JLabel(ADD_ICON);
        addColorIcon.setBorder(new EmptyBorder(0, 0, 0, 0));
        addColorIcon.setBackground(DARKER_GRAY_COLOR);
        addColorIcon.setToolTipText("Add color");

        configureMouseListener(addColorIcon,
                ignored -> addColor(),
                ignored -> addColorIcon.setIcon(ADD_HOVER_ICON),
                ignored -> addColorIcon.setIcon(ADD_ICON)
        );

        addColorPanel.add(addColorIcon);
        return addColorPanel;
    }

    private void save() {
        group.setLabel(labelInput.getText());
        plugin.saveGroupsAndRebuild();

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

    private void editColor(ColorPanel colorPanel, Color previousColor) {
        RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this),
                previousColor,
                group.getLabel() + " Border",
                false);
        colorPicker.setLocationRelativeTo(this);
        colorPicker.setOnClose(newColor -> {
            List<Color> colors = group.getColors();
            colors.set(colors.indexOf(previousColor), newColor);
            plugin.saveGroupsAndRebuild();
            colorPanel.setColor(newColor);
        });
        colorPicker.setVisible(true);
    }

    private void addColor() {
        RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
                SwingUtilities.windowForComponent(this),
                WHITE,
                group.getLabel() + " Border",
                false);
        colorPicker.setLocationRelativeTo(this);
        colorPicker.setOnClose(newColor -> {
            group.getColors().add(newColor);
            plugin.saveGroupsAndRebuild();
        });
        colorPicker.setVisible(true);
    }

    private void confirmDeleteColor(Color color) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete this color from the group?",
                "Warning", JOptionPane.OK_CANCEL_OPTION);

        if (confirm == 0) {
            group.getColors().remove(color);
        }
        plugin.saveGroupsAndRebuild();
    }

    private void toggleVisibility(JLabel isVisibleIcon) {
        group.setVisible(!group.isVisible());
        isVisibleIcon.setIcon(group.isVisible() ? VISIBLE_ICON : INVISIBLE_ICON);
        plugin.saveGroupsAndRebuild();
    }

    private void activateGroup() {
        plugin.getGroups().forEach(TileMarkerMetronomeGroup::setInactive);
        group.setActive();
        plugin.saveGroupsAndRebuild();
    }

    private void updateRenderType(JComboBox<AnimationType> renderTypeDropdown) {
        group.setAnimationType((AnimationType) renderTypeDropdown.getSelectedItem());
        plugin.saveGroupsAndRebuild();
    }

    private void updateTickCounter(JSpinner tickCounter) {
        group.setTickCounter((int) tickCounter.getValue());
        plugin.saveGroupsAndRebuild();
    }

    private void updateFillOpacity(JSpinner fillOpacity) {
        group.setFillOpacity((int) fillOpacity.getValue());
        plugin.saveGroupsAndRebuild();
    }

    private void updateBorderWidth(JSpinner borderWidth) {
        group.setBorderWidth((double) borderWidth.getValue());
        plugin.saveGroupsAndRebuild();
    }
}
