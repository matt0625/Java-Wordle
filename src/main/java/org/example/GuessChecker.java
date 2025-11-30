package org.example;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

// clear up class/method names
// e.g. getNextValidInput
public class GuessChecker {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";


    public static boolean isValidInput(List<String> validWords, String[] guesses, String guess, JFrame frame){
            if (guess.length() != 5) {
                JOptionPane.showMessageDialog(frame, "Word must be 5 letters!");
                return false;
            }


            if (Arrays.asList(guesses).contains(guess)) {
                JOptionPane.showMessageDialog(frame, "Already guessed!");
                return false;
            }

            if (!validWords.contains(guess)) {
                JOptionPane.showMessageDialog(frame, "Not a valid word");
                return false;
            }

            return true;
        }

    }

