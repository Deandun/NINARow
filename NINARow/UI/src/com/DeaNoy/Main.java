package com.DeaNoy;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("~*~*~*~*~ Welcome to our FUN GAME! ~*~*~*~*~");
        NINARowGame ninaRowGame = new NINARowGame();
        System.out.println("Start a new game? (y/n)");

        while (shouldContinuePlaying()) {
            ninaRowGame.RunGame();
            System.out.println("Start a new game? (y/n)");
        }

        System.out.println("Thank you for playing. Goodbye! <3");
    }

    private static boolean shouldContinuePlaying() {
        String selectedOption;
        Scanner scanner = new Scanner(System.in);
        boolean isInputValid;

        do {
            selectedOption = scanner.next();
            isInputValid = selectedOption.toLowerCase().contentEquals("y") || selectedOption.toLowerCase().contentEquals("n");
            if (!isInputValid) {
                System.out.println("Please choose y/n");
            }
        } while (!isInputValid);

        return selectedOption.contentEquals("y");
    }

}