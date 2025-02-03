package com.tilemarkermetronome.ui.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.JButton;
import lombok.Getter;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.ui.FontManager;

public class HotkeyButton extends JButton {

    @Getter
    private Consumer<Keybind> keybindConsumer;

    public HotkeyButton(Keybind initialKeybind, Consumer<Keybind> keybindConsumer, boolean modifierless) {
        this.keybindConsumer = keybindConsumer;
        // Disable focus traversal keys such as tab to allow tab key to be bound
        setFocusTraversalKeysEnabled(false);
        setFont(FontManager.getDefaultFont().deriveFont(12.f));
        setKeybind(initialKeybind);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Mouse buttons other than button1 don't give focus
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // We have to use a mouse adapter instead of an action listener so the press action key (space) can be bound
                    setKeybind(Keybind.NOT_SET);
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (modifierless) {
                    setKeybind(new ModifierlessKeybind(e));
                } else {
                    setKeybind(new Keybind(e));
                }
            }
        });
    }

    public void setKeybind(Keybind keybind) {
        if (keybind == null) {
            keybind = Keybind.NOT_SET;
        }
        keybindConsumer.accept(keybind);
        setText(keybind.toString());
    }
}
