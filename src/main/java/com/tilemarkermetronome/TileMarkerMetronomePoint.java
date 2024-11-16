package com.tilemarkermetronome;

import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

@Getter
@Setter
@EqualsAndHashCode(exclude = "label")
@AllArgsConstructor
public class TileMarkerMetronomePoint {
    private final int regionId;
    private final WorldPoint worldPoint;
    @Nullable
    private String label;

    public boolean isSameAs(WorldPoint other) {
        return worldPoint.getRegionX() == other.getRegionX()
                && worldPoint.getRegionY() == other.getRegionY()
                && worldPoint.getPlane() == other.getPlane();
    }
}
