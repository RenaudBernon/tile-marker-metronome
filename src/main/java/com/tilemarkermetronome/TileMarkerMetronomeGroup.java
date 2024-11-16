package com.tilemarkermetronome;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileMarkerMetronomeGroup {

    private final UUID id = UUID.randomUUID();
    private final List<TileMarkerMetronomePoint> tileMarkerMetronomePoints = new ArrayList<>();

    private List<Color> colors;
    private String label;
    private String renderType;
    private boolean isEnabled;
    private boolean isCurrent;

    public TileMarkerMetronomeGroup(String label, List<Color> colors, String renderType, boolean isEnabled, boolean isCurrent) {
        this.label = label;
        this.colors = new ArrayList<>(colors);
        this.renderType = renderType;
        this.isEnabled = isEnabled;
        this.isCurrent = isCurrent;
    }

    public void addTileMarkerMetronomePoint(TileMarkerMetronomePoint tileMarkerMetronomePoint) {
        tileMarkerMetronomePoints.add(tileMarkerMetronomePoint);
    }

    public void setColors(List<Color> colors) {
        this.colors = new ArrayList<>(colors);
    }
}
