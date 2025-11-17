package org.example;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        boolean won = false;
        Scanner obj = new Scanner(System.in);
        String target = WordGenerator.getWordFromFile("wordles.json");
        // plan to merge sort this list to then use binary search which should be quicker given multiple consecutive games
        List<String> validWords = Stream.concat(JSONhandler.parseJsonFile("nonwordles.json").stream(), JSONhandler.parseJsonFile("wordles.json").stream()).toList();
        System.out.println(target);
        String[] guesses = new String[6];

        for (int i = 6; i > 1; i--){
            System.out.println(String.format("Guess (%d left): ", i));
            String guess = obj.nextLine();

            guess = GuessChecker.validate(validWords, guesses, guess, i);
            guesses[i-1] = guess;

            if (Objects.equals(guess, target)){
                System.out.println("You win!");
                won = true;
                break;
            }
        }

        if (!won){
            System.out.println("You lost :(");
        }
    }
}
