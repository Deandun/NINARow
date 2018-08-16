package com.DeaNoy;

import Logic.ComputerPlayer.ComputerPlayerAlgo;
import Logic.Enums.eGameState;
import Logic.Enums.ePlayerType;
import Logic.Exceptions.InvalidFileInputException;
import Logic.Exceptions.InvalidUserInputException;
import Logic.Interfaces.IComputerPlayerAlgo;
import Logic.Interfaces.IGameStatus;
import Logic.Logic;
import Logic.Models.Cell;
import Logic.Models.GameSettings;
import Logic.Models.Player;
import Logic.Models.PlayerTurn;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;

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
        if(mLogic.isGameActive()) {
            try {
                if(mLogic.GetCurrentPlayer().getType() == ePlayerType.Human) {
                    mBoard.PrintBoard(); // Print board before turn only for human player.
                }

                int selectedColumn = mInputManager.GetColumnIndexFromPlayer(mLogic.GetCurrentPlayer().getType(), mLogic.GetCurrentPlayer()) - 1;
                PlayerTurn playerTurn = mLogic.PlayTurn(selectedColumn);
                char currentPlayerSign = ConsoleConfig.GetSignForPlayerID(playerTurn.getPlayer().getID());


                if (playerTurn.getGameState() == eGameState.Won) {
                    mLogic.getWinningSequencesMap().values().forEach(
                            (winningCellSequence) -> winningCellSequence.forEach(
                                    (cell) -> mBoard.UpdateBoard(cell.getRowIndex(), cell.getColumnIndex(), '!')
                            )
                    );
                    GameMenu.PrintVictoryMessage(playerTurn.getPlayer().getName());
                    shouldContinueGame = false;
                } else if (playerTurn.getGameState() == eGameState.Draw) {
                    GameMenu.PrintDrawMessage();
                    shouldContinueGame = false;
                }

                mBoard.UpdateBoard(playerTurn.getUpdatedCell().getRowIndex(), playerTurn.getUpdatedCell().getColumnIndex(), currentPlayerSign);
             } catch (InvalidUserInputException e) {
                GameMenu.PrintErrorMsg(e.getMessage());
            } catch (Exception e) {
                GameMenu.PrintErrorMsg(e.getMessage());
            }
        } else {
            GameMenu.GameNotActiveErrorMessage("play turn");
        }

        mBoard.PrintBoard(); // Print board after turn.

        return shouldContinueGame;
    }

    private void showGameStatus() {
        IGameStatus gameStatus = mLogic.GetGameStatus();
        GameMenu.PrintGameStatus(gameStatus, mBoard);
    }

    private void startGame() {
        mBoard.InitBoard();
        mLogic.StartGame();
        setComputerAlgo();
        ConsoleConfig.InitPlayerSigns(GameSettings.getInstance().getPlayers());
    }

    private void setComputerAlgo() {
        IComputerPlayerAlgo computerAlgo = new ComputerPlayerAlgo();
        computerAlgo.Init(mLogic.getBoard());
        mInputManager.setComputerPlayerAlgo(computerAlgo);
    }

    private void loadFile(){
        System.out.println("Start file chooser");
        FileChooser fileChooser = new FileChooser();

        try {
            mLogic.ReadGameFile(fileChooser.LoadFile());
            isConfigurationFileLoaded = true;
            mBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
            GameMenu.PrintGameStatus(mLogic.GetGameStatus(), mBoard);
        } catch (RuntimeException e){
            //TODO: handle exceptions with error popout window
            GameMenu.PrintErrorMsg(e.getMessage());
        } catch (InvalidFileInputException e) {
            //TODO: handle exceptions with error popout window
            GameMenu.PrintErrorMsg(e.getMessage());
        } catch (IOException e) {
            //TODO: handle exceptions with error popout window
            GameMenu.PrintErrorMsg(e.getMessage());
        } catch (JAXBException e) {
            //TODO: handle exceptions with error popout window
            GameMenu.PrintErrorMsg(e.getMessage());
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
                loadFile();
                break;
            case Exit:
                shouldContinueGame = false;
                break;
            default:
                GameMenu.PrintErrorMsg("Cannot execute - " + currentOption.getDescription() + " now because game details file is not loaded yet");
                break;
        }
        return shouldContinueGame;
    }

    private boolean executeNonActiveGameOption(eGameOptions currentOption) {
        boolean shouldContinueGame = true;

        switch(currentOption) {
            case StartGame:
                startGame();
                break;
            case ShowGameStatus:
                showGameStatus();
                break;
            case LoadExitsGame:
                loadExistsGame();
                break;
            default:
                GameMenu.PrintErrorMsg("Cannot execute " + currentOption.getDescription() + "now because game is not active");
                break;
        }
        return shouldContinueGame;
    }

    private boolean executeActiveGameOption(eGameOptions currentOption) {
        boolean shouldContinueGame = true;

        switch(currentOption) {
            case ReadGameFile:
                loadFile();
                break;
            case StartGame:
                startGame();
                break;
            case ShowGameStatus:
                showGameStatus();
                break;
            case PlayTurn:
                shouldContinueGame = playTurn();
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
                mLogic.SaveGame();
                System.out.println("Game saved successfully!");
            } catch (Exception e) {
                GameMenu.PrintErrorMsg("There has been a problem saving the game.");
            }
        }
    }

    private void loadExistsGame(){
        boolean isFileLoaded = false;
        if(mLogic.isGameActive()) {
            do {
                try {
                    mLogic.LoadExistsGame();
                    resetBoardFromHistory();
                    mBoard.PrintBoard();
                    isFileLoaded = true;
                    System.out.println("Game loaded successfully!");
                } catch (FileNotFoundException e) {
                    GameMenu.PrintErrorMsg("Cannot load game - no game to load (need to save a game before loading one).");
                } catch (Exception e) {
                    GameMenu.PrintErrorMsg("There has been a problem loading the game.");
                }

            }while(!isFileLoaded);
        } else {
            GameMenu.GameNotActiveErrorMessage("Load File");
        }
        showGameStatus();
    }

    private void resetBoardFromHistory() {
        mBoard = new Board(GameSettings.getInstance().getRows(), GameSettings.getInstance().getColumns());
        mLogic.GetTurnHistory().forEach(
            (turn) -> mBoard.UpdateBoard(turn.getUpdatedCell().getRowIndex(), turn.getUpdatedCell().getColumnIndex(),
                    ConsoleConfig.GetSignForPlayerID(turn.getPlayer().getID()))
        );
    }

    // Private inner static class that prints the game menu and notifications.
    private static class GameMenu {

        public static void PrintStartGameMenu(){ //display only start and exit menu options
            System.out.println(eGameOptions.SaveGame.getIndex() + ". " + eGameOptions.SaveGame.getDescription());
            System.out.println(eGameOptions.Exit.getIndex() + ". " + eGameOptions.Exit.getDescription());
        }

        private static void PrintWholeMenu() {
            System.out.println(System.lineSeparator() + "Please choose from the following options:");

            for (eGameOptions gameOption: eGameOptions.values()) {
                System.out.println(gameOption.getIndex() + ". " + gameOption.getDescription());
            }
        }

        private static void PrintVictoryMessage(String winnerName) {
            System.out.println(winnerName + " has won the game. Congratulations!");
        }

        private static void PrintDrawMessage() {
            System.out.println("The game has ended in a draw. This means every one's a winner!");
        }

        private static void PrintGameStatus(IGameStatus gameStatus, Board board){
            System.out.println("Game Status:");
            System.out.println("Board:");
            board.PrintBoard();
            System.out.format("Sequence required to win: %d%s", GameSettings.getInstance().getTarget(), System.lineSeparator());
            String gameStateString = gameStatus.getGameState() == eGameState.Active ? "Active" : "Inactive";
            System.out.println("Game state: " + gameStateString);

            if (gameStatus.getGameState() == eGameState.Active){
                PrintActiveGameStatus(gameStatus);
            }
        }

        private static void PrintActiveGameStatus(IGameStatus gameStatus) {
            // Print player that's currently playing
            System.out.println(gameStatus.getNameOfPlayerCurrentlyPlaying() + ": is currently playing.");

            // Print player names and their signs
            GameSettings.getInstance().getPlayers().forEach(
                    (player) ->
                        System.out.println(player.getName() + "'s sign is " + ConsoleConfig.GetSignForPlayerID(player.getID())
                                + ", and has played " + player.getTurnCounter() + " turns.")
            );

            // Print elapsed time
            printElapsedGameTime(gameStatus.getGameDuration());
         }

        private static void PrintErrorMsg(String str){
            System.out.println("***********************************************************************************");
            System.out.println(str);
            System.out.println("***********************************************************************************");
         }

        private static void GameNotActiveErrorMessage(String attemptedAction) {
            PrintErrorMsg("Cannot" + attemptedAction + " - Game is not active.");
         }

        private static void printElapsedGameTime(Duration gameDuration) {
            long minutes = gameDuration.toMinutes();
            long seconds = (gameDuration.toMillis() / 1000) % 60;

            System.out.format("Elapsed game time: %d:%d", minutes, seconds);
        }

    }

    // TODO: remove thsi DUMMY implementation of popout (not needed in console exercise).
    private void Popout() {
        mLogic.Popout(1);
        resetBoard();

    }

    private void playerQuit(Player quitter) {
        eGameState gameState = mLogic.PlayerQuit(quitter);

        resetBoard();
    }

    private void resetBoard() {
        Cell[][] cellArr = mLogic.getBoard().getCellArray();
        char cellSign;

        for(Cell[] row: cellArr) {
            for(Cell cell: row) {
                if(cell.getPlayer() == null) {
                    cellSign = ' ';
                } else {
                    cellSign = ConsoleConfig.GetSignForPlayerID(cell.getPlayer().getID());
                }

                mBoard.UpdateBoard(cell.getRowIndex(), cell.getColumnIndex(), cellSign);
            }
        }
    }
}