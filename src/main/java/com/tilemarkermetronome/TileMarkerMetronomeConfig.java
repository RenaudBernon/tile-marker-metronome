package com.tilemarkermetronome;

import java.awt.Color;

import com.tilemarkermetronome.TileMarkerMetronomeGroup.AnimationType;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import static com.tilemarkermetronome.TileMarkerMetronomeConfig.TILE_MARKER_METRONOME_CONFIG_GROUP;
import static com.tilemarkermetronome.TileMarkerMetronomeGroup.AnimationType.SYNCED;
import static java.awt.Color.CYAN;
import static java.awt.Color.WHITE;

@ConfigGroup(TILE_MARKER_METRONOME_CONFIG_GROUP)
public interface TileMarkerMetronomeConfig extends Config {

    String TILE_MARKER_METRONOME_CONFIG_GROUP = "tileMarkerMetronome";

    @ConfigSection(
            position = 0,
            name = "Defaults",
            description = "Color config"
    )
    String defaultConfigSection = "defaults";

    @Alpha
    @ConfigItem(
            keyName = "color1",
            name = "Color 1",
            description = "The first default colors for marked tiles",
            section = defaultConfigSection,
            position = 1
    )
    default Color color1() {
        return WHITE;
    }

    @Alpha
    @ConfigItem(
            keyName = "color2",
            name = "Color 2",
            description = "The second default colors for marked tiles",
            section = defaultConfigSection,
            position = 2
    )
    default Color color2() {
        return CYAN;
    }

    @ConfigItem(
            keyName = "animationType",
            name = "Animation type",
            description = "Changes how the colors are animated in a group",
            section = defaultConfigSection,
            position = 4
    )
    default AnimationType animationType() {
        return SYNCED;
    }

    @ConfigItem(
            keyName = "tickCounter",
            name = "Tick Counter",
            description = "Amount of ticks after which tile color should change",
            section = defaultConfigSection,
            position = 5
    )
    @Range(max = 100)
    default int tickCounter() {
        return 1;
    }

    @ConfigItem(
            keyName = "fillOpacity",
            name = "Fill opacity",
            description = "Opacity of the tile fill color",
            section = defaultConfigSection,
            position = 6
    )
    @Range(max = 255)
    default int fillOpacity() {
        return 50;
    }

    @ConfigItem(
            keyName = "borderWidth",
            name = "Border width",
            description = "Width of the marked tile border",
            section = defaultConfigSection,
            position = 7
    )
    default double borderWidth() {
        return 2;
    }
}
