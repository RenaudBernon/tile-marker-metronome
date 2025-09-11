package com.tilemarkermetronome;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import com.tilemarkermetronome.ui.TileMarkerMetronomePluginPanel;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.SwingUtilities;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.tilemarkermetronome.TileMarkerMetronomeGroup.AnimationType.DISABLED;


@PluginDescriptor(name = "Tile Marker Metronome")
public class TileMarkerMetronomePlugin extends Plugin implements KeyListener {
    private static final String CONFIG_GROUP = "tileMarkerMetronome";
    private static final String CONFIG_METRONOME_GROUPS_KEY = "metronomeGroups";
    private static final String WALK_HERE = "Walk here";
    private static final String REGION_PREFIX = "region_";
    private static final String PANEL_ICON = "panel_icon.png";
    private static final String PLUGIN_NAME = "Tile Marker Metronome";

    @Getter(AccessLevel.PACKAGE)
    private final List<TileMarkerMetronomeGroup> tileMarkerMetronomeGroups = new ArrayList<>();
    private TileMarkerMetronomePluginPanel pluginPanel;
    private NavigationButton navigationButton;
    private int currentTick;

    @Inject
    private Client client;
    @Inject
    private ConfigManager configManager;
    @Inject
    private TileMarkerMetronomeConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TileMarkerMetronomeOverlay overlay;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private EventBus eventBus;
    @Inject
    private ChatboxPanelManager chatboxPanelManager;
    @Getter
    @Inject
    private ColorPickerManager colorPickerManager;
    @Inject
    private Gson gson;
    @Inject
    private KeyManager keyManager;

