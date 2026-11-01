package com.juniverse.game.model;

/**
 * Tablero 3x3 del tres en raya con sus reglas (casillas libres, victoria y empate), sin depender de la interfaz.
 */
public class Board {

    /** Jugador humano. */
    public static final char PLAYER_X = 'X';

    /** Computadora. */
    public static final char PLAYER_O = 'O';

    /** Casilla vacía. */
    public static final char EMPTY = '\0';

    /** Tamaño del lado del tablero. */
    public static final int SIZE = 3;

    private final char[][] cells = new char[SIZE][SIZE];

    /**
     * Devuelve el contenido de una casilla.
     *
     * @param row fila (0 a 2)
     * @param col columna (0 a 2)
     * @return {@link #PLAYER_X}, {@link #PLAYER_O} o {@link #EMPTY}
     */
    public char get(int row, int col) {
        return cells[row][col];
    }

    /** Indica si la casilla está libre. */
    public boolean isEmpty(int row, int col) {
        return cells[row][col] == EMPTY;
    }

    /**
     * Marca una casilla libre.
     *
     * @param row fila
     * @param col columna
     * @param player jugador que marca
     * @return {@code true} si se marcó; {@code false} si la casilla estaba ocupada
     */
    public boolean mark(int row, int col, char player) {
        if (!isEmpty(row, col)) {
            return false;
        }
        cells[row][col] = player;
        return true;
    }

    /** Deja libre una casilla (lo usa el algoritmo al probar jugadas). */
    public void clear(int row, int col) {
        cells[row][col] = EMPTY;
    }

    /**
     * Comprueba si un jugador completó una fila, una columna o una diagonal.
     *
     * @param player jugador
     * @return {@code true} si ganó
     */
    public boolean hasWon(char player) {
        for (int i = 0; i < SIZE; i++) {
            if (cells[i][0] == player && cells[i][1] == player && cells[i][2] == player) {
                return true;
            }
            if (cells[0][i] == player && cells[1][i] == player && cells[2][i] == player) {
                return true;
            }
        }
        if (cells[0][0] == player && cells[1][1] == player && cells[2][2] == player) {
            return true;
        }
        return cells[0][2] == player && cells[1][1] == player && cells[2][0] == player;
    }

    /** Indica si no queda ninguna casilla libre. */
    public boolean isFull() {
        for (char[] row : cells) {
            for (char cell : row) {
                if (cell == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
}
