package com.tilemarkermetronome.ui.util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ComponentUtil {

    public static void configureMouseListener(Component component, Consumer<MouseEvent> onMousePressed, Consumer<MouseEvent> onMouseEntered, Consumer<MouseEvent> onMouseExited) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                onMousePressed.accept(mouseEvent);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                onMouseEntered.accept(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                onMouseExited.accept(mouseEvent);
            }
        });
    }
}
