package org.example;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    @Test
    void testAllGreen(){
        WordleGame game = new WordleGame("Hello", 6);
        LetterResult[] results = game.makeGuess("Hello");

        for (LetterResult res : results){
            assertEquals(LetterResult.CORRECT, res);
        }
        assertTrue(game.isWon());
    }

    @Test
    void testAnagram(){
        WordleGame game = new WordleGame("ascot", 6);
        LetterResult[] results = game.makeGuess("coats");

        for (LetterResult res: results){
            assertEquals(LetterResult.WRONG_POS, res);
        }
    }

    @Test
    void testAllGrey(){
        WordleGame game = new WordleGame("ghost", 6);
        LetterResult[] results = game.makeGuess("pride");

        for (LetterResult res: results){
            assertEquals(LetterResult.NOT_IN_WORD, res);
        }
    }

    @Test
    void testWinCondition(){
        WordleGame game = new WordleGame("stare", 6);
        LetterResult[] results = game.makeGuess("stare");

        assertTrue(game.isWon());
    }

    @Test
    void testRoundLimit(){
        final int NOROUNDS = 6;
        WordleGame game = new WordleGame("pinky", NOROUNDS);
        for (int i = 0; i < NOROUNDS; i++){
            game.makeGuess("guess");
        }

        assertFalse(game.isWon());
    }

    @Test
    void testInputValidation(){
        List<String> dummyWordList = List.of("STARE", "HELLO", "WORLD");
        String[] guesses = new String[6];

        // valid word
        assertTrue(GuessChecker.isValidInput(dummyWordList, guesses, "STARE", null));

        // too short
        assertFalse(GuessChecker.isValidInput(dummyWordList, guesses, "star", null));

        // too long
        assertFalse(GuessChecker.isValidInput(dummyWordList, guesses, "stares", null));

        // valid length but not valid word
        assertFalse(GuessChecker.isValidInput(dummyWordList, guesses, "zzzzz", null));
    }

    @Test
    void testSleepGuess(){
        WordleGame game = new WordleGame("PLANE", 6);
        LetterResult[] results = game.makeGuess("SLEEP");

        assertTrue((results[2] == LetterResult.WRONG_POS) && (results[3] == LetterResult.NOT_IN_WORD));
    }

    @Test
    void testEerieGuess(){
        WordleGame game = new WordleGame("CRANE", 6);
        LetterResult[] results = game.makeGuess("EERIE");

        assertEquals(results[0], LetterResult.NOT_IN_WORD);
        assertEquals(results[1], LetterResult.NOT_IN_WORD);
        assertEquals(results[4], LetterResult.CORRECT);
    }

    @Test
    void testProofGuess(){
        WordleGame game = new WordleGame("ROBOT", 6);
        LetterResult[] results = game.makeGuess("PROOF");

        assertEquals(results[2], LetterResult.WRONG_POS);
        assertEquals(results[3], LetterResult.CORRECT);
    }

}