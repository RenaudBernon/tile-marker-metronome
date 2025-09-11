package com.tilemarkermetronome;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(exclude = "label")
public class TileMarkerMetronomePoint {
    private final UUID uuid;
    private final int regionId;
    private final WorldPoint worldPoint;
    @Nullable
    private String label;

    public TileMarkerMetronomePoint(int regionId, WorldPoint worldPoint, @Nullable String label) {
        this.uuid = UUID.randomUUID();
        this.regionId = regionId;
        this.worldPoint = worldPoint;
        this.label = label;
    }

    public boolean isSameAs(WorldPoint other) {
        return worldPoint.getRegionX() == other.getRegionX()
                && worldPoint.getRegionY() == other.getRegionY()
                && worldPoint.getPlane() == other.getPlane();
    }
}
