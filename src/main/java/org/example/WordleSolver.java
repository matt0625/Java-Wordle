package org.example;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordleSolver {

    private final List<String> allWords = Stream.concat(JSONhandler.parseJsonFile("nonwordles.json").stream(), JSONhandler.parseJsonFile("wordles.json").stream()).toList();
    private List<String> validWords;
    private HashMap<Character, Integer> charMap;
    private HashSet<Character> exactCount;
    private static final double LN2 = Math.log(2);
    private Set<String> usedWords = new HashSet<>();

    public WordleSolver(){
        this.validWords =  Stream.concat(JSONhandler.parseJsonFile("nonwordles.json").stream(), JSONhandler.parseJsonFile("wordles.json").stream()).toList();
    }

    // constructor for unit testing
    public WordleSolver(List<String> validWords){
        this.validWords = validWords;
    }

    public void recordGuess(String guess){
        usedWords.add(guess.toLowerCase());
    }

    public void filterWords(LetterResult[] matches, String guess) {
        char[] guessArr = guess.toCharArray();

        // more efficient since string immutable
        StringBuilder myPattern = new StringBuilder();
        StringBuilder lookAheads = new StringBuilder();

        // creating Map for green/yellow counts, allowing us to deal with guesses with grey duplicate letters
        // and a set which will be added to if there is an extra grey character
        this.charMap = new HashMap<>();
        this.exactCount = new HashSet<>();

        for (int i = 0; i < matches.length; i++){
            if (matches[i] == LetterResult.CORRECT){
                myPattern.append(guessArr[i]);
                // if letter in the map then add 1 otherwise add it to the map with default value 0
                charMap.put(guessArr[i], charMap.getOrDefault(guessArr[i], 0) + 1);
            }
            else if (matches[i] == LetterResult.WRONG_POS){
                lookAheads.append("(?=.*" + guessArr[i] + ")");
                myPattern.append("[^" + guessArr[i] + "]");
                charMap.put(guessArr[i], charMap.getOrDefault(guessArr[i], 0) + 1);
            }
            else{
                // for the first pass, we know the grey letter isn't here, but it could be somewhere else if the guess has 2 of them
                // hence why we don't use '.'
                myPattern.append("[^" + guessArr[i] + "]");
            }

        }
        // finish by populating set now that map is fully populated
        for (int j = 0; j < matches.length; j++){
            if (matches[j] == LetterResult.NOT_IN_WORD){
                char guessChar = guessArr[j];
                // ensuring we don't ignore fully grey letters in the second pass
                charMap.putIfAbsent(guessChar, 0);
                exactCount.add(guessChar);
            }
        }

        String gyPattern = "^" + lookAheads.toString() + myPattern.toString() + "$";

        // first filter: green and yellows
        // applying the regex to filter down to those words which are green and yellow where the guess is
        // second filter: grey duplicates
        Pattern p = Pattern.compile(gyPattern);
        this.validWords = this.validWords.stream()
                .filter(word -> p.matcher(word).matches())
                .filter(this :: passesSecondFilter)
                .toList();
    }


    private boolean passesSecondFilter(String word){
        char[] wordArr = word.toCharArray();
        for (char c: charMap.keySet()){
            int wordCount = 0;
            for (int i = 0; i < word.length(); i++){
                if (wordArr[i] == c){
                    wordCount++;
                }
            }

            // if this character is in the exact count set the count must be equal since there are no more instances of this letter than there were in the guess
            if (exactCount.contains(c)){
                if (wordCount != charMap.get(c)){
                    return false;
                }
            }
            // now we need the number of occurences of the character in the candidate to be greater than or equal to the map count
            // since the map tells us the target has at LEAST map.get(c) green/yellow characters
            if (wordCount < charMap.get(c)){
                return false;
            }

        }
        return true;
    }

    public String getBestWord(){
        // skip long calculation if is the first guess, return a word that performs well given frequency analysis
        if (validWords.size() == allWords.size()){
            return "CRANE";
        }
        // even if a word is no longer 'valid', it might still give us the most information as the next guess
        // parallel stream to speed up the ~ 24 million operations on the large word list
        // comparator tells max to compare on entropy values
        return allWords.parallelStream()
                .filter(word -> !usedWords.contains(word))
                .max(Comparator.comparingDouble(this::calculateEntropy))
                .orElse(null);
    }

    public double calculateEntropy(String candidate){
        // i.e. 0-242 from getFeedBackPattern
        int[] possiblePatternIndices = new int[243];
        for (String word : this.validWords){
            // calculating how much the candidate matches to each valid word remaining
            int pattern = getFeedBackPattern(candidate, word);
            possiblePatternIndices[pattern]++;

        }

        double totalEntropy = 0;
        for (int i = 0; i < possiblePatternIndices.length; i++){
            if (possiblePatternIndices[i] > 0){
                double p = (double) possiblePatternIndices[i] / this.validWords.size();
                // change of base to base 2 here
                totalEntropy += (p * (Math.log(1/p)/LN2));
            }
        }

        //nudge this higher up the rankings if is a potential answer
        if (this.validWords.contains(candidate)){
            totalEntropy += 0.0001;
        }

        return totalEntropy;

    }

    // will return a number 0-242 that we will use as an array index based on green/yellow/grey values
    // 5 greens is 242, 5 greys is 0
    public int getFeedBackPattern(String guess, String target){
        int index = 0;
        // using pre-computed powers for time efficiency, powers[i] = 3^i
        int[] powers = {1, 3, 9, 27, 81};
        char[] guessArr = guess.toCharArray();
        char[] targetArr = target.toCharArray();
        // green pass
        for (int i = 0; i < guessArr.length; i++){
            if (guessArr[i] == targetArr[i]){
                index += 2 * powers[i];
                // marking these as 'used' and 'processed' so we avoid them in the yellow pass
                targetArr[i] = '#';
                guessArr[i] = '*';
            }
        }
        // yellow pass
        for (int j = 0; j < guessArr.length; j++){
            if (guessArr[j] != '*'){
                char current = guessArr[j];
                for (int k = 0; k < guessArr.length; k++){
                    if (current == targetArr[k] && targetArr[k] != '#'){
                        index += powers[j];
                        targetArr[k] = '#';
                        break;
                    }
                }
            }
        }
        return index;
    }
    public List<String> getValidWords() {return this.validWords;}

    public void setValidWords(List<String> words) {this.validWords = words;}

}
