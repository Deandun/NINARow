package com.DeaNoy;

import Logic.Models.Cell;
import Logic.Models.GameSettings;

// The console UI board
public class Board {

    private static final char sfSeparator = '|';
    private static final int sfSeparatorStartIndex = 2; // The separator start in this index in every row
    private static final int sfSeparatorsIncreamentor = 4; // The distance between 2 separators
    private char[][] mBoard;
    private int mPhysicRowsSize;
    private int mPhysicColsSize;

    public Board(int rows, int columns) {
        this.mPhysicRowsSize = getActualRow(rows);
        this.mPhysicColsSize = getActualColumn(columns);
        initBoard();
    }

    // API

    public void UpdateBoard(int row, int column, char sign) {
        int actualRow = getActualRow(row);
        int actualColumn = getActualColumn(column);

        mBoard[actualRow][actualColumn] = sign;
    }

    public void PrintBoard() {
        for(char[] row: mBoard){
            System.out.println(row);
        }
    }

    public void Clear() {
        for (char[] column : mBoard) {
            for (char cell : column) {
                cell = ' ';
            }
        }
    }

    // Helper function
    private void initBoard() {
        mBoard = new char[mPhysicRowsSize][mPhysicColsSize];
        for(int i = 0; i < mPhysicRowsSize; i++) {
            for(int j = 0; j < mPhysicColsSize; j++) {
                mBoard[i][j] = ' ';
            }
        }
        setBoardSeparators();
        setBoardRowIndexes();
        setBoardColumnIndexes();
    }

    private void setBoardSeparators() {
        for(char[] boardRow: mBoard) {
            for(int i = sfSeparatorStartIndex; i < mPhysicColsSize; i += sfSeparatorsIncreamentor) {
                boardRow[i] = sfSeparator;
            }
        }
    }

    private void setBoardRowIndexes() {
        for(int i = 1; i < mPhysicRowsSize; i++) {
            if(i < 10) {
                // Put single digit game index in index 1 (will start with a single space "_1").
                mBoard[i][0] = ' ';
                mBoard[i][1] = getNumberSecondDigitAsChar(i);
            } else {
                mBoard[i][0] = getNumberFirstDigitAsChar(i); // First digit.
                mBoard[i][1] = getNumberSecondDigitAsChar(i); // Second digit.
            }
        }
    }

    private void setBoardColumnIndexes() {
        char[] firstRow = mBoard[0];
        int actualColumnIndex;

        // Set first 2 indexes
        firstRow[0] = ' ';
        firstRow[1] = ' ';

        for(int i = 0; i < GameSettings.getInstance().getColumns(); i++) {
            actualColumnIndex = getActualColumn(i);

            if(i < 10) {
                firstRow[actualColumnIndex] = getNumberSecondDigitAsChar(i + 1);
            } else {
                firstRow[actualColumnIndex - 1] = getNumberFirstDigitAsChar(i + 1);
                firstRow[actualColumnIndex] = getNumberSecondDigitAsChar(i + 1);
            }
        }
    }

    // Return the numbers first digit as a char
    private char getNumberFirstDigitAsChar(int number) {
        return Character.forDigit(number / 10, 10);
    }

    // Return the numbers second digit as a char
    private char getNumberSecondDigitAsChar(int number) {
        return Character.forDigit(number % 10, 10);
    }

    private int getActualRow(int row) {
        return row + 1;
    }

    private int getActualColumn(int column) {
        return (column + 1) * 4 ;
    }
}
