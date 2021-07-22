package generator.triples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SexTripleGenerator {

    private static List<String> sexes = new ArrayList<>(Arrays.asList("male", "female"));

    public static void generateSexFile(String filepath, List<String> subjects) throws IOException {
        Path p = Paths.get(filepath);
        Random r = new Random();
        String prefixLine = "@prefix  ub:  <http://swat.cse.lehigh.edu/onto/univ-bench.owl#> .\n\n";
        Files.write(p, prefixLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        for (String s : subjects) {
            int index = r.nextInt(sexes.size());
            String tripleToWrite = "<" + s + ">  ub:sex  \"" + sexes.get(index) + "\".\n";
            Files.write(p, tripleToWrite.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }
}
