package com.DeaNoy;

import Logic.Models.Configuration;
import Logic.Enums.ePlayerType;
import Logic.Models.Player;

import java.util.InputMismatchException;
import java.util.Scanner;

// Receive input from human or "computer" user.
public class InputManager {

    private Scanner scanner = new Scanner(System.in);

    public eGameOptions GetCommandFromPlayer(Player player) {
        boolean isInputValid;
        eGameOptions playerChoice = eGameOptions.Exit;
        int selectedOption;

        if (ePlayerType.Human.equals(player.getType())) { //handel user chosen option
            do {
                selectedOption = scanner.nextInt();
                isInputValid = playerChoice.isIndexInRange(selectedOption);
            } while (!isInputValid);

            playerChoice = convertIndexToGameOption(selectedOption);
        }
        else{
            playerChoice = eGameOptions.PlayTurn; // if computer's turn, always return "play turn" option.
        }
        return playerChoice;
    }

    public int GetColumnIndexFromPlayer(ePlayerType playerType) {
        int cellInput = 0;
        boolean isValidColumn = false;

        if (playerType.equals(ePlayerType.Human)){
            do {
                try{
                    cellInput = scanner.nextInt();
                    isValidColumn = cellInput >= Configuration.MIN_BOARD_COLS && cellInput <= Configuration.MAX_BOARD_COLS;
                } catch (InputMismatchException e) {
                    e.printStackTrace();
                }
           }while(!isValidColumn);
        }

        else{
            // if computer's turn, use algorithm to determine the input.
            cellInput = 0;
        }
        return cellInput;
    }

    private eGameOptions convertIndexToGameOption(int selectedOption) { //returns the chosen option
        switch (selectedOption){
            case 1:
                return eGameOptions.ReadGameFile;
            case 2:
                return eGameOptions.StartGame;
            case 3:
                return eGameOptions.ShowGameStatus;
            case 4:
                return eGameOptions.PlayTurn;
            case 5:
                return eGameOptions.ShowTurnHistory;
            default:
                return eGameOptions.Exit;
        }
    }

}
