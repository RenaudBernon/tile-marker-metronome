package com.tilemarkermetronome.ui;

import com.tilemarkermetronome.TileMarkerMetronomePlugin;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import static com.tilemarkermetronome.ui.util.ComponentUtil.configureMouseListener;
import static com.tilemarkermetronome.ui.util.UIConstants.*;
import static com.tilemarkermetronome.ui.util.UIStyler.*;

public class TileMarkerMetronomePluginPanel extends PluginPanel {
    private final PluginErrorPanel noGroupsPanel = new PluginErrorPanel();
    private final JPanel groupsView = new JPanel(new GridBagLayout());
    private final TileMarkerMetronomePlugin plugin;

    public TileMarkerMetronomePluginPanel(TileMarkerMetronomePlugin tileMarkerMetronomePlugin) {
        this.plugin = tileMarkerMetronomePlugin;

        setLayout(new BorderLayout());
        setBorder(PANEL_BORDER);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(MINIMAL_PADDING, 0, SMALL_PADDING, 0));
        stylePanel(northPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, MINIMAL_PADDING, 0));
        stylePanel(buttonPanel);

        JButton createButton = new JButton("Add group");
        JButton importButton = new JButton("Import");
        JButton exportButton = new JButton("Export All");

        styleButton(createButton);
        styleButton(importButton);
        styleButton(exportButton);

        createButton.setToolTipText("Create new group");
        importButton.setToolTipText("Import groups");
        exportButton.setToolTipText("Export all groups");

        createButton.addActionListener(e -> plugin.addGroup());
        importButton.addActionListener(e -> showImportDialog());
        exportButton.addActionListener(e -> showExportDialog());

        buttonPanel.add(createButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton);

        northPanel.add(buttonPanel);

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

        centerPanel.add(groupsView, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void showImportDialog() {
        String json = JOptionPane.showInputDialog(this,
                "Paste group(s) import:",
                "Import Group(s)",
                JOptionPane.PLAIN_MESSAGE);

        if (json != null && !json.trim().isEmpty()) {
            try {
                plugin.importGroups(json);
                JOptionPane.showMessageDialog(this, "Group(s) imported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to import group(s): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showExportDialog() {
        String json = plugin.exportAllGroups();
        JOptionPane.showMessageDialog(this,
                json,
                "Export All Groups",
                JOptionPane.PLAIN_MESSAGE);
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

        groupsView.add(noGroupsPanel, constraints);
        constraints.gridy++;

        repaint();
        revalidate();
    }
}
