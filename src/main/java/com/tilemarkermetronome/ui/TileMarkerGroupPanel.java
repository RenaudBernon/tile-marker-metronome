package com.tilemarkermetronome.ui;

import com.tilemarkermetronome.TileMarkerMetronomeGroup;
import com.tilemarkermetronome.TileMarkerMetronomeGroup.AnimationType;
import com.tilemarkermetronome.TileMarkerMetronomePlugin;
import com.tilemarkermetronome.ui.util.HotkeyButton;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

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
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.NumberFormatter;
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

import static com.tilemarkermetronome.ui.util.ComponentUtil.configureMouseListener;
import static com.tilemarkermetronome.ui.util.UIConstants.ACCENT_COLOR;
import static com.tilemarkermetronome.ui.util.UIConstants.BORDER_WIDTH_MAX;
import static com.tilemarkermetronome.ui.util.UIConstants.BORDER_WIDTH_MIN;
import static com.tilemarkermetronome.ui.util.UIConstants.BORDER_WIDTH_STEP;
import static com.tilemarkermetronome.ui.util.UIConstants.BOTTOM_BORDER;
import static com.tilemarkermetronome.ui.util.UIConstants.ERROR_COLOR;
import static com.tilemarkermetronome.ui.util.UIConstants.FILL_OPACITY_MAX;
import static com.tilemarkermetronome.ui.util.UIConstants.FILL_OPACITY_MIN;
import static com.tilemarkermetronome.ui.util.UIConstants.FILL_OPACITY_STEP;
import static com.tilemarkermetronome.ui.util.UIConstants.GROUP_BORDER;
import static com.tilemarkermetronome.ui.util.UIConstants.LABEL_HEIGHT;
import static com.tilemarkermetronome.ui.util.UIConstants.LABEL_PADDING;
import static com.tilemarkermetronome.ui.util.UIConstants.MINIMAL_PADDING;
import static com.tilemarkermetronome.ui.util.UIConstants.NO_BORDER;
import static com.tilemarkermetronome.ui.util.UIConstants.PRIMARY_FOREGROUND;
import static com.tilemarkermetronome.ui.util.UIConstants.SECONDARY_FOREGROUND;
import static com.tilemarkermetronome.ui.util.UIConstants.SMALL_PADDING;
import static com.tilemarkermetronome.ui.util.UIConstants.STANDARD_PADDING;
import static com.tilemarkermetronome.ui.util.UIConstants.SUCCESS_COLOR;
import static com.tilemarkermetronome.ui.util.UIConstants.TICK_COUNTER_MAX;
import static com.tilemarkermetronome.ui.util.UIConstants.TICK_COUNTER_MIN;
import static com.tilemarkermetronome.ui.util.UIConstants.TICK_COUNTER_STEP;
import static com.tilemarkermetronome.ui.util.UIStyler.styleButton;
import static com.tilemarkermetronome.ui.util.UIStyler.styleComboBox;
import static com.tilemarkermetronome.ui.util.UIStyler.stylePanel;
import static com.tilemarkermetronome.ui.util.UIStyler.styleSectionPanel;
import static com.tilemarkermetronome.ui.util.UIStyler.styleSpinner;
import static com.tilemarkermetronome.ui.util.UIStyler.styledLabel;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.WEST;
import static java.awt.Color.WHITE;
import static java.awt.FlowLayout.LEFT;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static net.runelite.client.ui.FontManager.getRunescapeFont;

public class TileMarkerGroupPanel extends JPanel {
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
    private final JLabel visibilityLabel = new JLabel(">");

    private final FlatTextField labelInput = new FlatTextField();

    private final TileMarkerMetronomePlugin plugin;
    private final TileMarkerMetronomeGroup group;

    public TileMarkerGroupPanel(TileMarkerMetronomePlugin plugin, TileMarkerMetronomeGroup group) {
        this.plugin = plugin;
        this.group = group;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        stylePanel(this);
        if (group.isActive()) {
            setBorder(new MatteBorder(1, 1, 1, 1, ACCENT_COLOR));
        } else {
            setBorder(GROUP_BORDER);
        }

        add(createGroupNameComponent());
        add(createGroupConfigComponent());
    }

