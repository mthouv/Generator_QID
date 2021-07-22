package generator.hierarchies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZipcodeHierarchyGenerator {

    public static final List<String> zipcodes = new ArrayList<>(Arrays.asList("75000", "59000", "69000", "13000",
            "51100", "29200" , "06000", "06029", "67000", "54000", "33000", "31000", "34000", "66000",
            "44000", "35000", "63000", "38000", "37000"));


    public static void generateZipcodeHierarchy(String filepath) throws IOException {
        Path p = Paths.get(filepath);
        for (String z : zipcodes) {
            StringBuilder sb = new StringBuilder(z);
            String s = z + ";";
            Files.write(p, s.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            int lastDigitPosition = sb.length() - 1;
            while (lastDigitPosition != 0) {
                sb.setCharAt(lastDigitPosition, '*');
                s = sb.toString() + ";";
                Files.write(p, s.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                lastDigitPosition--;
            }
            sb.setCharAt(lastDigitPosition, '*');
            s = sb.toString() + "\n";
            Files.write(p, s.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }


    public static void generateGenericZipcodeHierarchy(String filepath, int startZipcode, int range) throws IOException {
        Path p = Paths.get(filepath);
        for (int i = startZipcode; i < (startZipcode + range); i++) {
            StringBuilder sb = new StringBuilder(String.valueOf(i));
            System.out.println("AAAAAAAAAAAA " + sb);
            String s = i + ";";
            Files.write(p, s.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            int lastDigitPosition = sb.length() - 1;
            while (lastDigitPosition != 0) {
                sb.setCharAt(lastDigitPosition, '*');
                s = sb.toString() + ";";
                Files.write(p, s.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                lastDigitPosition--;
            }
            sb.setCharAt(lastDigitPosition, '*');
            s = sb.toString() + "\n";
            Files.write(p, s.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

}
