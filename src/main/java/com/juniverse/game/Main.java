package com.juniverse.game;

import com.juniverse.game.gui.GameFrame;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import javax.swing.SwingUtilities;

/**
 * @author linux
 */
public class Main {

    public static void main(String[] args) {
        FlatMacDarkLaf.setup();
        SwingUtilities.invokeLater(() -> {
            GameFrame game = new GameFrame();
            game.setVisible(true);
        });
    }
}
