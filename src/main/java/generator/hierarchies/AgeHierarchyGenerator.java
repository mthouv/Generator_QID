package generator.hierarchies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AgeHierarchyGenerator {


    public static void generateAgeHierarchy(String filepath) throws IOException {
       Path p = Paths.get(filepath);

        for (int i = 20; i <= 100; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(i).append(";");
            int numberOfTens = i / 10;
            int lowerBound = numberOfTens * 10;
            int upperBound = ((numberOfTens + 1) * 10) - 1;
            sb.append(lowerBound).append("-").append(upperBound).append(";");
            if (numberOfTens % 2 == 0) {
                upperBound = ((numberOfTens + 2) * 10) - 1;
                sb.append(lowerBound).append("-").append(upperBound).append(";");
            }
            else {
                lowerBound = (numberOfTens - 1) * 10;
                upperBound = ((numberOfTens + 1) * 10) - 1;
                sb.append(lowerBound).append("-").append(upperBound).append(";");
            }
            sb.append("*\n");
            Files.write(p, sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }
}
