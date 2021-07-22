package generator.triples;

import util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;

public class AgeTripleGenerator {

    public static void generateAgeFile(String filepath, List<String> subjects) throws IOException {
        Path p = Paths.get(filepath);
        Random r = new Random();
        String prefixLine = "@prefix  ub:  <http://swat.cse.lehigh.edu/onto/univ-bench.owl#> .\n\n";
        Files.write(p, prefixLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        for (String s : subjects) {
            int age = Util.getRandomNumberInRange(20, 100, r);
            String tripleToWrite = "<" + s + ">  ub:age  " + age + ".\n";
            Files.write(p, tripleToWrite.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

}
