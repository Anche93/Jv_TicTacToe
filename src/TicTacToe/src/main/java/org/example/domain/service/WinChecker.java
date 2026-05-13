package org.example.domain.service;

import org.example.domain.model.Constant;

public class WinChecker {

    public boolean isWinGame(int[][] gameField, int winner) {
        int countHorizontal;
        int countVertical;

        for (int i = 0; i < Constant.ROW; i++) {
            countHorizontal = 0;
            countVertical = 0;

            for (int j = 0; j < Constant.COL; j++) {
                if (gameField[i][j] == winner) countHorizontal++;
                if (gameField[j][i] == winner) countVertical++;
            }
            if (countVertical == Constant.COL || countHorizontal == Constant.ROW) {
                return true;
            }

        }
        int countDiagonalLeft = 0;
        int countDiagonalRight = 0;

        for (int i = 0; i < Constant.ROW; i++) {
            if (gameField[i][i] == winner) countDiagonalLeft++;
            if (gameField[i][Constant.ROW - 1 - i] == winner) countDiagonalRight++;
        }
        return countDiagonalLeft == Constant.ROW || countDiagonalRight == Constant.ROW;
    }

    public boolean noFreeCells(int[][] gameField) {
        if (isWinGame(gameField, Constant.PLAYER_X) ||
                isWinGame(gameField, Constant.PLAYER_O)) {
            return true;
        }
        for (int i = 0; i < Constant.ROW; i++) {
            for (int j = 0; j < Constant.COL; j++) {
                if (gameField[i][j] == Constant.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
}