    private JPanel createGroupNameComponent() {
        JPanel groupNamePanel = new JPanel(new BorderLayout());
        styleSectionPanel(groupNamePanel);

        JPanel labelActionsContainer = new JPanel(new BorderLayout(MINIMAL_PADDING, 0));
        JPanel labelActions = new JPanel(new FlowLayout());
        labelActionsContainer.setBorder(NO_BORDER);
        stylePanel(labelActionsContainer);
        labelActions.setBorder(new EmptyBorder(0, 0, 0, STANDARD_PADDING));
        stylePanel(labelActions);

        labelInput.setText(group.getLabel());
        labelInput.setBorder(null);
        labelInput.setEditable(false);
        stylePanel(labelInput);
        labelInput.setPreferredSize(new Dimension(0, LABEL_HEIGHT));
        labelInput.getTextField().setForeground(PRIMARY_FOREGROUND);
        labelInput.getTextField().setBorder(new EmptyBorder(0, STANDARD_PADDING, 0, 0));
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
        saveLabel.setForeground(SUCCESS_COLOR);
        configureMouseListener(
                saveLabel,
                ignored -> save(),
                ignored -> saveLabel.setForeground(SUCCESS_COLOR.darker()),
                ignored -> saveLabel.setForeground(SUCCESS_COLOR));

        cancelLabel.setVisible(false);
        cancelLabel.setFont(getRunescapeFont());
        cancelLabel.setForeground(ERROR_COLOR);
        configureMouseListener(
                cancelLabel,
                ignored -> cancel(),
                ignored -> cancelLabel.setForeground(ERROR_COLOR.darker()),
                ignored -> cancelLabel.setForeground(ERROR_COLOR));

        renameLabel.setVisible(true);
        renameLabel.setForeground(SECONDARY_FOREGROUND);
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

        configureMouseListener(
                visibilityLabel,
                ignored -> collapseGroup(),
                ignored -> {
                },
                ignored -> {
                }
        );

        labelActions.add(saveLabel);
        labelActions.add(cancelLabel);
        labelActions.add(renameLabel);
        labelActions.add(deleteLabel);
        labelActions.add(visibilityLabel);
        labelActionsContainer.add(labelActions);

        groupNamePanel.add(labelInput, CENTER);
        groupNamePanel.add(labelActionsContainer, EAST);
        return groupNamePanel;
    }

    private JPanel createGroupConfigComponent() {
        JPanel groupConfigPanel = new JPanel();
        groupConfigPanel.setLayout(new BoxLayout(groupConfigPanel, BoxLayout.Y_AXIS));
        groupConfigPanel.setBorder(NO_BORDER);
        stylePanel(groupConfigPanel);
        groupConfigPanel.add(createColorConfigPanel());
        groupConfigPanel.add(createVisibilityConfigPanel());
        groupConfigPanel.add(createSettingsConfigPanel());
        groupConfigPanel.setVisible(!group.isCollapsed());

        return groupConfigPanel;
    }

    private JPanel createSettingsConfigPanel() {
        JPanel settingsConfigPanel = new JPanel();
        settingsConfigPanel.setLayout(new BoxLayout(settingsConfigPanel, BoxLayout.Y_AXIS));
        settingsConfigPanel.setBorder(new EmptyBorder(MINIMAL_PADDING, STANDARD_PADDING, 0, 0));
        stylePanel(settingsConfigPanel);

        settingsConfigPanel.add(createHotkeysPanel());
        settingsConfigPanel.add(createRenderTypePanel());
        settingsConfigPanel.add(createTickCounterPanel());
        settingsConfigPanel.add(createFillOpacityPanel());
        settingsConfigPanel.add(createBorderWidthPanel());

        return settingsConfigPanel;
    }

