package query;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import util.Counter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class QueryUtil {

    private static String fullProfessorsQueryString =
            "PREFIX ub: <http://swat.cse.lehigh.edu/onto/univ-bench.owl#>" +
                    "SELECT ?s " +
                    "WHERE {" +
                    "?s  a  ub:FullProfessor." +
                    "}";


    private static String associateProfessorsQueryString =
            "PREFIX ub: <http://swat.cse.lehigh.edu/onto/univ-bench.owl#>" +
                    "SELECT ?s " +
                    "WHERE {" +
                    "?s  a  ub:AssociateProfessor." +
                    "}";

    private static String assistantProfessorsQueryString =
            "PREFIX ub: <http://swat.cse.lehigh.edu/onto/univ-bench.owl#>" +
                    "SELECT ?s " +
                    "WHERE {" +
                    "?s  a  ub:AssistantProfessor." +
                    "}";


    public static List<QuerySolution> execQuery(Query query, Model model) {
        List<QuerySolution> solutionsList = new ArrayList<>();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext(); ) {
                QuerySolution soln = results.nextSolution();
                solutionsList.add(soln);
            }
        }
        return solutionsList;
    }


    public static List<String> extractFullProfessorsFromGraph(Model model) {
        Query query = QueryFactory.create(fullProfessorsQueryString);
        List<QuerySolution> querySolutions = execQuery(query, model);
        return querySolutions.stream()
                .map(qs -> qs.get("s").toString())
                .collect(Collectors.toList());
    }


    public static List<String> extractAssistantProfessorsFromGraph(Model model) {
        Query query = QueryFactory.create(assistantProfessorsQueryString);
        List<QuerySolution> querySolutions = execQuery(query, model);
        return querySolutions.stream()
                .map(qs -> qs.get("s").toString())
                .collect(Collectors.toList());
    }


    public static List<String> extractAssociateProfessorsFromGraph(Model model) {
        Query query = QueryFactory.create(associateProfessorsQueryString);
        List<QuerySolution> querySolutions = execQuery(query, model);
        return querySolutions.stream()
                .map(qs -> qs.get("s").toString())
                .collect(Collectors.toList());
    }


    public static List<String> retrieveGroupsURI(Model model) {
        List<String> res = new ArrayList<>();
        Query q = QueryFactory.create("select distinct ?o where { ?s <http://inGroup> ?o .}");
        List<QuerySolution> sols = execQuery(q, model);
        sols.forEach(s -> res.add(s.get("o").toString()));
        return res;
    }


    public static String createSelectClause(List<String> predicates) {
        StringBuilder selectClause = new StringBuilder("SELECT ?s ");
        int i;
        for (i = 0; i < predicates.size(); i++) {
            String attributeVariable = "?attr" + i;
            selectClause.append(attributeVariable).append(" ");
        }
        selectClause.append("\n");
        return selectClause.toString();
    }

    public static String createWhereClause(List<String> predicates) {
        StringBuilder whereClause = new StringBuilder("WHERE { ");
        for (int i = 0; i < predicates.size(); i++) {
            String attributeVariable = "?attr" + i;
            whereClause.append("?s ").append(predicates.get(i)).append(" ").append(attributeVariable).append(" . ");
        }
        return whereClause.toString();
    }


    public static String createQuery(List<String> predicates, String groupURI) {
        String selectClause = createSelectClause(predicates);
        StringBuilder whereClause = new StringBuilder(createWhereClause(predicates));
        if (groupURI != null) {
            whereClause.append(" ?s <http://inGroup> <").append(groupURI).append("> . ");
        }
        whereClause.append("} \n");
        return selectClause + whereClause.toString();
    }



    public static List<List<String>> retrieveResultsFromQuery(List<String> predicates, String groupURI, Model model) {
        Query q = QueryFactory.create(createQuery(predicates, groupURI));
        //System.out.println(createQuery(predicates, groupURI));
        List<QuerySolution> sols = execQuery(q, model);

        List<List<String>> results = sols.stream().map(qs -> {
            List<String> lst = new ArrayList<>();
            lst.add(qs.get("s").toString());
            for (int i = 0; i < predicates.size(); i++) {
                String attributeVariable = "attr" + i;
                String[] tab = qs.get(attributeVariable).toString().split("\\^\\^");
                lst.add(tab[0]);
            }
            return lst;
        }).collect(Collectors.toList());

        return results;
    }

    public static void flushResultsToCsv(List<List<String>> results, List<String> columnNames, String filepath) throws IOException {
        Path p = Paths.get(filepath);
        StringBuilder sb = new StringBuilder();
        String columnNamesLine = columnNames.stream().collect(Collectors.joining(";", "", "\n"));
        sb.append(columnNamesLine);
        for (List<String> l : results) {
            String line = l.stream().collect(Collectors.joining(";", "", "\n"));
            sb.append(line);
        }
        Files.write(p, sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }


/*
    public static List<String> execQueryCsvFormat(List<String> predicates, List<String> columnNames, Model model) {
        Query q = QueryFactory.create(createQuery(predicates));
        List<QuerySolution> sols = execQuery(q, model);

        List<String> lines = sols.stream().map(qs -> {
            StringBuilder sb = new StringBuilder();
            sb.append(qs.get("s").toString()).append(";");
            int i;
            for (i = 0; i < predicates.size() - 1; i++) {
                String attributeVariable = "attr" + i;
                String[] tab = qs.get(attributeVariable).toString().split("\\^\\^");
                sb.append(tab[0]).append(";");
            }
            String attributeVariable = "attr" + i;
            String[] tab = qs.get(attributeVariable).toString().split("\\^\\^");
            sb.append(tab[0]);
            return sb.toString();
        }).collect(Collectors.toList());

        List<String> res = new ArrayList<>();
        String firstLine = columnNames.stream().collect(Collectors.joining(";"));
        res.add(firstLine);
        res.addAll(lines);
        return res;
    }
*/


    public static String createOriginalTriplesString(List<String> originalData, List<String> predicates, List<Boolean> isNumerical) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < predicates.size(); i++) {
            sb.append("<").append(originalData.get(0)).append("> ").append(predicates.get(i)).append(" ");
            if (isNumerical.get(i)) {
                sb.append(originalData.get(i + 1)).append(" .\n");
            }
            else {
                sb.append("\"").append(originalData.get(i + 1)).append("\" .\n");
            }
        }
        return sb.toString();
    }


    public static String createBlankNodeIntervalTriples(String tab[], String subject, String predicate, Counter blankIdCounter) {
        String s =  "<http://blank" + blankIdCounter.getValue() + "> .\n" +
                "<http://blank" + blankIdCounter.getValue() + ">  <http://minValue> " + tab[0] + " .\n" +
                "<http://blank" + blankIdCounter.getValue() + ">  <http://maxValue> " + tab[1] + " .\n";

        return s;
    }


    public static String createTransformedTriplesString(List<String> transformedData, List<String> predicates,
                                                        List<Boolean> isNumerical, Counter blankIdCounter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < predicates.size(); i++) {
            sb.append("<").append(transformedData.get(0)).append("> ").append(predicates.get(i)).append(" ");

            String[] tab = transformedData.get(i + 1).split("-");
            if (tab.length > 1) {
                sb.append(createBlankNodeIntervalTriples(tab, transformedData.get(0), predicates.get(i), blankIdCounter));
                blankIdCounter.increment();
            }
            else {
                if (isNumerical.get(i) && !tab[0].equals("*")) {
                    /*if (tab[0].equals("*")) {
                        System.out.println("AAAAAAAAAA : " + i);
                    }*/
                    sb.append(tab[0]).append(" .\n");
                }
                else {
                    sb.append("\"").append(tab[0]).append("\" .\n");
                }
            }
        }
        return sb.toString();
    }


    public static List<String> computeGeneralizationQueries(List<List<String>> originalData, List<List<String>> transformedData, List<String> predicates,
                                                            List<Boolean> isNumerical, Counter blankIdCounter) {

        //Counter blankIdCounter = new Counter();
        List<String> updateQueries = new ArrayList<>();
        for (int i = 0; i < originalData.size(); i++) {
            List<String> od = originalData.get(i);
            List<String> td = transformedData.get(i);
            String originalTriples = createOriginalTriplesString(od, predicates, isNumerical);
            String transformedTriple = createTransformedTriplesString(td, predicates, isNumerical, blankIdCounter);
            StringBuilder sb = new StringBuilder("DELETE { ");
            sb.append(originalTriples).append("}\nINSERT { ").append(transformedTriple).append("}\nWHERE { ")
                    .append(originalTriples).append("}");
            updateQueries.add(sb.toString());
        }
        return updateQueries;
    }


    public static String createSelectClauseCountQuery(List<String> predicates) {
        StringBuilder selectClause = new StringBuilder("SELECT ");
        for (int i = 0; i < predicates.size(); i++) {
            String attributeVariable = "?attr" + i;
            selectClause.append(attributeVariable).append(" ");
        }
        selectClause.append("(COUNT(?s) as ?c) \n");
        return selectClause.toString();
    }


    public static String createGroupByClause(List<String> predicates) {
        StringBuilder groupByClause = new StringBuilder("GROUP BY  ");
        for (int i = 0; i < predicates.size(); i++) {
            String attributeVariable = "?attr" + i;
            groupByClause.append(attributeVariable).append(" ");
        }
        groupByClause.append("ORDER BY ?c");
        return groupByClause.toString();
    }


    public static String createWhereClauseAgeQueryBeforeAnon(int minValue, int maxValue, List<String> predicates) {
        StringBuilder whereClause = new StringBuilder("WHERE { ");
        for (int i = 0; i < predicates.size(); i++) {
            String attributeVariable = "?attr" + i;
            whereClause.append("?s ").append(predicates.get(i)).append(" ").append(attributeVariable).append(" . ");
        }
        whereClause.append("?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#age> ?o . ")
                        .append("FILTER(?o >= ").append(minValue).append(" && ?o < ").append(maxValue).append(") . }\n");
        return whereClause.toString();
    }


    public static String createWhereClauseAgeQuery(int minValue, int maxValue, String saGroup) {
        StringBuilder whereClause = new StringBuilder("WHERE { { ");

        whereClause.append("?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#age> ?o . ")
                .append("FILTER(?o >= ").append(minValue).append(" && ?o < ").append(maxValue).append(") . ");
        if (saGroup != null) {
            whereClause.append("?s <http://inGroup> <").append(saGroup).append("> . ");
        }

        whereClause.append(" } UNION { \n");

        whereClause.append("?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#age> ?b . ")
                .append("?b <http://minValue> ?min . ")
                .append("?b <http://maxValue> ?max . ")
                .append("FILTER(?min >= ").append(minValue).append(" && ?max < ").append(maxValue).append(") . ");

        if (saGroup != null) {
            whereClause.append("?s <http://inGroup> <").append(saGroup).append("> . ");
        }

        whereClause.append("}}");
        return whereClause.toString();
    }


    public static String createCountAgeQueryBeforeAnon(int minValue, int maxValue, List<String> predicates) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(createSelectClauseCountQuery(predicates));
        queryStr.append(createWhereClauseAgeQueryBeforeAnon(minValue, maxValue, predicates));
        if (!predicates.isEmpty()) {
            queryStr.append(createGroupByClause(predicates));
        }
        return queryStr.toString();
    }



    public static String createCountAgeQuery(int minValue, int maxValue, String saGroup) {
        String selectClause = "SELECT (COUNT(?s) as ?c) \n";
        String whereClause = createWhereClauseAgeQuery(minValue, maxValue, saGroup);

        return selectClause + whereClause;
    }


    public static String createWhereClauseAgeZipcodeQueryBeforeAnon(List<String> predicates, int minValue, int maxValue, String zipcode) {
        StringBuilder whereClause = new StringBuilder("WHERE { ");
        for (int i = 0; i < predicates.size(); i++) {
            String attributeVariable = "?attr" + i;
            whereClause.append("?s ").append(predicates.get(i)).append(" ").append(attributeVariable).append(" . ");
        }
        whereClause.append("?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#age> ?o . ")
                .append("FILTER(?o >= ").append(minValue).append(" && ?o < ").append(maxValue).append(") . ");

        whereClause.append("?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#zipcode> ?z . ")
                .append("FILTER regex(?z, \"^").append(zipcode).append("\") . ");

        whereClause.append("} ");
        return whereClause.toString();
    }


    public static String createCountAgeZipcodeQueryBeforeAnon(List<String> predicates, int minValue, int maxValue, String zipcode) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(createSelectClauseCountQuery(predicates));
        queryStr.append(createWhereClauseAgeZipcodeQueryBeforeAnon(predicates, minValue, maxValue, zipcode));
        if (!predicates.isEmpty()) {
            queryStr.append(createGroupByClause(predicates));
        }
        return queryStr.toString();
    }


    public static String createWhereClauseAgeZipcodeQuery(List<String> predicates, int minValue, int maxValue, String zipcode, List<String> saGroups) {
        StringBuilder whereClause = new StringBuilder("WHERE { { ");

        whereClause.append("?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#age> ?o . ")
                .append("FILTER(?o >= ").append(minValue).append(" && ?o < ").append(maxValue).append(") . ");

        whereClause.append("?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#zipcode> ?z . \n")
                .append("FILTER regex(?z, \"^").append(zipcode).append("\") . ");

        for (String g : saGroups) {
            whereClause.append("?s <http://inGroup> <").append(g).append("> . ");
        }
/*
        for (int i = 0; i < predicates.size(); i++) {
            String attributeVariable = "?attr" + i;
            whereClause.append("?s ").append(predicates.get(i)).append(" ").append(attributeVariable).append(" . ");
        }
*/
        whereClause.append(" } UNION { \n");

        whereClause.append("?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#age> ?b . ")
                .append("?b <http://minValue> ?min . ")
                .append("?b <http://maxValue> ?max . ")
                .append("FILTER(?min >= ").append(minValue).append(" && ?max < ").append(maxValue).append(") . ");

        whereClause.append("?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#zipcode> ?z . \n")
                .append("FILTER regex(?z, \"^").append(zipcode).append("\") . ");

        for (String g : saGroups) {
            whereClause.append("?s <http://inGroup> <").append(g).append("> . ");
        }
/*
        for (int i = 0; i < predicates.size(); i++) {
            String attributeVariable = "?attr" + i;
            whereClause.append("?s ").append(predicates.get(i)).append(" ").append(attributeVariable).append(" . ");
        }
*/
        whereClause.append("}}");
        return whereClause.toString();
    }


    public static String createCountAgeZipcodeQuery(List<String> predicates, int minValue, int maxValue, String zipcode, List<String> saGroups) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append(createSelectClauseCountQuery(predicates));
        queryStr.append(createWhereClauseAgeZipcodeQuery(predicates, minValue, maxValue, zipcode, saGroups));
        if (!predicates.isEmpty()) {
            queryStr.append(createGroupByClause(predicates));
        }
        return queryStr.toString();
    }



    public static String createCountQueryZipcode(String zipcodePrefix) {
        return
                "SELECT (COUNT(?s) as ?c) WHERE { ?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#zipcode> ?o . \n" +
                        "FILTER regex(?o, \"^" + zipcodePrefix + "\") }";
    }


    public static String getAttributeCardinality() {
        return
                "SELECT ?val ?card WHERE { " +
                        "?attribute <http://value> ?val ." +
                        "?attribute <http://cardinality> ?card ." +
                        "}";
    }

    public static Map<String, Integer> getMapAttributeToCardinality(Model model) {
        Map<String, Integer> map = new HashMap<>();
        Query query = QueryFactory.create(getAttributeCardinality());
        List<QuerySolution> sols = QueryUtil.execQuery(query, model);

        sols.forEach(qs -> {
            String attributeValue = qs.get("val").toString();
            int card = Integer.parseInt(qs.get("card").toString().split("\\^\\^")[0]);
            map.put(attributeValue, card);
        });
        return map;
    }


    public static String getAttributesInGroups() {
        return
                "SELECT ?val ?g WHERE { " +
                        "?g ?p ?attribute ." +
                        "?attribute <http://value> ?val ." +
                        "}";
    }

    public static Map<String, String> getMapAttributeToGroup(Model model) {
        Map<String, String> map = new HashMap<>();
        Query query = QueryFactory.create(getAttributesInGroups());
        List<QuerySolution> sols = QueryUtil.execQuery(query, model);

        sols.forEach(qs -> {
            String attribute = qs.get("val").toString();
            String group = qs.get("g").toString();
            map.put(attribute, group);
        });
        return map;
    }



    public static String totalCardinalityByGroupRequest() {
        return
                "SELECT distinct ?g (sum(?card) as ?sum) where {" +
                        "?g ?p ?attr ." +
                        "?attr <http://cardinality> ?card ." +
                        "} group by ?g";
    }


    public static Map<String, Integer> getMapGroupCardinality(Model model) {
        Map<String, Integer> totalCardinalityByGroups = new HashMap<>();
        Query query = QueryFactory.create(QueryUtil.totalCardinalityByGroupRequest());
        List<QuerySolution> sols = QueryUtil.execQuery(query, model);

        sols.forEach(qs -> {
            String groupName = qs.get("g").toString();
            int cardTotal = Integer.parseInt(qs.get("sum").toString().split("\\^\\^")[0]);
            totalCardinalityByGroups.put(groupName, cardTotal);
        });
        return totalCardinalityByGroups;
    }


    public static List<String> getSensitiveAttributes(Model model) {
        List<String> sensitiveAttributes = new ArrayList<>();
        String queryStr = "select distinct ?o where { ?s <http://value> ?o . }";
        Query q = QueryFactory.create(queryStr);

        execQuery(q, model).forEach(qs -> sensitiveAttributes.add(qs.get("o").toString()));
        return sensitiveAttributes;
    }


    public static List<String> getZipcodes(Model model) {
        List<String> zipcodes = new ArrayList<>();
        String queryStr = "select distinct ?o where { ?s <http://swat.cse.lehigh.edu/onto/univ-bench.owl#zipcode> ?o . }";
        Query q = QueryFactory.create(queryStr);

        execQuery(q, model).forEach(qs -> zipcodes.add(qs.get("o").toString()));
        return zipcodes;
    }




    public static int countSubjectsWithAgeInGroup(int minValue, int maxValue, String saGroup, Model model) {
        Query q = QueryFactory.create(QueryUtil.createCountAgeQuery(minValue, maxValue, saGroup));
        int res = Integer.parseInt(execQuery(q, model).get(0).get("c").toString().split("\\^\\^")[0]);
        return res;
    }

    public static int countSubjectsWithAgeZipcodeInGroup(int minValue, int maxValue, String zipcode, String saGroup, Model model) {
        Query q = QueryFactory.create(QueryUtil.createCountAgeZipcodeQuery(Collections.emptyList(), minValue, maxValue, zipcode, Arrays.asList(saGroup)));
        int res = Integer.parseInt(execQuery(q, model).get(0).get("c").toString().split("\\^\\^")[0]);
        return res;
    }

    public static int countSubjectsWithAgeZipcodeInTwoGroups(int minValue, int maxValue, String zipcode, String saGroup1, String saGroup2, Model model) {
        Query q = QueryFactory.create(QueryUtil.createCountAgeZipcodeQuery(Collections.emptyList(), minValue, maxValue, zipcode, Arrays.asList(saGroup1, saGroup2)));
        int res = Integer.parseInt(execQuery(q, model).get(0).get("c").toString().split("\\^\\^")[0]);
        return res;
    }

}
