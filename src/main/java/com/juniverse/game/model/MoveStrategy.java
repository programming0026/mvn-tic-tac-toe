package com.juniverse.game.model;

/**
 * Estrategia con la que la computadora elige su jugada. Permite añadir otras dificultades (por ejemplo, aleatoria)
 * sin tocar la ventana.
 */
public interface MoveStrategy {

    /**
     * Elige una casilla libre.
     *
     * @param board tablero actual (la estrategia lo puede modificar mientras prueba jugadas, pero debe dejarlo igual)
     * @return {@code {fila, columna}}
     */
    int[] chooseMove(Board board);
}
