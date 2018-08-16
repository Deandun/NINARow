package com.DeaNoy;

import Logic.Enums.ePlayerType;
import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Models.GameSettings;
import Logic.Models.Player;

import java.util.InputMismatchException;
import java.util.Scanner;

// Receive input from human or "computer" user.
public class InputManager {
    private Scanner scanner = new Scanner(System.in);
    private IComputerPlayerAlgo mComputerPlayerAlgo;
    private static final int mErroInd = -1;

    public void setComputerPlayerAlgo(IComputerPlayerAlgo computerPlayerAlgo) {
        this.mComputerPlayerAlgo = computerPlayerAlgo;
    }


    public eGameOptions GetCommandFromPlayer() {
        boolean isInputValid;
        eGameOptions playerChoice;
        int selectedOption;

        do {
            System.out.println("What would you like to do? ");
            selectedOption = getIntegerInput();
            isInputValid = (selectedOption == mErroInd) ? false : eGameOptions.isIndexInRange(selectedOption);
        } while (!isInputValid);

        playerChoice = convertIndexToGameOption(selectedOption);

        return playerChoice;
    }

    private int getIntegerInput() {
        try { //handle exception in case input is not an integer
            return scanner.nextInt();
        }catch (InputMismatchException e){
            String invalidInput = scanner.next();
            System.out.println("INVALID INPUT! Please choose a legal option" + System.lineSeparator()
            + "Expected a numeric value, received: " + invalidInput + ".");
            return mErroInd;
        }
    }

    public int GetColumnIndexFromPlayer(ePlayerType playerType, Player player) {
        int cellInput = 0;

        if (playerType.equals(ePlayerType.Human)){
            System.out.println("Please select the column you wish to insert the disc into (1 to " + GameSettings.getInstance().getColumns() + ')');
            cellInput = getInputFromHumanPlayer();
        } else {
            System.out.println("Computer playing turn...");
            try {
                //TODO: exception not relevant here, only in new UI.
                cellInput = mComputerPlayerAlgo.getNextPlay(player).getmSelectedColumn() + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Computer has selected cell " + cellInput);
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
                String invalidInput = scanner.next();
                System.out.println("Invalid input (" + invalidInput + ")!" + System.lineSeparator()
                        +"Please enter a number between 1 and " + GameSettings.getInstance().getColumns());
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
            case 7:
                return eGameOptions.SaveGame;
            case 8:
                return eGameOptions.LoadExitsGame;
            default:
                return eGameOptions.Exit;
        }
    }

    public String GetFileNameFromPlayer() {
        return GetInputFromUser("Please enter the game file's name (must be in xml format)");
    }

    public String GetInputFromUser(String output) { //generic method that gets non empty input from user

        boolean isInputValid = false;
        String inputString = new String();

        while(!isInputValid) {
            System.out.println(output);
            inputString = scanner.next();
            isInputValid = inputString != null && !inputString.isEmpty();
        }

        return inputString;
    }

    public boolean IsUserWantToSaveTheGame() {
        String selectedOption;
        boolean isInputValid;
        do {
            selectedOption = GetInputFromUser("Do you want to save game? (y/n)").toLowerCase();
            isInputValid = GetYesOrNotAnswer(selectedOption);
        } while(!isInputValid);

        return selectedOption.contentEquals("y");
    }

    public boolean GetYesOrNotAnswer(String input){
        return input.toLowerCase().contentEquals("y") || input.toLowerCase().contentEquals("n");
    }
}
