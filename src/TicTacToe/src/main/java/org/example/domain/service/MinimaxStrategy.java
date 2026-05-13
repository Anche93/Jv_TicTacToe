package org.example.domain.service;

import org.example.domain.model.Constant;
import org.example.domain.port.MoveStrategy;

public record MinimaxStrategy(WinChecker winChecker) implements MoveStrategy {

    public MinimaxStrategy(WinChecker winChecker) {
        this.winChecker = winChecker;
    }


    @Override
    public int[] findBestCompMove(int[][] field, int valueComp, int valuePlayer) {
        int bestScore = -1000;
        int[] bestCompMove = {-1, -1};

        for (int i = 0; i < Constant.ROW; i++) {
            for (int j = 0; j < Constant.COL; j++) {
                if (field[i][j] == Constant.EMPTY) {
                    field[i][j] = valueComp;

                    int movePrice = minimax(field, 0, false, valueComp, valuePlayer);
                    field[i][j] = Constant.EMPTY;

                    if (movePrice > bestScore) {
                        bestCompMove[0] = i;
                        bestCompMove[1] = j;
                        bestScore = movePrice;
                    }
                }
            }
        }
        return bestCompMove;
    }

    @Override
    public boolean isComputerTurn(int[][] field, int firstPlayer) {
        int countX = 0;
        int countO = 0;

        for (int i = 0; i < Constant.ROW; i++) {
            for (int j = 0; j < Constant.COL; j++) {
                if (field[i][j] == Constant.PLAYER_X) countX++;
                if (field[i][j] == Constant.PLAYER_O) countO++;
            }
        }
        if (countX == 0 && countO == 0) {
            return firstPlayer == Constant.PLAYER_X;
        }

        if (countX == countO) {
            return firstPlayer == Constant.PLAYER_X;
        }
        if (countX > countO) {
            return firstPlayer == Constant.PLAYER_O;
        }
        return true;
    }

    private int minimax(int[][] field, int depth, boolean compMove, int valueComp, int valuePlayer) {
        int score = evaluateScore(field, valueComp, valuePlayer);
        if (score == 10 || score == -10) return score;

        if (winChecker.noFreeCells(field)) return 0;

        int bestScore;
        if (compMove) {
            bestScore = -1000;
            for (int i = 0; i < Constant.ROW; i++) {
                for (int j = 0; j < Constant.COL; j++) {
                    if (field[i][j] == Constant.EMPTY) {
                        field[i][j] = valueComp;
                        bestScore = Math.max(bestScore, minimax(field, depth + 1, false, valueComp, valuePlayer));
                        field[i][j] = Constant.EMPTY;
                    }
                }
            }
        } else {
            bestScore = 1000;
            for (int i = 0; i < Constant.ROW; i++) {
                for (int j = 0; j < Constant.COL; j++) {
                    if (field[i][j] == Constant.EMPTY) {
                        field[i][j] = valuePlayer;
                        bestScore = Math.min(bestScore, minimax(field, depth + 1, true, valueComp, valuePlayer));
                        field[i][j] = Constant.EMPTY;
                    }
                }
            }
        }
        return bestScore;
    }

    private int evaluateScore(int[][] field, int valueComp, int valuePlayer) {
        if (winChecker.isWinGame(field, valueComp)) return 10;
        if (winChecker.isWinGame(field, valuePlayer)) return -10;
        return 0;
    }
}
