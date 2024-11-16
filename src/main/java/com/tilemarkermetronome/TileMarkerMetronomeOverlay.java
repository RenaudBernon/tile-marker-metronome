package com.tilemarkermetronome;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.Collection;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

@Slf4j
public class TileMarkerMetronomeOverlay extends Overlay {
    private static final int MAX_DRAW_DISTANCE = 32;

    private final Client client;
    private final TileMarkerMetronomeConfig config;
    private final TileMarkerMetronomePlugin plugin;

    @Inject
    private TileMarkerMetronomeOverlay(Client client, TileMarkerMetronomeConfig config, TileMarkerMetronomePlugin plugin) {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(PRIORITY_LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        final Collection<TileMarkerMetronomePoint> points = plugin.getVisiblePoints();
        log.info("Got {} visible points", points.size());
        if (points.isEmpty()) {
            return null;
        }

        //TODO use borderWidth from group
        Stroke stroke = new BasicStroke((float) config.borderWidth());
        points.stream()
                .filter(this::isOnCurrentPlane)
                //TODO use all colors and renderType
                .forEach(point -> drawTile(graphics, point.getWorldPoint(), config.color1(), point.getLabel(), stroke));
        return null;
    }

    private void drawTile(Graphics2D graphics, WorldPoint point, Color color, @Nullable String label, Stroke borderStroke) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (point.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE) {
            return;
        }

        LocalPoint lp = LocalPoint.fromWorld(client, point);
        if (lp == null) {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly != null) {
            //TODO use fillOpacity from group
            OverlayUtil.renderPolygon(graphics, poly, color, new Color(0, 0, 0, config.fillOpacity()), borderStroke);
        }

        if (!Strings.isNullOrEmpty(label)) {
            Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, label, 0);
            if (canvasTextLocation != null) {
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
            }
        }
    }

    private boolean isOnCurrentPlane(TileMarkerMetronomePoint point) {
        return point.getWorldPoint().getPlane() == client.getPlane();
    }
}
