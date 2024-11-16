package com.tilemarkermetronome;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import com.tilemarkermetronome.ui.TileMarkerMetronomePluginPanel;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;


@Slf4j
@PluginDescriptor(
        name = "Tile Marker Metronome"
)
public class TileMarkerMetronomePlugin extends Plugin {
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

    @Override
    public void startUp() {
        overlayManager.add(overlay);
        loadGroups();
        loadPoints();

        pluginPanel = new TileMarkerMetronomePluginPanel(this);
        pluginPanel.rebuild();

        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), PANEL_ICON);
        navigationButton = NavigationButton.builder()
                .tooltip(PLUGIN_NAME)
                .icon(icon)
                .priority(10000)
                .panel(pluginPanel)
                .build();

        clientToolbar.addNavigation(navigationButton);
    }

    @Override
    public void shutDown() {
        overlayManager.remove(overlay);
        tileMarkerMetronomeGroups.clear();
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged profileChanged) {
        loadGroups();
        loadPoints();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        loadGroups();
        loadPoints();
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        final boolean hotKeyPressed = client.isKeyPressed(KeyCode.KC_SHIFT);
        if (hotKeyPressed && event.getOption().equals(WALK_HERE)) {
            Tile selectedSceneTile = client.getSelectedSceneTile();

            if (selectedSceneTile == null) {
                return;
            }

            final WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, selectedSceneTile.getLocalLocation());
            Optional<TileMarkerMetronomePoint> markedTile = getCurrentGroup()
                    .getTileMarkerMetronomePoints()
                    .stream()
                    .filter(tileMarker -> tileMarker.isSameAs(worldPoint))
                    .findFirst();

            client.createMenuEntry(-1)
                    .setOption(markedTile.isPresent() ? "Unmark" : "Mark")
                    .setTarget("Tile")
                    .setType(MenuAction.RUNELITE)
                    .onClick(e -> {
                        Tile target = client.getSelectedSceneTile();
                        if (target != null) {
                            markTile(target.getLocalLocation());
                        }
                    });

            markedTile.ifPresent(tileMarkerMetronomePoint -> client.createMenuEntry(-2)
                    .setOption("Label")
                    .setTarget("Tile")
                    .setType(MenuAction.RUNELITE)
                    .onClick(e -> labelTile(tileMarkerMetronomePoint)));
        }
    }

    private void markTile(LocalPoint localPoint) {
        if (localPoint == null) {
            return;
        }

        WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, localPoint);

        int regionId = worldPoint.getRegionID();
        UUID groupId = getCurrentGroup().getId();
        TileMarkerMetronomePoint point = new TileMarkerMetronomePoint(regionId, worldPoint, null);
        log.debug("Updating point: {} - {}", point, worldPoint);


        List<TileMarkerMetronomePoint> currentGroupTileMarkerMetronomePoints = getCurrentGroup().getTileMarkerMetronomePoints();
        if (currentGroupTileMarkerMetronomePoints.contains(point)) {
            currentGroupTileMarkerMetronomePoints.remove(point);
        } else {
            currentGroupTileMarkerMetronomePoints.add(point);
        }

        savePoints(groupId, regionId);
        loadPoints();
    }

    public void addGroup() {
        tileMarkerMetronomeGroups.add(new TileMarkerMetronomeGroup("New group", List.of(config.color1(), config.color2()), "defaultRenderType", true, false));
        saveGroups();
    }

    public void removeGroup(TileMarkerMetronomeGroup group) {
        tileMarkerMetronomeGroups.remove(group);
        //TODO remove linked tiles
        //TODO reset overlay? (probably not)
        pluginPanel.rebuild();
        saveGroups();
    }

    public List<TileMarkerMetronomeGroup> getGroups() {
        return tileMarkerMetronomeGroups;
    }

    public void saveGroups() {
        if (tileMarkerMetronomeGroups.isEmpty()) {
            configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_METRONOME_GROUPS_KEY);
        } else {
            log.info("saving groups to file");
            String json = gson.toJson(tileMarkerMetronomeGroups);
            configManager.setConfiguration(CONFIG_GROUP, CONFIG_METRONOME_GROUPS_KEY, json);
        }
    }

    public List<TileMarkerMetronomePoint> getVisiblePoints() {
        return getGroups()
                .stream()
                .filter(TileMarkerMetronomeGroup::isEnabled)
                .map(TileMarkerMetronomeGroup::getTileMarkerMetronomePoints)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void labelTile(TileMarkerMetronomePoint existing) {
        chatboxPanelManager.openTextInput("Tile label")
                .value(Optional.ofNullable(existing.getLabel()).orElse(""))
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
                    log.info("Saved {} points", points.size());
                },
                () -> {
                    configManager.unsetConfiguration(CONFIG_GROUP, createConfigKey(groupId, regionId));
                    log.info("Removed points for group {}", groupId);
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
        log.info("Loaded {} points", (int) tileMarkerMetronomeGroups.stream().map(TileMarkerMetronomeGroup::getTileMarkerMetronomePoints).mapToLong(Collection::size).sum());
    }

    private void loadGroups() {
        String json = configManager.getConfiguration(CONFIG_GROUP, CONFIG_METRONOME_GROUPS_KEY);
        tileMarkerMetronomeGroups.clear();
        if (Strings.isNullOrEmpty(json)) {
            tileMarkerMetronomeGroups.add(new TileMarkerMetronomeGroup("Group 1", List.of(config.color1(), config.color2()), "renderType", true, true));
            saveGroups();
        } else {
            List<TileMarkerMetronomeGroup> savedGroups = gson.fromJson(json, new TypeToken<ArrayList<TileMarkerMetronomeGroup>>() {
            }.getType());
            tileMarkerMetronomeGroups.addAll(savedGroups);
        }
        log.info("Loaded {} groups", tileMarkerMetronomeGroups.size());
    }

    private TileMarkerMetronomeGroup getCurrentGroup() {
        return tileMarkerMetronomeGroups
                .stream()
                .filter(TileMarkerMetronomeGroup::isCurrent)
                .findFirst()
                .orElseGet(() -> {
                            loadGroups();
                            return getCurrentGroup();
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
