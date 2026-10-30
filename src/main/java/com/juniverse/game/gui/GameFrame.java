package com.juniverse.game.gui;

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
 * The Class GameFrame.
 *
 * @author linux
 */
public class GameFrame extends JFrame {

    /** The Constant serialVersionUID. */
	@Serial
    private static final long serialVersionUID = 1L;
	
	/** The Constant TITLE. */
	private static final String TITLE = "Tic-tac-toe";
    
    /** The Constant PLAYER_X. */
    private static final char PLAYER_X = 'X';
    
    /** The Constant PLAYER_O. */
    private static final char PLAYER_O = 'O';
    
    /** The Constant EMPTY. */
    private static final char EMPTY = '\0';

    /** The current player. */
    private char currentPlayer = PLAYER_X;
    
    /** The board. */
    private char[][] board = new char[3][3];
    
    /** The game ended. */
    private boolean gameEnded = false;
    /** Imágenes ya leídas (X y O): se cargan una sola vez en lugar de en cada repintado. */
    private final Map<String, Optional<BufferedImage>> imageCache = new HashMap<>();

    /**
     * Instantiates a new game frame.
     */
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
     * Play game.
     *
     * @param x the x
     * @param y the y
     */
    private void playGame(int x, int y) {
        if (board[y][x] == EMPTY) {
            board[y][x] = currentPlayer;
            if (checkWin(currentPlayer)) {
                gameEnded = true;
                repaint();
                MessageUtil.showInfo("Player " + currentPlayer + " wins!", TITLE);
            } else if (boardFull()) {
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
     * Reset game.
     */
    private void resetGame() {
        currentPlayer = PLAYER_X;
        board = new char[3][3];
        gameEnded = false;
        repaint();
    }

    /**
     * Load image.
     *
     * @param fileName the file name
     * @return the optional
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
     * Draw board.
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
                if (board[i][j] == PLAYER_X) {
                    if (imgX.isPresent()) {
                        g2d.drawImage(imgX.get(), j * width / 3, i * height / 3, width / 3, height / 3, this);
                    } else {
                        g2d.setColor(Color.RED);
                        g2d.drawLine(j * width / 3, i * height / 3, (j + 1) * width / 3, (i + 1) * height / 3);
                        g2d.drawLine((j + 1) * width / 3, i * height / 3, j * width / 3, (i + 1) * height / 3);
                    }
                } else if (board[i][j] == PLAYER_O) {
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
     * Check win.
     *
     * @param player the player
     * @return true, if successful
     */
    private boolean checkWin(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        return board[0][2] == player && board[1][1] == player && board[2][0] == player;
    }

    /**
     * Board full.
     *
     * @return true, if successful
     */
    private boolean boardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Ai move.
     */
    private void aiMove() {
        if (gameEnded) {
            return;
        }
        int[] bestMove = findBestMove();
        int x = bestMove[1];
        int y = bestMove[0];
        board[y][x] = PLAYER_O;
        repaint(); // sin esto la jugada de la IA no se veía hasta el siguiente clic
        if (checkWin(PLAYER_O)) {
            gameEnded = true;
            MessageUtil.showInfo("Player O wins!", TITLE);
        } else if (boardFull()) {
            gameEnded = true;
            MessageUtil.showInfo("It's a tie!", TITLE);
        } else {
            currentPlayer = PLAYER_X;
        }
    }

    /**
     * Find best move.
     *
     * @return the int[]
     */
    private int[] findBestMove() {
        int bestValue = Integer.MIN_VALUE;
        int[] bestMove = new int[2];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = PLAYER_O;
                    int moveValue = minimax(0, false);
                    board[i][j] = EMPTY;
                    if (moveValue > bestValue) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestValue = moveValue;
                    }
                }
            }
        }

        return bestMove;
    }

    /**
     * Minimax.
     *
     * @param depth the depth
     * @param isMax the is max
     * @return the int
     */
    private int minimax(int depth, boolean isMax) {
        if (checkWin(PLAYER_O)) {
            return 10 - depth;
        }
        if (checkWin(PLAYER_X)) {
            return depth - 10;
        }
        if (boardFull()) {
            return 0;
        }

        if (isMax) {
            return getMax(depth);
        } else {
            return getMin(depth);
        }
    }

    /**
     * Gets the min.
     *
     * @param depth the depth
     * @return the min
     */
    private int getMin(int depth) {
        int best = Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = PLAYER_X;
                    best = Math.min(best, minimax(depth + 1, true));
                    board[i][j] = EMPTY;
                }
            }
        }
        return best;
    }

    /**
     * Gets the max.
     *
     * @param depth the depth
     * @return the max
     */
    private int getMax(int depth) {
        int best = Integer.MIN_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = PLAYER_O;
                    best = Math.max(best, minimax(depth + 1, false));
                    board[i][j] = EMPTY;
                }
            }
        }
        return best;
    }

    /**
     * About.
     */
    private void about() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.setVisible(true);
    }
}
