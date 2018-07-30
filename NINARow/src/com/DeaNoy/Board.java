package com.DeaNoy;

import Logic.Models.Cell;

// The console UI board
public class Board {
    // TODO: get board size from singleton (from file).
    private char[][] mBoard;
    private int mRows;
    private int mCols;
    private int physicRowsSize;
    private int physicColsSize;


    /* for size=9 board is 19*10
      |1|2|3|4|5|6|7|8|9
     1| | | | | | | | |
     2| | | | | | | | |
     3| | | | | | | | |
     4| | | | | | | | |
     5| | | | | | | | |
     6| | | | | | | | |
     7| | | | | | | | |
     8| | | | | | | | |
     9| | | | | | | | |

    */

    public Board(int numRows, int numCols) {
        this.mRows = numRows;
        this.mCols = numCols;
        initBoard();
    }

    private void initBoard() { //TODO: need to check
        this.physicRowsSize = this.mRows + 1;
        this.physicColsSize = this.mCols * 2 + 1;
        mBoard = new char[physicRowsSize][physicColsSize];
        int index = 1;

        for (int i=1; i<physicColsSize; i+=2) {
            mBoard[0][i] = '|';
            mBoard[0][i+1] = (char)index;//casting is bothering in fingers
            index++;
        }
        for (int i=1; i<physicRowsSize; i++) {
            mBoard[i][0] = (char)index; //casting is bothering in fingers
            mBoard[i][1] = '|';
            index++;
        }


    }

    public void updateBoard(Cell cell) {
        // Check status (which player used the cell).
        // Update board with correct sign.
    }

    public void printBoard() {
        for(char[] row: mBoard){
            System.out.println(row);
        }
    }

    public void clear() {
        for (char[] column : mBoard) {
            for (char cell : column) {
                cell = ' ';
            }
        }
    }
}
