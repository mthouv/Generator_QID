package generator.triples;

import util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ZipcodeTripleGenerator {

    public static final List<String> zipcodes = new ArrayList<>(Arrays.asList("75000", "59000", "69000", "13000",
            "51100", "29200" , "06000", "06029", "67000", "54000", "33000", "31000", "34000", "66000",
            "44000", "35000", "63000", "38000", "37000"));


    public static void generateZipcodeFile(String filepath, List<String> subjects, List<String> zipcodes) throws IOException {
        Path p = Paths.get(filepath);
        Random r = new Random();
        String prefixLine = "@prefix  ub:  <http://swat.cse.lehigh.edu/onto/univ-bench.owl#> .\n\n";
        Files.write(p, prefixLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        for (String s : subjects) {
            int index = r.nextInt(zipcodes.size());
            String tripleToWrite = "<" + s + ">  ub:zipcode  \"" + zipcodes.get(index) + "\".\n";
            Files.write(p, tripleToWrite.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }



    public static void generateGenericZipcodeFile(String filepath, List<String> subjects, int startZipcode, int range) throws IOException {
        Path p = Paths.get(filepath);
        Random r = new Random();
        String prefixLine = "@prefix  ub:  <http://swat.cse.lehigh.edu/onto/univ-bench.owl#> .\n\n";
        Files.write(p, prefixLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        for (String s : subjects) {
            int zipcode = Util.getRandomNumberInRange(startZipcode, (startZipcode + range - 1), r);
            String tripleToWrite = "<" + s + ">  ub:zipcode  \"" + zipcode + "\".\n";
            Files.write(p, tripleToWrite.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }


}
