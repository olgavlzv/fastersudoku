package ru.skeletikana.fastersudoku;

import java.util.Random;

public class GenerateBoard {

    private static int fullViewBoard;
    static Random random = new Random();

    public static int[][] transpose(int[][] board) { // Transposing method
        int[][] newBoard = new int[fullViewBoard][fullViewBoard];
        for (int i = 0; i < fullViewBoard; i++) {
            for (int j = 0; j < fullViewBoard; j++) {
                newBoard[i][j] = board[j][i];
            }
        }
        return newBoard;
    }

    public static int[][] twoStringSwap(int[][] board) {
        int[][] newBoard = new int[fullViewBoard][fullViewBoard];
        for (int i = 0; i < fullViewBoard; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, fullViewBoard);
        }

        int[] toArea;
        if (fullViewBoard == 9) {
            toArea = new int[]{0, 3, 6};
        } else {
            toArea = new int[]{100, 200, 300};
        }
        int area = toArea[random.nextInt(toArea.length)];
        int swapI = 0, swapJ = 0;
        while (swapI == swapJ) {
            swapI = random.nextInt(3) + area;
            swapJ = random.nextInt(3) + area;
        }

        newBoard[swapI] = board[swapJ];
        newBoard[swapJ] = board[swapI];
        return newBoard;
    }

    public static int[][] twoColumnSwap(int[][] board) {
        int[][] newBoard = new int[fullViewBoard][fullViewBoard];
        int[] toArea;
        if (fullViewBoard == 9) {
            toArea = new int[]{0, 3, 6};
        } else {
            toArea = new int[]{100, 200, 300};
        }
        int area = toArea[random.nextInt(toArea.length)];
        int swapI = 0, swapJ = 0;
        while (swapI == swapJ) {
            swapI = random.nextInt(3) + area;
            swapJ = random.nextInt(3) + area;
        }

        for (int i = 0; i < fullViewBoard; i++) {
            for (int j = 0; j < fullViewBoard; j++) {
                if (j == swapI) {
                    newBoard[i][j] = board[i][swapJ];
                } else if (j == swapJ) {
                    newBoard[i][j] = board[i][swapI];
                } else {
                    newBoard[i][j] = board[i][j];
                }
            }
        }
        return newBoard;
    }

    public static int[][] areaStringSwap(int[][] board) {
        int[][] newBoard = new int[fullViewBoard][fullViewBoard];
        for (int i = 0; i < fullViewBoard; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, fullViewBoard);
        }

        int[] toArea;
        if (fullViewBoard == 9) {
            toArea = new int[]{0, 3, 6};
        } else {
            toArea = new int[]{100, 200, 300};
        }
        int swapI = 0, swapJ = 0;
        while (swapI == swapJ) {
            swapI = toArea[random.nextInt(toArea.length)];
            swapJ = toArea[random.nextInt(toArea.length)];
        }

        newBoard[swapI] = board[swapJ];
        newBoard[swapI+1] = board[swapJ+1];
        newBoard[swapI+2] = board[swapJ+2];
        newBoard[swapJ] = board[swapI];
        newBoard[swapJ+1] = board[swapI+1];
        newBoard[swapJ+2] = board[swapI+2];

        return newBoard;
    }

    public static int[][] areaColumnSwap(int[][] board) {
        board = transpose(board);
        board = areaStringSwap(board);
        board = transpose(board);
        return board;
    }

    public static int[][] generate(int getFullView, int holesInBoard) {
        fullViewBoard = getFullView;
        int[][] beginBoard = new int[fullViewBoard][fullViewBoard];
        int[] sequenceOrder;
        if (fullViewBoard == 9) {
            sequenceOrder = new int[]{1, 4, 7, 2, 5, 8, 3, 6, 9};
        } else {
            sequenceOrder = new int[]{1, 3, 2, 4};
        }
        for (int i = 0; i < fullViewBoard; i++) { // Generate primitive board
            int startNum = sequenceOrder[i];
            for (int j = 0; j < fullViewBoard; j++) {
                if (startNum > fullViewBoard) { // Reset value from top to begin
                    startNum -= fullViewBoard;
                }
                beginBoard[i][j] = startNum;
                startNum++;
            }
        }

        for (int i = 0; i < 250; i++) {
            int step = random.nextInt(5);
            switch (step) {
                case 0:
                    beginBoard = transpose(beginBoard);
                    break;
                case 1:
                    beginBoard = twoStringSwap(beginBoard);
                    break;
                case 2:
                    beginBoard = twoColumnSwap(beginBoard);
                    break;
                case 3:
                    beginBoard = areaStringSwap(beginBoard);
                    break;
                case 4:
                    beginBoard = areaColumnSwap(beginBoard);
            }
        }

        for (int i = 0; i < holesInBoard; i++) {
            int x = random.nextInt(fullViewBoard);
            int y = random.nextInt(fullViewBoard);
            if (beginBoard[x][y] == 0) {
                i--;
            } else {
                beginBoard[x][y] = 0;
            }
        }

        return beginBoard;
    }
}