    private JPanel createBorderWidthPanel() {
        JPanel borderWidthPanel = new JPanel(new GridLayout(1, 2));
        borderWidthPanel.setBorder(new EmptyBorder(MINIMAL_PADDING, 0, 0, 0));
        stylePanel(borderWidthPanel);

        JLabel borderWidthLabel = styledLabel("Border width");
        borderWidthLabel.setBorder(NO_BORDER);

        JSpinner borderWidthSpinner = new JSpinner();
        borderWidthSpinner.setModel(new SpinnerNumberModel(group.getBorderWidth(), BORDER_WIDTH_MIN, BORDER_WIDTH_MAX, BORDER_WIDTH_STEP));
        JFormattedTextField txt = ((JSpinner.NumberEditor) borderWidthSpinner.getEditor()).getTextField();
        NumberFormatter formatter = (NumberFormatter) txt.getFormatter();
        formatter.setFormat(new DecimalFormat("0.0"));
        formatter.setAllowsInvalid(false);
        styleSpinner(borderWidthSpinner);

        borderWidthSpinner.addChangeListener(action -> updateBorderWidth(borderWidthSpinner));

        borderWidthPanel.add(borderWidthLabel);
        borderWidthPanel.add(borderWidthSpinner);
        return borderWidthPanel;
    }

