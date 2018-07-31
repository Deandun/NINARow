package com.DeaNoy;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Models.Cell;
import Logic.Models.GameSettings;
import Logic.Logic;
import Logic.Models.PlayerTurn;

import java.awt.*;

public class NINARowGame {
    private InputManager mInputManager;
    private Board mBoard;
    private Logic mLogic;

    public NINARowGame() {
        this.mInputManager = new InputManager();
        this.mLogic = new Logic();
    }

    public void RunGame() {
        boolean shouldContinuePlaying = true;
        eGameOptions currentOption;

        //TODO: read player details from user

        while(shouldContinuePlaying) {
            GameMenu.PrintMenu();
            currentOption = getCommandFromPlayer();
            shouldContinuePlaying = executeOption(currentOption);
        }
    }

    private eGameOptions getCommandFromPlayer() {
        // TODO: check if the selected option is valid (e.g. selected start game before game was loaded, etc).
        return mInputManager.GetCommandFromPlayer(mLogic.GetCurrentPlayer());
    }

    // Game commands implemenetation

    private void showTurnHistory() {

    }

    private boolean playTurn() {
        boolean shouldContinueGame = true;
        int selectedColumn = mInputManager.GetColumnIndexFromPlayer(mLogic.GetCurrentPlayer().getType());

        try {
            PlayerTurn playerTurn = mLogic.PlayTurn(selectedColumn);
            char currentPlayerSign = ConsoleConfig.GetSignForPlayerID(playerTurn.getPlayer().getID());

            if(playerTurn.getGameState() == eGameState.Won) {
                System.out.println("Player " + playerTurn.getPlayer().getName() + " has won the game!");
                shouldContinueGame = false;
            } else if (playerTurn.getGameState() == eGameState.Draw) {
                System.out.println("The game has ended in a draw. Everyone's a winner!");
                shouldContinueGame = false;
            }

            mBoard.UpdateBoard(playerTurn.getCellRow(), playerTurn.getCellColumn(), currentPlayerSign);
        } catch (Exception e) {
            // TODO:
        }

        return shouldContinueGame;
    }

    private void showGameStatus() {
        //Logic.Logic.GameStatus gameStatus = mLogic.GetGameStatus();

        //TODO: Change GameMenu.PrintGameStatus to receive gameStatus and print the required details
    }

    private void startGame() {
        mLogic.StartGame();
        ConsoleConfig.InitPlayerSigns(GameSettings.getInstance().getPlayers());
        mBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
    }

    private void readGameFile() {
        String fileName = mInputManager.GetFileNameFromPlayer();
        try {
            mLogic.ReadGameFile(fileName);
        } catch (InvalidFileInputException e) {
            //TODO: informative message to the user.
        }
    }


    // Helper functions

    private boolean executeOption(eGameOptions currentOption) {
        boolean shouldContinueGame = true;

        switch(currentOption) {
            case ReadGameFile:
                readGameFile();
                break;
            case StartGame:
                startGame();
                mBoard.PrintBoard(); // TODO: temp print, for debugging.
                break;
            case ShowGameStatus:
                showGameStatus();
                break;
            case PlayTurn:
                shouldContinueGame = playTurn();
                mBoard.PrintBoard(); // TODO: temp print, for debugging.
                break;
            case ShowTurnHistory:
                showTurnHistory();
                break;
            case Exit:
                shouldContinueGame = false;
                break;
        }

        return shouldContinueGame;
    }

    // Private inner static class that prints the game menu and notifications.
    private static class GameMenu {

        public static void PrintMenu() {
            System.out.println("Please choose from the following options:");

            for (eGameOptions gameOption: eGameOptions.values()) {
                System.out.println(gameOption.getIndex() + ". " + gameOption.getDescription());
            }
        }

        public static void PrintVictoryMessage(String winnerName) {
            System.out.println(winnerName + " has won the game. Congratulations!");
        }

        public static void PrintDrawMessage() {
            System.out.println("The game has ended in a draw. This means every one's a winner!");
        }

        public static void PrintGameStatus(int activePlayerIndex, String[] playerNames, char[] playerBoardSigns, int player1TurnCounter, int player2TurnCounter, String timeSinceGameStarted) {
            System.out.println("Game status:" + System.lineSeparator() +
                                "Player " + playerNames[activePlayerIndex] + " is currently playing." + System.lineSeparator() +
                                playerNames[0] + "'s board sign: " + playerBoardSigns[0] + "." + System.lineSeparator() +
                                playerNames[1] + "'s board sign: " + playerBoardSigns[1] + "." + System.lineSeparator() +
                                playerNames[0] + " has played " + player1TurnCounter + " turns." + System.lineSeparator() +
                                playerNames[1] + " has played " + player2TurnCounter + " turns." + System.lineSeparator() +
                                "Time since game started: " + timeSinceGameStarted + ".");
        }
    }
}