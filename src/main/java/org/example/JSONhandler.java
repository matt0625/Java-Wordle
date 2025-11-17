package org.example;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;

public class JSONhandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> parseJsonFile(String filename){
        try{
            String content = Files.readString(Paths.get(filename));
            List<String> result = mapper.readValue(content ,new TypeReference<List<String>>() {});
            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
