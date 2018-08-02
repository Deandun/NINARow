package com.DeaNoy;

import Logic.Enums.eGameState;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Interfaces.IGameStatus;
import Logic.Models.Cell;
import Logic.Models.GameSettings;
import Logic.Logic;
import Logic.Models.Player;
import Logic.Models.PlayerTurn;

import java.awt.*;
import java.util.Collection;

public class NINARowGame {
    private InputManager mInputManager;
    private Board mBoard;
    private Logic mLogic;

    public NINARowGame() {
        this.mInputManager = new InputManager();
        this.mLogic = new Logic();
    }

    public void RunGame() {
        GameSettings.getInstance().DummyInit();
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
        return mInputManager.GetCommandFromPlayer();
    }

    // Game commands implementation

    private void showTurnHistory() {
        // Create a new board to display the changes between turns.
        Board board = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
        System.out.println("Showing turn history:");
        mLogic.GetTurnHistory().forEach(
                (turn) -> {
                    Player player = turn.getPlayer();
                    System.out.println(player.getName() + " has inserted into column: " + turn.getUpdatedCell().getColumnIndex() + " (row " + turn.getUpdatedCell().getRowIndex() + ").");
                    board.UpdateBoard(turn.getUpdatedCell().getRowIndex(), turn.getUpdatedCell().getColumnIndex(), ConsoleConfig.GetSignForPlayerID(player.getID()));
                    board.PrintBoard();
                }
        );
    }

    private boolean playTurn() {
        boolean shouldContinueGame = true;
        int selectedColumn = mInputManager.GetColumnIndexFromPlayer(mLogic.GetCurrentPlayer().getType()) - 1;

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

            mBoard.UpdateBoard(playerTurn.getUpdatedCell().getRowIndex(), playerTurn.getUpdatedCell().getColumnIndex(), currentPlayerSign);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return shouldContinueGame;
    }

    private void showGameStatus() {
        IGameStatus gameStatus = mLogic.GetGameStatus();

        GameMenu.PrintGameStatus(gameStatus);
    }

    private void startGame() {
        mLogic.StartGame();
        ConsoleConfig.InitPlayerSigns(GameSettings.getInstance().getPlayers());
        mBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
        // Todo: Set computer algo
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
                mBoard.PrintBoard(); // TODO: temp print, for debugging. Find a better place to print
                break;
            case ShowGameStatus:
                showGameStatus();
                break;
            case PlayTurn:
                shouldContinueGame = playTurn();
                mBoard.PrintBoard(); // TODO: temp print, for debugging. Find a better place to print
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

        public static void PrintGameStatus(IGameStatus gameStatus) {
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
}