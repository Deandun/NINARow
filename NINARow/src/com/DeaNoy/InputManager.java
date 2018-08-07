package com.DeaNoy;

import Logic.Enums.ePlayerType;
import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Models.GameSettings;
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

    public int GetColumnIndexFromPlayer(ePlayerType playerType) {
        int cellInput;

        if (playerType.equals(ePlayerType.Human)){
            System.out.println("Please select the column you wish to insert the disc into (1 to " + GameSettings.getInstance().getColumns() + ')');
            cellInput = getInputFromHumanPlayer();
        } else {
            System.out.println("Computer playing turn...");
            cellInput = mComputerPlayerAlgo.getNextPlay() + 1;
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

    public String GetPlayersType() {
        int playerInd = 1;
        String playerType;
        String computerPlayers;

        playerType = getPlayerType(playerInd); //get player1's type from user

        if (playerType.equals("computer")){ //in case player1 is computer - second player is automatically human
            System.out.println("Player1 is a computer and Player2 is a human");
            computerPlayers = "player 1";
        }
        else{
            playerType = getPlayerType(++playerInd);
            System.out.println("Player1 is a human and Player2 is a " + playerType);
            computerPlayers = playerType.equals("computer") ? "player2" : "none";
        }

        return computerPlayers;
    }

    private String getPlayerType(int playerInd) { //get player type from user
        boolean isInputValid;
        String playerType;

        do{
            playerType = GetInputFromUser("Please insert the type of player " + playerInd + " (Human/Computer)");
            isInputValid = playerType.toLowerCase().equals("human") || playerType.toLowerCase().equals("computer") ;
        }while(!isInputValid);

        return playerType.toLowerCase();
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
