package com.DeaNoy;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Interfaces.IGameStatus;
import Logic.Models.GameSettings;
import Logic.Logic;
import Logic.Models.Player;
import Logic.Models.PlayerTurn;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NINARowGame {
    private InputManager mInputManager;
    private Board mBoard;
    private Logic mLogic;
    private boolean isConfigurationFileLoaded = false;

    public NINARowGame() {
        this.mInputManager = new InputManager();
        this.mLogic = new Logic();
    }

    public void RunGame() {
        boolean shouldContinuePlaying = true;
        eGameOptions currentOption;

        while(shouldContinuePlaying) {
            GameMenu.PrintWholeMenu();
            currentOption = getCommandFromPlayer();
            shouldContinuePlaying = executeOption(currentOption);
        }
    }

    private eGameOptions getCommandFromPlayer() {
        return mInputManager.GetCommandFromPlayer();
    }

    // Game commands implementation

    private void showTurnHistory() {
        // Create a new board to display the changes between turns.
        Board board = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
        if (mLogic.GetGameStatus().getTurn() > 0){
            System.out.println("Showing turn history:");
            mLogic.GetTurnHistory().forEach(
                    (turn) -> {
                        Player player = turn.getPlayer();
                        System.out.println(player.getName() + " has inserted into column: " + (turn.getUpdatedCell().getColumnIndex() + 1));
                        board.UpdateBoard(turn.getUpdatedCell().getRowIndex(), turn.getUpdatedCell().getColumnIndex(), ConsoleConfig.GetSignForPlayerID(player.getID()));
                        board.PrintBoard();
                    }
            );
        }
        else{
            System.out.println("No turns have been played");
        }

    }

    private boolean playTurn() {
        boolean shouldContinueGame = true;

        try {
            int selectedColumn = mInputManager.GetColumnIndexFromPlayer(mLogic.GetCurrentPlayer().getType()) - 1;
            PlayerTurn playerTurn = mLogic.PlayTurn(selectedColumn);
            char currentPlayerSign = ConsoleConfig.GetSignForPlayerID(playerTurn.getPlayer().getID());

            if (playerTurn.getGameState() == eGameState.Won) {
                System.out.println("Player " + playerTurn.getPlayer().getName() + " has won the game!");
                shouldContinueGame = false;
            } else if (playerTurn.getGameState() == eGameState.Draw) {
                System.out.println("The game has ended in a draw. Everyone's a winner!");
                shouldContinueGame = false;
            }

            mBoard.UpdateBoard(playerTurn.getUpdatedCell().getRowIndex(), playerTurn.getUpdatedCell().getColumnIndex(), currentPlayerSign);
        }catch (NullPointerException e){
            printErrorMsg("Game is not active");
        }catch (Exception e) {
            printErrorMsg(e.getMessage());
        }

        return shouldContinueGame;
    }

    private void showGameStatus() {
       try{
            IGameStatus gameStatus = mLogic.GetGameStatus();

            GameMenu.PrintGameStatus(gameStatus, mLogic.GetGameState());
        }catch (NullPointerException e){
            printErrorMsg("Game is not active");
        }
    }

    private void startGame() {
        GameSettings.getInstance().clear(); //clear all previous game settings
        mBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
        String computerPlayer = mInputManager.GetPlayersType();
        initPlayer(computerPlayer);

        try {
            mLogic.StartGame();
        } catch (NoSuchFieldException e) {
            System.out.println(e.getMessage());
        }

        ConsoleConfig.InitPlayerSigns(GameSettings.getInstance().getPlayers());
    }

    private void initPlayer(String computerPlayer) { //initialize players in game settings
        if (computerPlayer.contains("1")){
            GameSettings.getInstance().InitPlayers("Computer" , "Human");
        }
        else if (computerPlayer.contains("2")){
            GameSettings.getInstance().InitPlayers("Human", "Computer");
        }
        else{
            GameSettings.getInstance().InitPlayers("Human", "Human");
        }
    }

    private void readGameFile() {
        if (!mLogic.isGameActive()) {
            String fileName = mInputManager.GetFileNameFromPlayer();

            if (fileName.endsWith(".xml")) {
                try {
                    mLogic.ReadGameFile(fileName);
                    isConfigurationFileLoaded = true;
                } catch (FileNotFoundException e) {
                    printErrorMsg("Couldn't find " + fileName + " file!");
                } catch (InvalidFileInputException e) {
                    printErrorMsg("Couldn't find " + fileName + " file!");
                } catch (JAXBException e) {
                    printErrorMsg("The " + fileName + "is in a wrong format, couldn't convert it to object");
                } catch (IOException e) {
                    printErrorMsg("DEBUG ERROR: General exception"); //TODO: add message to user
                }
            } else {
                printErrorMsg("Couldn't load file! File name should ends with .xml");
                isConfigurationFileLoaded = true;
            }
        }
        else{
            printErrorMsg("Sorry, you cannot load a new file when game is active");
        }
    }

    // Helper functions

    private boolean executeOption(eGameOptions currentOption) {
        boolean shouldContinueGame = true;

        if (isConfigurationFileLoaded){
             if (mLogic.isGameActive()) {
                 shouldContinueGame = executeActiveGameOption(currentOption);
             }
             else{
                 shouldContinueGame = executeNonActiveGameOption(currentOption);
             }
        }
        else { //lock options to choose commands when user hasn't chose 1 yet
            shouldContinueGame = executeFirstOptionOnly(currentOption);
        }
        return shouldContinueGame;
    }

    private boolean executeFirstOptionOnly(eGameOptions currentOption) {
        boolean shouldContinueGame = true;

        switch (currentOption) {
            case ReadGameFile:
                readGameFile();
                break;
            case Exit:
                shouldContinueGame = false;
                break;
            default:
                printErrorMsg("Cannot execute ." + currentOption.getDescription() + " now because game details file is not loaded yet");
                break;

        }
        return shouldContinueGame;
    }

    private boolean executeNonActiveGameOption(eGameOptions currentOption) {
        boolean shouldContinueGame = true;

        switch(currentOption) {
            case StartGame:
                startGame();
                mBoard.PrintBoard(); // TODO: temp print, for debugging. Find a better place to print
                break;
            case ShowGameStatus:
                showGameStatus();
                //todo: Print board // ?
                break;
            case LoadExitsGame:
                loadExistsGame();
                break;
            default:
                printErrorMsg("Cannot execute " + currentOption.getDescription() + "now because game is not active");
                break;
        }
        return shouldContinueGame;
    }

    private boolean executeActiveGameOption(eGameOptions currentOption) {
        boolean shouldContinueGame = true;

        switch(currentOption) {
            case ReadGameFile:
                readGameFile();
                break;
            case StartGame:
                startGame();
                mBoard.PrintBoard(); // TODO: temp print, for debugging. Find a better place to print
                break;
            case ShowGameStatus:
                showGameStatus();
                break;
            case PlayTurn:
                shouldContinueGame = playTurn();
                mBoard.PrintBoard();
                break;
            case ShowTurnHistory:
                showTurnHistory();
                break;
            case SaveGame:
                saveGame();
                break;
            case LoadExitsGame:
                loadExistsGame();
                break;
            default:
                shouldContinueGame = false;
                break;
        }
        return shouldContinueGame;
    }

    private void saveGame() {
        if (mInputManager.IsUserWantToSaveTheGame()){
            try {
                mLogic.saveGame();
            } catch (Exception e) { //TODO
                e.printStackTrace();
            }
        }
    }

    private void loadExistsGame(){
        boolean isFileLoaded = false;

        do {
            try {
                mLogic.loadExistsGame();
                isFileLoaded = true;
                System.out.println("File Loaded Successfully! ");
            } catch (FileNotFoundException e){
                printErrorMsg(e.getMessage() + "ERROR couldn't find the file");
            } catch (IOException e) {
                printErrorMsg(e.getMessage()); //TODO
            } catch (ClassNotFoundException e) {
                printErrorMsg(e.getMessage() + " ERROR reading from file :(");
            } catch (Exception e) {
                printErrorMsg(e.getMessage()); //TODO
            }

        }while(!isFileLoaded);

        showGameStatus();
    }

    // Private inner static class that prints the game menu and notifications.
    private static class GameMenu {

        public static void PrintstartGameMenu(){ //display only start and exit menu options TODO: use
            System.out.println(eGameOptions.SaveGame.getIndex() + ". " + eGameOptions.SaveGame.getDescription());
            System.out.println(eGameOptions.Exit.getIndex() + ". " + eGameOptions.Exit.getDescription());
        }

        public static void PrintWholeMenu() {
            System.out.println("\nPlease choose from the following options:");

            for (eGameOptions gameOption: eGameOptions.values()) {
                System.out.println(gameOption.getIndex() + ". " + gameOption.getDescription());
            }
        }

        public static void PrintVictoryMessage(String winnerName) { //TODO: use
            System.out.println(winnerName + " has won the game. Congratulations!");
        }

        public static void PrintDrawMessage() { //TODO: use
            System.out.println("The game has ended in a draw. This means every one's a winner!");
        }

        public static void PrintGameStatus(IGameStatus gameStatus, eGameState gameState){
            //TODO: print board
            if (gameState.equals(eGameState.Active)){
                System.out.println("Game is active");
                PrintActiveGameStatus(gameStatus);
            }
            else{
                System.out.println("Game is not active");
            }
        }
        public static void PrintActiveGameStatus(IGameStatus gameStatus) {
            // Print player that's currently playing

            System.out.println("Game status:" + System.lineSeparator() +
                        gameStatus.getNameOfPlayerCurrentlyPlaying() + ": is currently playing.");

            // Print player names and their signs
            GameSettings.getInstance().getPlayers().forEach(
                    (player) -> System.out.println(player.getName() + "'s sign is: " + ConsoleConfig.GetSignForPlayerID(player.getID()))
            );
            // Print elapsed time
            System.out.println("Elapsed game time: " + gameStatus.getGameDuration());
         }
    }
    private void printErrorMsg(String str){
        System.out.println("***********************************************************************************");
        System.out.println(str);
        System.out.println("***********************************************************************************");
    }
}