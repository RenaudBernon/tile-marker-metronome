package com.tilemarkermetronome;

import java.awt.Color;
import java.util.List;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import static com.tilemarkermetronome.TileMarkerMetronomeConfig.TILE_MARKER_METRONOME_CONFIG_GROUP;
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
            keyName = "defaultColor1",
            name = "default color 1",
            description = "The first default colors for marked tiles",
            section = defaultConfigSection,
            position = 1
    )
    default Color color1() {
        return WHITE;
    }

    @Alpha
    @ConfigItem(
            keyName = "defaultColor2",
            name = "default color 2",
            description = "The second default colors for marked tiles",
            section = defaultConfigSection,
            position = 2
    )
    default Color color2() {
        return CYAN;
    }

    @ConfigItem(
            keyName = "fillOpacity",
            name = "Fill opacity",
            description = "Opacity of the tile fill color",
            section = defaultConfigSection,
            position = 3
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
            position = 4
    )
    default double borderWidth() {
        return 2;
    }
}
