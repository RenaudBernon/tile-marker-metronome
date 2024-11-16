package com.tilemarkermetronome;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
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
    private final TileMarkerMetronomePlugin plugin;

    @Inject
    private TileMarkerMetronomeOverlay(Client client, TileMarkerMetronomePlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(PRIORITY_LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        plugin.getGroups()
                .stream()
                .filter(TileMarkerMetronomeGroup::isVisible)
                .forEach(group -> group.getTileMarkerMetronomePoints()
                        .forEach(point -> drawTile(graphics, point, group)));
        return null;
    }

    private void drawTile(Graphics2D graphics, TileMarkerMetronomePoint point, TileMarkerMetronomeGroup group) {
        WorldPoint.toLocalInstance(client, point.getWorldPoint())
                .stream()
                .filter(this::isOnCurrentPlane)
                .forEach(wp -> drawTile(graphics, point, group, wp));
    }

    private void drawTile(Graphics2D graphics, TileMarkerMetronomePoint point, TileMarkerMetronomeGroup group, WorldPoint worldPoint) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (worldPoint.distanceTo(playerLocation) >= MAX_DRAW_DISTANCE) {
            return;
        }

        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null) {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
        if (poly != null) {
            Stroke stroke = new BasicStroke((float) group.getBorderWidth());
            Color currentColor = group.getCurrentColor(point);
            Color currentFillColor = getFillColor(currentColor, group.getFillOpacity());
            OverlayUtil.renderPolygon(graphics, poly, currentColor, currentFillColor, stroke);

            String label = point.getLabel();
            if (!Strings.isNullOrEmpty(label)) {
                Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, localPoint, label, 0);
                if (canvasTextLocation != null) {
                    OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, currentColor);
                }
            }
        }
    }

    private Color getFillColor(Color color, int fillOpacity) {
        return new Color(color.getRed(),
                color.getGreen(),
                color.getBlue(), fillOpacity);
    }

    private boolean isOnCurrentPlane(WorldPoint worldPoint) {
        return worldPoint.getPlane() == client.getPlane();
    }
}
