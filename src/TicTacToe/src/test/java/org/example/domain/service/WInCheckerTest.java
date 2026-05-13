package org.example.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WInCheckerTest {

    private WinChecker winChecker;

    @BeforeEach
    void setUp() {
        winChecker = new WinChecker();
    }

    @Test
    void testIsWinGame_ShouldReturnTrue_WhenHorizontalWin() {
        int[][] field = {{1, 1, 1}, {0, 0, 0}, {0, 0, 0}};
        boolean  result = winChecker.isWinGame(field, 1);
        assertTrue(result);
    }

    @Test
    void testIsWinGame_ShouldReturnTrue_WhenVerticalWin() {
        int[][] field = {{1, 2, 1}, {1, 0, 2}, {1, 0, 0}};
        boolean  result = winChecker.isWinGame(field, 1);
        assertTrue(result);
    }

    @Test
    void testIsWinGame_ShouldReturnTrue_WhenDiagonalRightWin() {
        int[][] field = {{2, 2, 1}, {1, 2, 2}, {1, 1, 2}};
        boolean  result = winChecker.isWinGame(field, 2);
        assertTrue(result);
    }

    @Test
    void testIsWinGame_ShouldReturnTrue_WhenDiagonalLeftWin() {
        int[][] field = {{1, 2, 2}, {1, 2, 2}, {2, 1, 1}};
        boolean  result = winChecker.isWinGame(field, 2);
        assertTrue(result);
    }

    @Test
    void testIsWinGame_ShouldReturnFalse_WhenNoWin() {
        int[][] field = {{1, 0, 1}, {0, 2, 0}, {0, 0, 0}};
        boolean  result = winChecker.isWinGame(field, 2);
        assertFalse(result);
    }

    @Test
    void testNoFreeCells_ShouldReturnTrue_WhenFieldIsFull() {
        int[][] field = {{1, 2, 1}, {2, 2, 1}, {1, 1, 2}};
        boolean  result = winChecker.noFreeCells(field);
        assertTrue(result);
    }

    @Test
    void testNoFreeCells_ShouldReturnFalse_WhenFieldIsNotFull() {
        int[][] field = {{1, 2, 1}, {2, 2, 1}, {0, 1, 2}};
        boolean  result = winChecker.noFreeCells(field);
        assertFalse(result);
    }

    @Test
    void testNoFreeCells_ShouldReturnTrue_WhenPlayerXWinBefore() {
        int[][] field = {{1, 1, 1}, {0, 2, 0}, {2, 0, 0}};
        boolean  result = winChecker.noFreeCells(field);
        assertTrue(result);
    }

    @Test
    void testNoFreeCells_ShouldReturnTrue_WhenPlayerOWinBefore() {
        int[][] field = {{1, 2, 1}, {1, 2, 0}, {0, 2, 0}};
        boolean result = winChecker.noFreeCells(field);
        assertTrue(result);
    }
}
