package com.tilemarkermetronome.ui.util;

import net.runelite.client.ui.ColorScheme;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class UIConstants {
    public static final int STANDARD_PADDING = 10;
    public static final int SMALL_PADDING = 5;
    public static final int MINIMAL_PADDING = 2;

    public static final int LABEL_HEIGHT = 24;
    public static final int SPACING_BETWEEN_GROUPS = 10;

    public static final Border PANEL_BORDER = new EmptyBorder(STANDARD_PADDING, STANDARD_PADDING, STANDARD_PADDING, STANDARD_PADDING);
    public static final Border GROUP_BORDER = new EmptyBorder(MINIMAL_PADDING, MINIMAL_PADDING, MINIMAL_PADDING, MINIMAL_PADDING);
    public static final Border BOTTOM_BORDER = new MatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR);
    public static final Border LABEL_PADDING = new EmptyBorder(0, STANDARD_PADDING, 0, 0);
    public static final Border NO_BORDER = new EmptyBorder(0, 0, 0, 0);

    public static final Color PRIMARY_FOREGROUND = Color.WHITE;
    public static final Color SECONDARY_FOREGROUND = ColorScheme.LIGHT_GRAY_COLOR.darker();
    public static final Color SUCCESS_COLOR = ColorScheme.PROGRESS_COMPLETE_COLOR;
    public static final Color ERROR_COLOR = ColorScheme.PROGRESS_ERROR_COLOR;
    public static final Color PANEL_BACKGROUND = ColorScheme.DARKER_GRAY_COLOR;
    public static final Color ACCENT_COLOR = Color.ORANGE;

    public static final int TICK_COUNTER_MIN = 1;
    public static final int TICK_COUNTER_MAX = 100;
    public static final int TICK_COUNTER_STEP = 1;

    public static final int FILL_OPACITY_MIN = 0;
    public static final int FILL_OPACITY_MAX = 255;
    public static final int FILL_OPACITY_STEP = 10;

    public static final double BORDER_WIDTH_MIN = 0;
    public static final double BORDER_WIDTH_MAX = 50;
    public static final double BORDER_WIDTH_STEP = 0.1;

    private UIConstants() {
    }
}

