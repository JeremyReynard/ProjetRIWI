/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import serialization.IndexDeserialization;

/**
 * Class LtnSmartElements
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class LtnSmartElements extends ScoreElements {

    String precision;

    public LtnSmartElements(String request, Index index, String precision) {
        super(request, index);
        this.precision = precision;
    }

    public Map<String, Map<String, Double>> getScores() {

        Map<String, Map<String, Double>> scores = new HashMap<>();

        String documentNumber;

        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }

        return scores;
    }

    public Map<String, Double> getRequestScore(String documentNumber) {
        Map<String, Double> pathsScores = new HashMap<>();
        ArrayList<String> paths;
        double score;
        for (String word : this.request.split("[\\W]+")) {
            //For each path
            if (index.getCollectionData().get(word) != null) {
                if (index.getCollectionData().get(word).get(documentNumber) != null) {
                    paths = generatedPathsList(index.getCollectionData().get(word).get(documentNumber));
                    for (String p : paths) {
                        score = getDocumentWordScore(word, getTermFrequency(index, word, documentNumber, p), p);
                        if (!pathsScores.containsKey(p)) {
                            pathsScores.put(p, score);
                        }
                    }
                }
            }
        }

        return pathsScores;
    }

    public double getDocumentWordScore(String word, float termFrequency, String path) {

        int documentFrequency = getDocumentFrequency(index, word, path);

        String[] tags = path.split("/");
        String tag = tags[tags.length - 1].replaceAll("\\[\\d+\\]", "");

        String pathForN = "";
        for (int i = 0; i < tags.length - 1; i++) {
            if (!tags[i].isEmpty()) {
                pathForN = pathForN + "/" + tags[i];
            }
        }

        pathForN = pathForN + "/" + tag;

        return Math.log(1.0 + termFrequency) * (index.getN(pathForN) / (documentFrequency));
    }

    private ArrayList<String> generatedPathsList(ArrayList<String> paths) {
        System.out.println("Paths : " + paths);

        boolean isInArticle = false;
        boolean isInHeader = false;
        boolean isInBody = false;

        ArrayList<String> pathsList = new ArrayList<>();

        String[] splitedPath;
        String s;
        for (String p : paths) {
            System.out.println(p);
            splitedPath = p.split("/");
            for (int i = 1; i < splitedPath.length; i++) {

                switch (splitedPath[i].replaceAll("\\[\\d+\\]", "")) {
                    case "article":
                        isInArticle = true;
                        break;
                    case "header":
                        if (!isInBody) {
                            isInHeader = true;
                        }
                        break;
                    case "bdy":
                        System.out.println("BODY");
                        isInBody = true;
                        break;
                }
                s = "";
                for (int j = 1; j <= i; j++) {
                    if (precision.contains("bdy") && isInBody) {
                        s = s + "/" + splitedPath[j];
                    }
                }
                System.out.println("s : " + s);
                if (s.startsWith(this.precision)) {
                    s = this.precision;
                    if (!pathsList.contains(s)) {
                        pathsList.add(s);
                    }
                }
            }
        }

        System.out.println(
                "Paths list : " + pathsList);

        return pathsList;
    }

    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexXML10.serial");

        String request = "almond";

        System.out.println(index.getCollectionData().get(request.toLowerCase()) + "\n");

        LtnSmartElements score = new LtnSmartElements(request, index, "/article[1]/bdy");

        Map<String, Map<String, Double>> scores = score.getScores();

        System.out.println("[main][scores]" + scores.toString());
    }
}
