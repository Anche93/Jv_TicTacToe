package org.example.domain.port;

public interface MoveStrategy {

    int[] findBestCompMove(int[][] field, int valueComp, int valuePlayer);

    boolean isComputerTurn(int[][] field, int firstPlayer);
}
