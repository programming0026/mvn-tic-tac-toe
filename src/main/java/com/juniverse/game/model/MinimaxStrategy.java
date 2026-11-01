package com.juniverse.game.model;

/**
 * Juega siempre la mejor jugada posible con el algoritmo minimax: la computadora (O) nunca pierde.
 */
public class MinimaxStrategy implements MoveStrategy {

    @Override
    public int[] chooseMove(Board board) {
        int bestValue = Integer.MIN_VALUE;
        int[] bestMove = new int[2];
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.isEmpty(i, j)) {
                    board.mark(i, j, Board.PLAYER_O);
                    int moveValue = minimax(board, 0, false);
                    board.clear(i, j);
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

    private int minimax(Board board, int depth, boolean maximizing) {
        if (board.hasWon(Board.PLAYER_O)) {
            return 10 - depth;
        }
        if (board.hasWon(Board.PLAYER_X)) {
            return depth - 10;
        }
        if (board.isFull()) {
            return 0;
        }
        return maximizing ? best(board, depth, Board.PLAYER_O, true) : best(board, depth, Board.PLAYER_X, false);
    }

    private int best(Board board, int depth, char player, boolean maximizing) {
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (board.isEmpty(i, j)) {
                    board.mark(i, j, player);
                    int value = minimax(board, depth + 1, !maximizing);
                    board.clear(i, j);
                    best = maximizing ? Math.max(best, value) : Math.min(best, value);
                }
            }
        }
        return best;
    }
}
