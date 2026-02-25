package com.tilemarkermetronome;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runelite.client.config.Keybind;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TileMarkerGroupExport {
    private String label;
    private List<Color> colors;
    private TileMarkerMetronomeGroup.AnimationType animationType;
    private int fillOpacity;
    private double borderWidth;
    private int tickCounter;
    private Keybind tickResetHotkey;
    private Keybind visibilityHotkey;
    private List<TileMarkerMetronomePoint> tileMarkerMetronomePoints;

    public static TileMarkerGroupExport fromGroup(TileMarkerMetronomeGroup group) {
        TileMarkerGroupExport export = new TileMarkerGroupExport();
        export.setLabel(group.getLabel());
        export.setColors(new ArrayList<>(group.getColors()));
        export.setAnimationType(group.getAnimationType());
        export.setFillOpacity(group.getFillOpacity());
        export.setBorderWidth(group.getBorderWidth());
        export.setTickCounter(group.getTickCounter());
        export.setTickResetHotkey(group.getTickResetHotkey());
        export.setVisibilityHotkey(group.getVisibilityHotkey());
        export.setTileMarkerMetronomePoints(new ArrayList<>(group.getTileMarkerMetronomePoints()));
        return export;
    }

    public TileMarkerMetronomeGroup toGroup() {
        return new TileMarkerMetronomeGroup(this.label,
                this.colors,
                this.animationType,
                this.fillOpacity,
                this.borderWidth,
                this.tickCounter,
                this.tickResetHotkey,
                this.visibilityHotkey,
                this.tileMarkerMetronomePoints);
    }
}


