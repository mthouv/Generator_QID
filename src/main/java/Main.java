import generator.triples.AgeTripleGenerator;
import generator.triples.SexTripleGenerator;
import generator.triples.ZipcodeTripleGenerator;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDBFactory;
import query.QueryUtil;
import reader.Reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Dataset dataset = null;
        Model model = ModelFactory.createDefaultModel();

        String beforeAnonFilename = null;
        String outPath = ".";
        int zipStart = 0;
        int zipRange = 100;

        String arg;
        try {
            for (int i = 0; i < args.length; i++) {
                arg = args[i++];
                System.out.println("Option : " + arg);
                switch (arg) {
                    case "-d":
                        while (i < args.length && !args[i].startsWith("-")){
                            Reader.readModelFromDirectory(args[i], model, null);
                            System.out.println(args[i]);
                            i++;
                        }
                        i--;
                        break;
                    case "-f":
                        while (i < args.length && !args[i].startsWith("-")){
                            model.read(args[i]);
                            System.out.println(args[i]);
                            i++;
                        }
                        i--;
                        break;
                    case "-tdb":
                        System.out.println(args[i]);
                        String directory = args[i];
                        dataset = TDBFactory.createDataset(directory);
                        model = dataset.getDefaultModel();
                        System.out.println("SIZE :" + model.size());
                        break;
                    case "-zipcode":
                        zipStart = Integer.parseInt(args[i++]);
                        zipRange = Integer.parseInt(args[i]);
                        break;
                    case "-out":
                        outPath = args[i];
                        System.out.println(args[i]);
                        break;
                    default:
                        System.out.println("Wrong option : " + arg);
                        break;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }


        List<String> fullProfs = QueryUtil.extractFullProfessorsFromGraph(model);
        List<String> assiProfs = QueryUtil.extractAssistantProfessorsFromGraph(model);
        List<String> assoProfs = QueryUtil.extractAssociateProfessorsFromGraph(model);

        List<String> profs = new ArrayList<>(fullProfs);
        profs.addAll(assiProfs);
        profs.addAll(assoProfs);

        System.out.println("FULL " + fullProfs.size());
        System.out.println("ASSI " + assiProfs.size());
        System.out.println("ASSO " + assoProfs.size());
        System.out.println("TOTAL " +  profs.size());

        AgeTripleGenerator.generateAgeFile(outPath + "/age.ttl", profs);

        SexTripleGenerator.generateSexFile(outPath + "/sex.ttl", profs);

        String zipPath = outPath + "/zip_" + zipStart + "_" + zipRange + ".ttl";
        ZipcodeTripleGenerator.generateGenericZipcodeFile(zipPath, profs, zipStart, zipRange);

    }

}
