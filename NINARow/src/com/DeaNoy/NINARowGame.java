package com.DeaNoy;

public class NINARowGame {

    public void RunGame() {
        boolean shouldContinuePlaying = true;

        while(shouldContinuePlaying) {
            // Get valid input from user

            // Parse input from user (get the selected option)

            // Do selected option.

            // Check if should continue playing
        }
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