package generator.hierarchies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SexHierarchyGenerator {

    private static List<String> sexes = new ArrayList<>(Arrays.asList("male", "female"));


    public static void generateSexHierarchy(String filepath) throws IOException {
        Path p = Paths.get(filepath);
        for (String s : sexes) {
            String lineToWrite = s + ";*\n";
            Files.write(p, lineToWrite.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

    }



    }
