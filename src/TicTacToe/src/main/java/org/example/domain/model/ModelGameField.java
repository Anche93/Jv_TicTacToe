package org.example.domain.model;

public class ModelGameField {
    private int[][] gameMatrix;

    public ModelGameField() {
        gameMatrix = new int[Constant.ROW][Constant.COL];
    }

    public int[][] getGameMatrix() {
        return gameMatrix;
    }

    public void setGameMatrix(int[][] gameMatrix) {
        this.gameMatrix = gameMatrix;
    }

    public void setValue(int row, int col, int value) {
        if (row >= 0 && row < Constant.ROW &&
                col >= 0 && col < Constant.COL) {
            gameMatrix[row][col] = value;
        }
    }

    public int getValue(int row, int col) {
        if (row >= 0 && row < Constant.ROW &&
                col >= 0 && col < Constant.COL) {
            return gameMatrix[row][col];
        }
        return 0;
    }
}
