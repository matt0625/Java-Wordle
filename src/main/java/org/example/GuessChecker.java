package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GuessChecker {

    public static String validate(List<String> validWords, String[] guesses, String guess, int i){
        Scanner obj = new Scanner(System.in);
        while(!validWords.contains(guess) || Arrays.asList(guesses).contains(guess)){
            if (Arrays.asList(guesses).contains(guess)){
                System.out.println("Already guessed");
            }
            else{
                System.out.println("Not a valid word");
            }
            System.out.println(String.format("Guess (%d left): ", i));
            guess = obj.nextLine();
        }

        return guess;
    }
}
