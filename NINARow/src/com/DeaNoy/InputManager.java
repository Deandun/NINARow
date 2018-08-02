package com.DeaNoy;

import Logic.Enums.ePlayerType;
import Logic.Models.GameSettings;
import Logic.Models.Player;

import java.util.InputMismatchException;
import java.util.Scanner;

// Receive input from human or "computer" user.
public class InputManager {
    private Scanner scanner = new Scanner(System.in);

    public eGameOptions GetCommandFromPlayer() {
        boolean isInputValid;
        eGameOptions playerChoice = eGameOptions.Exit;
        int selectedOption;

        do {
            selectedOption = scanner.nextInt();
            isInputValid = eGameOptions.isIndexInRange(selectedOption);
        } while (!isInputValid);

        playerChoice = convertIndexToGameOption(selectedOption);

        return playerChoice;
    }

    public int GetColumnIndexFromPlayer(ePlayerType playerType) {
        int cellInput;
        boolean isValidColumn = false;

        if (playerType.equals(ePlayerType.Human)){
            System.out.println("Please select the column you wish to insert the disc into (1 to " + GameSettings.getInstance().getColumns());
            cellInput = getInputFromHumanPlayer();
        } else {
            // TODO: Call computer's algorithm
            cellInput = 1;
        }

        return cellInput;
    }

    private int getInputFromHumanPlayer() {
        int cellInput = 0;
        boolean isValidColumn = false;
        do {
            try{
                cellInput = scanner.nextInt();
                isValidColumn = cellInput >= 1 && cellInput <= GameSettings.getInstance().getColumns();

                if (!isValidColumn) {
                    System.out.println("Invalid input! Please enter a number between 1 and " + GameSettings.getInstance().getColumns());
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1 and " + GameSettings.getInstance().getColumns());
            }
        } while(!isValidColumn);

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

    public String GetFileNameFromPlayer() {
        boolean isInputValid = false;
        String inputString = new String();

        while(!isInputValid) {
            System.out.println("Please enter the game file's name (must be in xml format)");
            inputString = scanner.next();
            isInputValid = inputString != null && !inputString.isEmpty();
        }

        return inputString;
    }
}
