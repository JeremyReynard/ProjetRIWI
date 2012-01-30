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
        //System.out.println(documentNumber);
        Map<String, Double> pathsScores = new HashMap<>();
        PathsCouple pathsCouple;
        double score;
        String p;
        for (String word : this.request.split("[\\W]+")) {
            //For each path
            if (index.getCollectionData().get(word) != null) {
                if (index.getCollectionData().get(word).get(documentNumber) != null) {
                    pathsCouple = generatePathsList(index.getCollectionData().get(word).get(documentNumber));
                    for (int i = 0; i < pathsCouple.compressedPaths.size(); i++) {
                        p = pathsCouple.compressedPaths.get(i);
                        //System.out.println(p);
                        score = getDocumentWordScore(word, getTermFrequency(index, word, documentNumber, pathsCouple.originalPaths.get(i)), p, pathsCouple.originalPaths.get(i));
                        if (!pathsScores.containsKey(p)) {
                            pathsScores.put(pathsCouple.originalPaths.get(i), score);
                        }
                    }
                }
            }
        }

        return pathsScores;
    }

    public double getDocumentWordScore(String word, float termFrequency, String path, String completePath) {

        int documentFrequency = getDocumentFrequency(index, word, completePath);

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

    private PathsCouple generatePathsList(ArrayList<String> paths) {

        boolean isInArticle = false;
        boolean isInHeader = false;
        boolean isInBody = false;

        PathsCouple pathsCouple = new PathsCouple();

        String[] splitedPath;
        String s;
        for (String p : paths) {
            splitedPath = p.split("/");
            isInArticle = false;
            isInHeader = false;
            isInBody = false;
            s = "";
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
                        isInBody = true;
                        break;
                }
                if (splitedPath[i].replaceAll("\\[\\d+\\]", "").equals("article")
                        || (isInArticle && (isInHeader || isInBody))) {
                    s = s + "/" + splitedPath[i];
                }
            }
            if (s.startsWith(this.precision)) {
                s = this.precision;
                splitedPath = s.split("/");
                s = "";
                for (int j = 1; j < splitedPath.length; j++) {
                    if (!pathsCouple.compressedPaths.contains(s + "/" + splitedPath[j].replaceAll("\\[\\d+\\]", ""))) {
                        pathsCouple.compressedPaths.add(s + "/" + splitedPath[j].replaceAll("\\[\\d+\\]", ""));
                        if (j == splitedPath.length - 1) {
                            pathsCouple.originalPaths.add(p.split(splitedPath[j].replaceAll("\\[\\d+\\]", ""))[0] + splitedPath[j] + "[" + p.split("\\[")[p.split("\\[").length - 1]);
                        } else {
                            pathsCouple.originalPaths.add(p.split(splitedPath[j].replaceAll("\\[\\d+\\]", ""))[0] + splitedPath[j]);
                        }
                    }
                    s = s + "/" + splitedPath[j];
                }
            }
        }
        return pathsCouple;
    }

    private static class PathsCouple {

        ArrayList<String> originalPaths;
        ArrayList<String> compressedPaths;

        public PathsCouple() {
            this.originalPaths = new ArrayList<>();
            this.compressedPaths = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexXML10.serial");

        String request = "almond";

        //System.out.println(index.getCollectionData().get(request.toLowerCase()) + "\n");

        System.out.println("N : " + index.getN());


        LtnSmartElements score = new LtnSmartElements(request, index, "/article");

        Map<String, Map<String, Double>> scores = score.getScores();

        System.out.println("[main][scores]" + scores.toString());
    }
}
