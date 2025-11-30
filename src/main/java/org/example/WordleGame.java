package org.example;
import java.util.HashMap;
import java.util.Map;


public class WordleGame {
    private final String target;
    private int attempts;
    private final int maxAttempts;
    private boolean isWon;

    public WordleGame(String target, int maxAttempts) {
        this.target = target.toUpperCase();
        this.maxAttempts = maxAttempts;
        this.attempts = 0;
        this.isWon = false;
    }

    public LetterResult[] makeGuess(String guess) {
        guess = guess.toUpperCase();

        if (guess.length() != 5) {
            throw new IllegalArgumentException("Guess must be 5 letters");
        }

        attempts++;
        LetterResult[] results = new LetterResult[5];
        Map<Character, Integer> targetFreq = new HashMap<>();

        for (char c : target.toCharArray()) {
            targetFreq.put(c, targetFreq.getOrDefault(c, 0) + 1);
        }

        for (int i = 0; i < 5; i++) {
            char gChar = guess.charAt(i);
            if (gChar == target.charAt(i)) {
                results[i] = LetterResult.CORRECT;
                targetFreq.put(gChar, targetFreq.get(gChar) - 1);
            }
        }

        for (int i = 0; i < 5; i++) {
            if (results[i] == LetterResult.CORRECT) continue;

            char gChar = guess.charAt(i);
            if (targetFreq.containsKey(gChar) && targetFreq.get(gChar) > 0) {
                results[i] = LetterResult.WRONG_POS;
                targetFreq.put(gChar, targetFreq.get(gChar) - 1);
            } else {
                results[i] = LetterResult.NOT_IN_WORD;
            }
        }

        isWon = true;
        for (LetterResult r : results) {
            if (r != LetterResult.CORRECT) {
                isWon = false;
                break;
            }
        }

        return results;
    }

    public boolean isGameOver() {
        return isWon || attempts >= maxAttempts;
    }

    public boolean isWon() {
        return isWon;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getTarget() {
        return target;
    }
}
