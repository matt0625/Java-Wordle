package org.example;

import java.util.List;
import java.util.Random;

public class WordGenerator {

    public static String getWordFromFile(String filename){
        JSONhandler handler = new JSONhandler();
        List<String> result = handler.parseJsonFile(filename);

        Random rnd = new Random();
        int UB = result.toArray().length;

        return result.get(rnd.nextInt(UB));
    }
}
