package com.tilemarkermetronome.ui.util;

import javax.swing.*;
import java.awt.*;

import static com.tilemarkermetronome.ui.util.UIConstants.BOTTOM_BORDER;
import static com.tilemarkermetronome.ui.util.UIConstants.NO_BORDER;
import static com.tilemarkermetronome.ui.util.UIConstants.PANEL_BACKGROUND;
import static com.tilemarkermetronome.ui.util.UIConstants.PRIMARY_FOREGROUND;

public class UIStyler {
    private UIStyler() {
    }

    public static JLabel styledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(PRIMARY_FOREGROUND);
        label.setBorder(NO_BORDER);
        label.setBackground(PANEL_BACKGROUND);
        return label;
    }

    public static void stylePanel(JPanel panel) {
        panel.setBackground(PANEL_BACKGROUND);
    }

    public static void styleSectionPanel(JPanel panel) {
        stylePanel(panel);
        panel.setBorder(BOTTOM_BORDER);
    }

    public static <T> void styleComboBox(JComboBox<T> comboBox) {
        comboBox.setBackground(PANEL_BACKGROUND);
        comboBox.setForeground(PRIMARY_FOREGROUND);
    }

    public static void styleSpinner(JSpinner spinner) {
        spinner.setBackground(PANEL_BACKGROUND);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.NumberEditor) {
            JTextField textField = ((JSpinner.NumberEditor) editor).getTextField();
            textField.setBackground(PANEL_BACKGROUND);
            textField.setForeground(PRIMARY_FOREGROUND);
            textField.setCaretColor(PRIMARY_FOREGROUND);
        }
    }

    public static void styleButton(JButton button) {
        button.setBackground(PANEL_BACKGROUND);
        button.setForeground(PRIMARY_FOREGROUND);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(0, 3, 1, 3)
        ));
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
    }
}

