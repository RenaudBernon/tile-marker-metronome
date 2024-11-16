package com.tilemarkermetronome.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
@AllArgsConstructor
public class ColorPanel extends JPanel {
    private static final int CHECKER_SIZE = 10;

    private Color color;

    protected void setColor(Color color) {
        this.color = color;
        this.paintImmediately(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (this.color.getAlpha() != 255) {
            for (int x = 0; x < getWidth(); x += CHECKER_SIZE) {
                for (int y = 0; y < getHeight(); y += CHECKER_SIZE) {
                    int val = (x / CHECKER_SIZE + y / CHECKER_SIZE) % 2;
                    g.setColor(val == 0 ? Color.LIGHT_GRAY : Color.WHITE);
                    g.fillRect(x, y, CHECKER_SIZE, CHECKER_SIZE);
                }
            }
        }
        g.setColor(color);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
}
