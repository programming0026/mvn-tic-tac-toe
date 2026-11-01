package com.juniverse.game.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Pruebas del tablero y de la estrategia minimax. */
class BoardAndStrategyTest {

    private static Board boardWith(String rows) {
        // tres filas separadas por "/", con X, O o "." para casilla vacía
        Board board = new Board();
        String[] lines = rows.split("/");
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                char ch = lines[r].charAt(c);
                if (ch != '.') {
                    board.mark(r, c, ch);
                }
            }
        }
        return board;
    }

    @Test
    void marcaSoloCasillasLibres() {
        Board board = new Board();
        assertTrue(board.mark(1, 1, Board.PLAYER_X));
        assertFalse(board.mark(1, 1, Board.PLAYER_O));
        assertEquals(Board.PLAYER_X, board.get(1, 1));
        assertFalse(board.isEmpty(1, 1));
    }

    @Test
    void detectaFilasColumnasYDiagonales() {
        assertTrue(boardWith("XXX/.../...").hasWon(Board.PLAYER_X));
        assertTrue(boardWith(".../OOO/...").hasWon(Board.PLAYER_O));
        assertTrue(boardWith("X../X../X..").hasWon(Board.PLAYER_X));
        assertTrue(boardWith("X../.X./..X").hasWon(Board.PLAYER_X));
        assertTrue(boardWith("..O/.O./O..").hasWon(Board.PLAYER_O));
        assertFalse(boardWith("XOX/OXO/OXO").hasWon(Board.PLAYER_X));
    }

    @Test
    void detectaElTableroLleno() {
        assertFalse(new Board().isFull());
        assertTrue(boardWith("XOX/XOO/OXX").isFull());
    }

    @Test
    void minimaxCompletaSuLineaSiPuedeGanar() {
        Board board = boardWith("OO./XX./X..");
        assertArrayEquals(new int[] {0, 2}, new MinimaxStrategy().chooseMove(board));
    }

    @Test
    void minimaxBloqueaLaVictoriaDelRival() {
        Board board = boardWith("XX./.O./...");
        assertArrayEquals(new int[] {0, 2}, new MinimaxStrategy().chooseMove(board));
    }

    @Test
    void minimaxDejaElTableroSinCambios() {
        Board board = boardWith("X../.O./...");
        new MinimaxStrategy().chooseMove(board);
        assertEquals(Board.PLAYER_X, board.get(0, 0));
        assertEquals(Board.PLAYER_O, board.get(1, 1));
        assertTrue(board.isEmpty(2, 2));
    }

    @Test
    void laComputadoraNuncaPierdeContraJugadasCualesquiera() {
        // El humano (X) prueba todas las secuencias posibles; la computadora (O) no debe perder nunca.
        assertFalse(humanCanWin(new Board()));
    }

    private boolean humanCanWin(Board board) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board.isEmpty(r, c)) {
                    board.mark(r, c, Board.PLAYER_X);
                    boolean humanWins = board.hasWon(Board.PLAYER_X);
                    boolean continues = !humanWins && !board.isFull();
                    if (humanWins) {
                        board.clear(r, c);
                        return true;
                    }
                    if (continues) {
                        int[] move = new MinimaxStrategy().chooseMove(board);
                        board.mark(move[0], move[1], Board.PLAYER_O);
                        boolean computerWins = board.hasWon(Board.PLAYER_O);
                        boolean found = !computerWins && !board.isFull() && humanCanWin(board);
                        board.clear(move[0], move[1]);
                        if (found) {
                            board.clear(r, c);
                            return true;
                        }
                    }
                    board.clear(r, c);
                }
            }
        }
        return false;
    }
}
