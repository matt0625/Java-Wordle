package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleSolverTest {

    private WordleSolver solver;

    @BeforeEach
    void setUp(){
        solver = new WordleSolver(Arrays.asList("APPLE", "BEACH", "CLOUD", "STARE", "TREAD"));
    }

    //  FILTERING LOGIC TESTS

    @Test
    void testGreyFiltering(){
        // say guessing CLOUD results in all grey
        LetterResult[] results = {LetterResult.NOT_IN_WORD,LetterResult.NOT_IN_WORD, LetterResult.NOT_IN_WORD, LetterResult.NOT_IN_WORD, LetterResult.NOT_IN_WORD};

        solver.filterWords(results, "CLOUD");
        List<String> remaining = solver.getValidWords();

        // should remove guessed word
        assertFalse(remaining.contains("CLOUD"));

        // should remove APPLE because of L
        assertFalse(remaining.contains("APPLE"));

        // should keep STARE as no common letters with the all-grey guess
        assertTrue(remaining.contains("STARE"));

        // should remove TREAD because of D
        assertFalse(remaining.contains("TREAD"));
    }

    @Test
    void testGreenAndYellowFiltering(){
        // guess GRAFT with R, T yellow and A green, rest grey
        LetterResult[] results = {
                LetterResult.NOT_IN_WORD,
                LetterResult.WRONG_POS,
                LetterResult.CORRECT,
                LetterResult.NOT_IN_WORD,
                LetterResult.WRONG_POS
        };

        solver.filterWords(results, "GRAFT");
        List<String> remaining = solver.getValidWords();

        // STARE should be kept
        assertTrue(remaining.contains("STARE"));
        // STARE should be the only word kept
        assertEquals(1, remaining.size());
    }

    @Test
    void testDuplicateLetterExactCount(){
        // this is testing the second filer
        // i.e. that the solver correctly identifies that a word has exactly a certain number of a specific letter
        solver.setValidWords(Arrays.asList("ABIDE", "ABBEY", "BABEL", "BASIC"));

        // target: ABIDE, guess: BABES
        LetterResult[] results = {
                LetterResult.WRONG_POS,
                LetterResult.WRONG_POS,
                LetterResult.NOT_IN_WORD, // grey as only 1 B should exist
                LetterResult.WRONG_POS,
                LetterResult.NOT_IN_WORD
        };

        solver.filterWords(results, "BABES");
        List<String> remaining = solver.getValidWords();

        // both have 2 Bs so should be removed
        assertFalse(remaining.contains("ABBEY"));
        assertFalse(remaining.contains("BABEL"));

        // only has 1 B and no other letter conflicts so should stay
        assertTrue(remaining.contains("ABIDE"));
    }

    @Test
    void testExcludedGuessedWords(){
        // testing that the solver does not recommend a word already guessed
        // was a bug run into frequently when making the project

        solver.setValidWords(Arrays.asList("APPLE", "APPLY"));
        solver.recordGuess("AAHED");

        String hint = solver.getBestWord();

        // AAHED has a high entropy and is a valid word, but has already been guessed
        assertNotEquals("AAHED", hint);
    }

    // FEEDBACK PATTERN TESTS
    @Test
    void testAllGreenFeedback(){
        assertEquals(242 ,solver.getFeedBackPattern("SPEED", "SPEED"));
    }

    @Test
    void testAllGreyFeedback(){
        assertEquals(0, solver.getFeedBackPattern("AAAAA", "ZZZZZ"));
    }

    @Test
    void testGreenAndYellowFeedback(){
        // target: GHOST with guess: STOOP should have feedback =
        // (1 * 3^0) + (1 * 3^1) + (2 * 3^2) = 22
        // ^ see getFeedBackPattern()
        assertEquals(22, solver.getFeedBackPattern("STOOP", "GHOST"));
    }

    // ENTROPY LOGIC TESTS
    @Test
    void testEntropyCalculation(){
        solver.setValidWords(Arrays.asList("APPLE", "APPLY"));

        // guess: APPLE will put APPLE in the green bucket (242)
        // and APPLY in another bucket
        // which should split the pool perfectly into 2 and yield 1 bit
        double entropy = solver.calculateEntropy("APPLE");

        // log2(2) = 1.0
        // using a delta for floating point accuracies
        assertEquals(1.0, entropy, 0.001);

        // guess: ZZZZZ will return all grey for both words, 0 bits
        assertEquals(0.0, solver.calculateEntropy("ZZZZZ"), 0.001);
    }

    @Test
    void testParallelDeterminism(){
        // check that parallelStream() is deterministic
        // i.e. it doesn't crash or produce a different result when run more than once
        solver.setValidWords(Arrays.asList("STARE", "TREAD", "ROAST", "SPARE", "TABLE"));

        String firstRun = solver.getBestWord();
        String secondRun = solver.getBestWord();

        assertEquals(firstRun, secondRun);
    }
}