    private JPanel createFillOpacityPanel() {
        JPanel fillOpacityPanel = new JPanel(new GridLayout(1, 2));
        fillOpacityPanel.setBorder(new EmptyBorder(MINIMAL_PADDING, 0, 0, 0));
        stylePanel(fillOpacityPanel);

        JLabel fillOpacityLabel = styledLabel("Fill opacity");
        fillOpacityLabel.setBorder(NO_BORDER);

        JSpinner fillOpacitySpinner = new JSpinner();
        fillOpacitySpinner.setModel(new SpinnerNumberModel(group.getFillOpacity(), FILL_OPACITY_MIN, FILL_OPACITY_MAX, FILL_OPACITY_STEP));
        JFormattedTextField txt = ((JSpinner.NumberEditor) fillOpacitySpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        styleSpinner(fillOpacitySpinner);

        fillOpacitySpinner.addChangeListener(action -> updateFillOpacity(fillOpacitySpinner));

        fillOpacityPanel.add(fillOpacityLabel);
        fillOpacityPanel.add(fillOpacitySpinner);
        return fillOpacityPanel;
    }

    private JPanel createTickCounterPanel() {
        JPanel tickCounterPanel = new JPanel(new GridLayout(1, 2));
        tickCounterPanel.setBorder(new EmptyBorder(MINIMAL_PADDING, 0, 0, 0));
        stylePanel(tickCounterPanel);

        JLabel tickCounterLabel = styledLabel("Tick counter");
        tickCounterLabel.setBorder(NO_BORDER);

        JSpinner tickCounterSpinner = new JSpinner();
        tickCounterSpinner.setModel(new SpinnerNumberModel(group.getTickCounter(), TICK_COUNTER_MIN, TICK_COUNTER_MAX, TICK_COUNTER_STEP));
        JFormattedTextField txt = ((JSpinner.NumberEditor) tickCounterSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        styleSpinner(tickCounterSpinner);

        tickCounterSpinner.addChangeListener(action -> updateTickCounter(tickCounterSpinner));

        tickCounterPanel.add(tickCounterLabel);
        tickCounterPanel.add(tickCounterSpinner);
        return tickCounterPanel;
    }

    private JPanel createHotkeysPanel() {
        JPanel hotKeysPanel = new JPanel(new GridLayout(2, 1));
        stylePanel(hotKeysPanel);
        hotKeysPanel.setBorder(new EmptyBorder(MINIMAL_PADDING, 0, SMALL_PADDING, 0));

        JLabel hotkeysLabel = styledLabel("Hotkeys");
        hotkeysLabel.setBorder(NO_BORDER);

        JPanel hotkeysListPanel = new JPanel(new GridLayout(0, 2));
        stylePanel(hotkeysListPanel);
        hotkeysListPanel.setBorder(NO_BORDER);

        JLabel tickResetLabel = styledLabel("Cycle reset");
        tickResetLabel.setBorder(NO_BORDER);
        HotkeyButton tickResetHotkeyButton = new HotkeyButton(group.getTickResetHotkey(), group::setTickResetHotkey, false);

        JLabel visibilityLabel = styledLabel("Visibility");
        visibilityLabel.setBorder(NO_BORDER);
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
        renderTypePanel.setBorder(new EmptyBorder(MINIMAL_PADDING, 0, 0, 0));
        stylePanel(renderTypePanel);

        JLabel renderTypeLabel = styledLabel("Animation");
        renderTypeLabel.setBorder(NO_BORDER);

        JComboBox<AnimationType> renderTypeDropdown = new JComboBox<>(AnimationType.values());
        renderTypeDropdown.setSelectedItem(group.getAnimationType());
        styleComboBox(renderTypeDropdown);
        renderTypeDropdown.addActionListener(action -> updateRenderType(renderTypeDropdown));

        renderTypePanel.add(renderTypeLabel);
        renderTypePanel.add(renderTypeDropdown);
        return renderTypePanel;
    }

    private JPanel createVisibilityConfigPanel() {
        JPanel renderConfigPanel = new JPanel(new BorderLayout());
        renderConfigPanel.setBorder(new EmptyBorder(STANDARD_PADDING, STANDARD_PADDING, 0, 0));
        stylePanel(renderConfigPanel);

        JButton setActiveButton = new JButton("Activate group");
        styleButton(setActiveButton);
        setActiveButton.setToolTipText("Marking tiles will add them to the active group.");
        configureMouseListener(
                setActiveButton,
                ignored -> activateGroup(),
                ignored -> setForeground(getForeground().darker()),
                ignored -> setForeground(getForeground().brighter())
        );

        JLabel isVisibleIcon = new JLabel(group.isVisible() ? VISIBLE_ICON : INVISIBLE_ICON);
        isVisibleIcon.setBorder(NO_BORDER);

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
        colorConfigPanel.setBorder(new EmptyBorder(MINIMAL_PADDING, 0, MINIMAL_PADDING, 0));
        stylePanel(colorConfigPanel);

        JLabel colorLabel = styledLabel("Tile colors");
        colorLabel.setBorder(LABEL_PADDING);
        colorConfigPanel.add(colorLabel, LEFT_ALIGNMENT);

        JPanel colorGridLayout = new JPanel();
        colorGridLayout.setLayout(new FlowLayout(FlowLayout.LEFT, MINIMAL_PADDING, MINIMAL_PADDING));
        colorGridLayout.setBorder(LABEL_PADDING);
        stylePanel(colorGridLayout);
        colorConfigPanel.add(colorGridLayout);

        group.getColors()
                .stream()
                .map(this::createColorIndicator)
                .forEach(colorIndicator -> colorGridLayout.add(colorIndicator, LEFT_ALIGNMENT));
        colorGridLayout.add(createAddColorPanel());

        colorConfigPanel.setBorder(BOTTOM_BORDER);
        return colorConfigPanel;
    }

    private JPanel createColorIndicator(Color color) {
        ColorPanel colorPanel = new ColorPanel(color);
        colorPanel.setBorder(NO_BORDER);
        stylePanel(colorPanel);

        colorPanel.setColor(color);
        colorPanel.setToolTipText("Click to edit tile color.\n Shift click to remove.");

        configureMouseListener(colorPanel,
                mouseEvent -> {
                    if (mouseEvent.isShiftDown()) {
                        deleteColor(color);
                    } else {
                        editColor(colorPanel, color);
                    }
                },
                ignored -> colorPanel.setColor(ColorUtil.colorWithAlpha(color, color.getAlpha() - 100)),
                ignored -> colorPanel.setColor(color)
        );
        return colorPanel;
    }

    private JPanel createAddColorPanel() {
        JPanel addColorPanel = new JPanel(new FlowLayout(LEFT));
        addColorPanel.setBorder(LABEL_PADDING);
        stylePanel(addColorPanel);

        JLabel addColorIcon = new JLabel(ADD_ICON);
        addColorIcon.setBorder(NO_BORDER);
        addColorIcon.setToolTipText("Add color");

        configureMouseListener(addColorIcon,
                ignored -> addColor(),
                ignored -> addColorIcon.setIcon(ADD_HOVER_ICON),
                ignored -> addColorIcon.setIcon(ADD_ICON)
        );

        addColorPanel.add(addColorIcon);
        return addColorPanel;
    }

    private void collapseGroup() {
        group.setCollapsed(!group.isCollapsed());
        plugin.saveGroupsAndRebuild();
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

    private void deleteColor(Color color) {
        group.getColors().remove(color);
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