    @Override
    public void startUp() {
        overlayManager.add(overlay);
        loadGroups();
        loadPoints();

        pluginPanel = new TileMarkerMetronomePluginPanel(this);

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), PANEL_ICON);
        navigationButton = NavigationButton.builder().tooltip(PLUGIN_NAME).icon(icon).priority(10000).panel(pluginPanel).build();
        clientToolbar.addNavigation(navigationButton);
        SwingUtilities.invokeLater(() -> pluginPanel.rebuild());
        keyManager.registerKeyListener(this);
    }

    @Override
    public void shutDown() {
        overlayManager.remove(overlay);
        tileMarkerMetronomeGroups.clear();
        clientToolbar.removeNavigation(navigationButton);
        keyManager.unregisterKeyListener(this);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick) {
        tileMarkerMetronomeGroups.forEach(this::tickGroup);
        currentTick++;
    }

    void tickGroup(TileMarkerMetronomeGroup group) {
        if (group.getAnimationType() == DISABLED) {
            return;
        }
        group.incrementTick();
        if (group.getCurrentTick() % group.getTickCounter() == 0) {
            group.setNextColor();
        }
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        loadGroups();
        loadPoints();
        SwingUtilities.invokeLater(() -> pluginPanel.rebuild());
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        loadPoints();
        SwingUtilities.invokeLater(() -> pluginPanel.rebuild());
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        final boolean hotKeyPressed = client.isKeyPressed(KeyCode.KC_SHIFT);
        if (hotKeyPressed && event.getOption().equals(WALK_HERE)) {
            Tile selectedSceneTile = client.getSelectedSceneTile();

            if (selectedSceneTile == null) {
                return;
            }

            final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, selectedSceneTile.getLocalLocation(), client.getPlane());
            Optional<TileMarkerMetronomePoint> markedTile = getActiveGroup()
                    .getTileMarkerMetronomePoints()
                    .stream()
                    .filter(tileMarker -> tileMarker.isSameAs(worldPoint))
                    .findFirst();

            client.createMenuEntry(-1)
                    .setOption("Add to group")
                    .setTarget("Tile")
                    .setType(MenuAction.RUNELITE)
                    .onClick(e -> {
                        Tile target = client.getSelectedSceneTile();
                        if (target != null) {
                            markTile(target.getLocalLocation());
                        }
                    });
            markedTile.ifPresent(tileMarkerMetronomePoint -> {
                client.createMenuEntry(-1)
                        .setOption("Remove from group")
                        .setTarget("Tile")
                        .setType(MenuAction.RUNELITE)
                        .onClick(e -> {
                            Tile target = client.getSelectedSceneTile();
                            if (target != null) {
                                unmarkTile(target.getLocalLocation());
                            }
                        });
                client.createMenuEntry(-1)
                        .setOption("Label")
                        .setTarget("Tile")
                        .setType(MenuAction.RUNELITE)
                        .onClick(e -> labelTile(tileMarkerMetronomePoint));
            });
        }
    }

    public void addGroup() {
        tileMarkerMetronomeGroups.forEach(group -> group.setActive(false));
        tileMarkerMetronomeGroups.add(new TileMarkerMetronomeGroup("New group", config, true, true));
        saveGroupsAndRebuild();
    }

    public void removeGroup(TileMarkerMetronomeGroup group) {
        group.getTileMarkerMetronomePoints().clear();
        removePoints(group.getId());
        tileMarkerMetronomeGroups.remove(group);
        saveGroupsAndRebuild();
    }

    public List<TileMarkerMetronomeGroup> getGroups() {
        return tileMarkerMetronomeGroups;
    }

    public void saveGroupsAndRebuild() {
        saveGroups();
        SwingUtilities.invokeLater(() -> pluginPanel.rebuild());
    }

    public void saveGroups() {
        if (tileMarkerMetronomeGroups.isEmpty()) {
            configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_METRONOME_GROUPS_KEY);
        } else {
            String json = gson.toJson(tileMarkerMetronomeGroups);
            configManager.setConfiguration(CONFIG_GROUP, CONFIG_METRONOME_GROUPS_KEY, json);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        tileMarkerMetronomeGroups.forEach(group -> {
            if (group.getTickResetHotkey().matches(e)) {
                group.setCurrentColor(0);
                group.setCurrentTick(0);
            }
            if (group.getVisibilityHotkey().matches(e)) {
                group.toggleVisibility();
            }
        });
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void markTile(LocalPoint localPoint) {
        if (localPoint == null) {
            return;
        }

        WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, localPoint);

        int regionId = worldPoint.getRegionID();
        UUID groupId = getActiveGroup().getId();
        TileMarkerMetronomePoint point = new TileMarkerMetronomePoint(regionId, worldPoint, null);

        List<TileMarkerMetronomePoint> currentGroupTileMarkerMetronomePoints = getActiveGroup().getTileMarkerMetronomePoints();
        currentGroupTileMarkerMetronomePoints.add(point);

        savePoints(groupId, regionId);
        loadPoints();
    }

    private void unmarkTile(LocalPoint localPoint) {
        if (localPoint == null) {
            return;
        }

        WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, localPoint);

        int regionId = worldPoint.getRegionID();
        UUID groupId = getActiveGroup().getId();
        TileMarkerMetronomePoint point = new TileMarkerMetronomePoint(regionId, worldPoint, null);

        List<TileMarkerMetronomePoint> currentGroupTileMarkerMetronomePoints = getActiveGroup().getTileMarkerMetronomePoints();
        currentGroupTileMarkerMetronomePoints.stream()
                .filter(p -> p.isSameAs(point.getWorldPoint()))
                .forEach(currentGroupTileMarkerMetronomePoints::remove);

        savePoints(groupId, regionId);
        loadPoints();
    }

    private void labelTile(TileMarkerMetronomePoint existing) {
        chatboxPanelManager.openTextInput("Tile label")
                .value(Optional.ofNullable(existing.getLabel())
                        .orElse(""))
                .onDone(existing::setLabel)
                .build();
    }

    private void savePoints(UUID groupId, int regionId) {
        Optional<List<TileMarkerMetronomePoint>> groupPoints = getGroups()
                .stream()
                .filter(group -> group.getId().equals(groupId))
                .map(TileMarkerMetronomeGroup::getTileMarkerMetronomePoints)
                .findFirst();

        groupPoints.ifPresentOrElse(points -> {
                    String json = gson.toJson(points);
                    configManager.setConfiguration(CONFIG_GROUP, createConfigKey(groupId, regionId), json);
                },
                () -> configManager.unsetConfiguration(CONFIG_GROUP, createConfigKey(groupId, regionId)));
    }

    private void loadPoints() {
        int[] regions = client.getMapRegions();

        if (regions == null) {
            return;
        }

        getGroups()
                .forEach(group -> {
                    group.getTileMarkerMetronomePoints().clear();
                    group.getTileMarkerMetronomePoints().addAll(
                            Arrays.stream(regions)
                                    .mapToObj(regionId -> loadPoints(group.getId(), regionId))
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toList()));
                });
    }

    private List<TileMarkerMetronomePoint> loadPoints(UUID groupId, int regionId) {
        String json = configManager.getConfiguration(CONFIG_GROUP, groupId + "_" + REGION_PREFIX + regionId);
        if (Strings.isNullOrEmpty(json)) {
            return Collections.emptyList();
        }

        return gson.fromJson(json, new TypeToken<List<TileMarkerMetronomePoint>>() {
        }.getType());
    }

    private void removePoints(UUID groupId) {
        configManager.unsetConfiguration(CONFIG_GROUP, groupId.toString());
    }

    private void loadGroups() {
        String json = configManager.getConfiguration(CONFIG_GROUP, CONFIG_METRONOME_GROUPS_KEY);
        tileMarkerMetronomeGroups.clear();
        if (Strings.isNullOrEmpty(json)) {
            tileMarkerMetronomeGroups.add(new TileMarkerMetronomeGroup("Group 1", config, true, true));
            saveGroups();
        } else {
            List<TileMarkerMetronomeGroup> savedGroups = gson.fromJson(json, new TypeToken<ArrayList<TileMarkerMetronomeGroup>>() {
            }.getType());

            //Add default for older saved groups
            savedGroups.forEach(tileMarkerMetronomeGroup -> {
                if (tileMarkerMetronomeGroup.getTickResetHotkey() == null) {
                    tileMarkerMetronomeGroup.setTickResetHotkey(Keybind.NOT_SET);
                }
                if (tileMarkerMetronomeGroup.getVisibilityHotkey() == null) {
                    tileMarkerMetronomeGroup.setVisibilityHotkey(Keybind.NOT_SET);
                }
            });

            tileMarkerMetronomeGroups.addAll(savedGroups);
        }
    }

    private TileMarkerMetronomeGroup getActiveGroup() {
        return tileMarkerMetronomeGroups
                .stream()
                .filter(TileMarkerMetronomeGroup::isActive)
                .findFirst()
                .orElseGet(() -> {
                            TileMarkerMetronomeGroup firstGroup = tileMarkerMetronomeGroups.get(0);
                            firstGroup.setActive(true);
                            return firstGroup;
                        }
                );
    }

    private String createConfigKey(UUID groupId, int regionId) {
        return groupId + "_" + REGION_PREFIX + regionId;
    }

    @Provides
    TileMarkerMetronomeConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TileMarkerMetronomeConfig.class);
    }
}
