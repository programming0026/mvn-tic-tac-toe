package com.juniverse.game.gui;

import com.juniverse.game.model.Board;
import com.juniverse.game.model.MinimaxStrategy;
import com.juniverse.game.model.MoveStrategy;
import com.juniverse.game.util.MessageUtil;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;

/**
 * @author Java Universe
 */
public class GameFrame extends JFrame {

	@Serial
    private static final long serialVersionUID = 1L;
	
	private static final String TITLE = "Tic-tac-toe";
    
    private static final char PLAYER_X = Board.PLAYER_X;

    private static final char PLAYER_O = Board.PLAYER_O;

    /** Jugador en turno. */
    private char currentPlayer = PLAYER_X;
    
    /** Tablero. */
    private Board board = new Board();

    /** Estrategia con la que la computadora elige su jugada. */
    private final MoveStrategy computer = new MinimaxStrategy();
    
    private boolean gameEnded = false;
    /** Imágenes ya leídas (X y O): se cargan una sola vez en lugar de en cada repintado. */
    private final Map<String, Optional<BufferedImage>> imageCache = new HashMap<>();

    public GameFrame() {
        setTitle(TITLE);
        setSize(600, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenuItem aboutItem = new JMenuItem("About");

        newGameItem.addActionListener((ActionEvent e)
                -> resetGame()
        );

        exitItem.addActionListener((ActionEvent e)
                -> System.exit(0)
        );

        aboutItem.addActionListener(e -> about());

        gameMenu.add(newGameItem);
        gameMenu.add(exitItem);
        helpMenu.add(aboutItem);
        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        JPanel panel = new JPanel() {
        	@Serial
            private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g, getWidth(), getHeight());
            }
        };
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameEnded) {
                    return;
                }
                // Se usa el tamaño del panel (el mismo con el que se dibuja) y se limita a 0..2
                Component panelClicked = e.getComponent();
                int x = Math.min(2, e.getX() * 3 / Math.max(1, panelClicked.getWidth()));
                int y = Math.min(2, e.getY() * 3 / Math.max(1, panelClicked.getHeight()));
                playGame(x, y);
            }
        });
        add(panel);
    }

    /**
     * Realiza una jugada.
     *
     * @param x coordenada x
     * @param y the y
     */
    private void playGame(int x, int y) {
        if (board.mark(y, x, currentPlayer)) {
            if (board.hasWon(currentPlayer)) {
                gameEnded = true;
                repaint();
                MessageUtil.showInfo("Player " + currentPlayer + " wins!", TITLE);
            } else if (board.isFull()) {
                gameEnded = true;
                repaint();
                MessageUtil.showInfo("It's a tie!", TITLE);
            } else {
                currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
                if (currentPlayer == PLAYER_O) {
                    repaint();
                    aiMove();
                }
            }
        }
    }

    /**
     * Reinicia la partida.
     */
    private void resetGame() {
        currentPlayer = PLAYER_X;
        board = new Board();
        gameEnded = false;
        repaint();
    }

    /**
     * Carga la imagen.
     *
     * @param fileName nombre del archivo
     * @return opcional
     */
    private Optional<BufferedImage> loadImage(String fileName) {
        return imageCache.computeIfAbsent(fileName, this::readImage);
    }

    /**
     * Lee una imagen del classpath. Devuelve vacío (y se dibujará una figura) si no existe o no se puede leer.
     *
     * @param fileName nombre del archivo dentro de {@code /icons/}
     * @return la imagen, o vacío
     */
    private Optional<BufferedImage> readImage(String fileName) {
        try (InputStream in = getClass().getResourceAsStream("/icons/" + fileName)) {
            return in == null ? Optional.empty() : Optional.ofNullable(ImageIO.read(in));
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    /**
     * Dibuja el tablero.
     *
     * @param g contexto gráfico
     * @param width ancho del panel en píxeles
     * @param height alto del panel en píxeles
     */
    private void drawBoard(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;

        int cellWidth = width / 3;
        int cellHeight = height / 3;

        g2d.setBackground(Color.BLACK);
        g2d.clearRect(0, 0, width, height);

        g2d.setColor(Color.WHITE);

        for (int i = 1; i < 3; i++) {
            g2d.drawLine(0, i * cellHeight, width, i * cellHeight);
        }

        for (int i = 1; i < 3; i++) {
            g2d.drawLine(i * cellWidth, 0, i * cellWidth, height);
        }
        g2d.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.6f
        ));

        Optional<BufferedImage> imgX = loadImage("x.png");
        Optional<BufferedImage> imgO = loadImage("o.png");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.get(i, j) == PLAYER_X) {
                    if (imgX.isPresent()) {
                        g2d.drawImage(imgX.get(), j * width / 3, i * height / 3, width / 3, height / 3, this);
                    } else {
                        g2d.setColor(Color.RED);
                        g2d.drawLine(j * width / 3, i * height / 3, (j + 1) * width / 3, (i + 1) * height / 3);
                        g2d.drawLine((j + 1) * width / 3, i * height / 3, j * width / 3, (i + 1) * height / 3);
                    }
                } else if (board.get(i, j) == PLAYER_O) {
                    if (imgO.isPresent()) {
                        g2d.drawImage(imgO.get(), j * width / 3, i * height / 3, width / 3, height / 3, this);
                    } else {
                        g2d.setColor(Color.YELLOW);
                        g2d.drawOval(j * width / 3, i * height / 3, width / 3, height / 3);
                    }
                }
            }
        }
    }

    /**
     * Calcula la jugada de la computadora.
     */
    private void aiMove() {
        if (gameEnded) {
            return;
        }
        int[] bestMove = computer.chooseMove(board);
        int x = bestMove[1];
        int y = bestMove[0];
        board.mark(y, x, PLAYER_O);
        repaint(); // sin esto la jugada de la IA no se veía hasta el siguiente clic
        if (board.hasWon(PLAYER_O)) {
            gameEnded = true;
            MessageUtil.showInfo("Player O wins!", TITLE);
        } else if (board.isFull()) {
            gameEnded = true;
            MessageUtil.showInfo("It's a tie!", TITLE);
        } else {
            currentPlayer = PLAYER_X;
        }
    }

    /**
     * Muestra el cuadro «Acerca de».
     */
    private void about() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.setVisible(true);
    }
}
