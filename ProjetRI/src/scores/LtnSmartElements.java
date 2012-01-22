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

    public LtnSmartElements(String request, Index index) {
        super(request, index);
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
            int dl = index.getDlMap().get(documentNumber).intValue();
            //For each path

            if (index.getCollectionData().get(word) != null) {
                if (index.getCollectionData().get(word).get(documentNumber) != null) {
                    paths = generatedPathsList(index.getCollectionData().get(word).get(documentNumber));
                    for (String p : paths) {
                        score = getDocumentWordScore(word, getTermFrequency(index, word, documentNumber, p), p, dl);
                        if (pathsScores.containsKey(p)) {
                            //pathsScores.put(p,pathsScores.get(p) + score);
                        } else {
                            pathsScores.put(p, score);
                        }
                    }
                }
            }
        }

        return pathsScores;
    }

    public double getDocumentWordScore(String word, float termFrequency, String path, int documentLength) {

        int documentFrequency = getDocumentFrequency(index, word, path);

        String[] tags = path.split("/");
        String tag = tags[tags.length - 1].replaceAll("\\[\\d+\\]", "");

        //System.out.println(path + " " + documentFrequency+ " " + tag);

        return Math.log(1.0 + termFrequency) * (index.getN().get(tag) / (documentFrequency));
    }

    private ArrayList<String> generatedPathsList(ArrayList<String> paths) {
        ArrayList<String> pathsList = new ArrayList<>();

        String[] splitedPath;
        String s;
        for (String p : paths) {
            splitedPath = p.split("/");
            for (int i = 1; i < splitedPath.length; i++) {
                s = "";
                for (int j = 1; j <= i; j++) {
                    s = s + "/" + splitedPath[j]/*.replaceAll("\\[\\d+\\]", "")*/;
                }
                pathsList.add(s);
            }
        }

        return pathsList;
    }

    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexXML1.serial");

        String request = "Gottschalk";

        System.out.println(index.getCollectionData().get(request) + "\n");

        LtnSmartElements score = new LtnSmartElements(request, index);

        Map<String, Map<String, Double>> scores = score.getScores();

        System.out.println("[main][scores]" + scores.toString());
    }
}
