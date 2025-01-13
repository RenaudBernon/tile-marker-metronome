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

    private String label;
    private transient int currentColor;
    private List<Color> colors;
    private AnimationType animationType;
    private int fillOpacity;
    private double borderWidth;
    private int tickCounter;
    private boolean isVisible;
    private boolean isActive;
    private transient int currentTick;

    public TileMarkerMetronomeGroup(String label, TileMarkerMetronomeConfig config, boolean isVisible, boolean isActive) {
        this.label = label;
        this.colors = new ArrayList<>(List.of(config.color1(), config.color2()));
        this.fillOpacity = config.fillOpacity();
        this.borderWidth = config.borderWidth();
        this.tickCounter = config.tickCounter();
        this.animationType = config.animationType();
        this.isVisible = isVisible;
        this.isActive = isActive;
    }

    public void setActive() {
        this.isActive = true;
    }

    public void setInactive() {
        this.isActive = false;
    }

    public void setNextColor() {
        currentColor++;
        if (currentColor >= colors.size()) {
            currentColor = 0;
        }
    }

    public Color getCurrentColor(TileMarkerMetronomePoint point) {
        if (animationType == AnimationType.TRAIN) {
            int pointIndex = tileMarkerMetronomePoints.indexOf(point);
            int currentTileColor = 0;
            for (int i = 0; i < pointIndex; i++) {
                currentTileColor++;
                currentTileColor %= colors.size();
            }
            return colors.get(currentTileColor);
        }
        return colors.get(currentColor);
    }

    public enum AnimationType {
        DISABLED,
        SYNCED,
        TRAIN
    }
}