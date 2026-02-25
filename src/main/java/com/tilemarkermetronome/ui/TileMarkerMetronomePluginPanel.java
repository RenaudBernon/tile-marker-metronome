package com.tilemarkermetronome.ui;

import com.tilemarkermetronome.TileMarkerMetronomePlugin;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import static com.tilemarkermetronome.ui.util.ComponentUtil.configureMouseListener;
import static com.tilemarkermetronome.ui.util.UIConstants.*;
import static com.tilemarkermetronome.ui.util.UIStyler.*;

public class TileMarkerMetronomePluginPanel extends PluginPanel {
    private static final BufferedImage ADD_IMAGE = ImageUtil.loadImageResource(TileMarkerMetronomePlugin.class, "add_icon.png");
    private static final ImageIcon ADD_ICON = new ImageIcon(ADD_IMAGE);
    private static final ImageIcon ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(ADD_IMAGE, -100));

    private final JLabel createGroup = new JLabel(ADD_ICON);
    private final JLabel title = styledLabel("Tile marker metronome groups");
    private final PluginErrorPanel noGroupsPanel = new PluginErrorPanel();
    private final JPanel groupsView = new JPanel(new GridBagLayout());

    private final TileMarkerMetronomePlugin plugin;

    public TileMarkerMetronomePluginPanel(TileMarkerMetronomePlugin tileMarkerMetronomePlugin) {
        this.plugin = tileMarkerMetronomePlugin;

        setLayout(new BorderLayout());
        setBorder(PANEL_BORDER);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new javax.swing.border.EmptyBorder(MINIMAL_PADDING, 0, SMALL_PADDING, 0));
        stylePanel(northPanel);
        northPanel.add(title, BorderLayout.WEST);
        northPanel.add(createGroup, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        stylePanel(centerPanel);

        stylePanel(groupsView);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        noGroupsPanel.setContent("Tile Marker Metronome", "Create tile group");
        noGroupsPanel.setVisible(false);

        groupsView.add(noGroupsPanel, constraints);
        constraints.gridy++;

        createGroup.setToolTipText("Create tile group");
        configureMouseListener(createGroup,
                ignored -> plugin.addGroup(),
                ignored -> createGroup.setIcon(ADD_HOVER_ICON),
                ignored -> createGroup.setIcon(ADD_ICON)
        );

        centerPanel.add(groupsView, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    public void rebuild() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        groupsView.removeAll();

        plugin.getGroups()
                .forEach(group -> {
                            groupsView.add(new TileMarkerGroupPanel(plugin, group), constraints);
                            constraints.gridy++;
                            groupsView.add(Box.createRigidArea(new Dimension(0, SPACING_BETWEEN_GROUPS)), constraints);
                            constraints.gridy++;
                        }
                );

        boolean empty = constraints.gridy == 0;
        noGroupsPanel.setVisible(empty);
        title.setVisible(!empty);

        groupsView.add(noGroupsPanel, constraints);
        constraints.gridy++;

        repaint();
        revalidate();
    }
}
