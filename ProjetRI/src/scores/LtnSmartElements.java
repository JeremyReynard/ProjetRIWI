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

/**
 *
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public class LtnSmartElements extends ScoreElements {

    String precision;

    /**
     * The constructor
     * @param request the request
     * @param index the index
     * @param precision the granularity
     */
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
        PathsCouple pathsCouple;
        double score = 0;
        String p;
        //For each word of request
        for (String word : this.request.split("[\\W]+")) {
            //For each path of the current word
            if (index.getCollectionData().get(word) != null) {
                //If tf !=0
                if (index.getCollectionData().get(word).get(documentNumber) != null) {
                    //Generate all compressed paths
                    pathsCouple = generatePathsList(index.getCollectionData().get(word).get(documentNumber));
                    for (int i = 0; i < pathsCouple.compressedPaths.size(); i++) {
                        p = pathsCouple.compressedPaths.get(i);
                        score += getDocumentWordScore(word, getTermFrequency(index, word, documentNumber, pathsCouple.originalPaths.get(i)), p);
                        if (!pathsScores.containsKey(p)) {
                            pathsScores.put(pathsCouple.originalPaths.get(i), score);
                        }
                    }
                }
            }
        }

        return pathsScores;
    }

    public double getDocumentWordScore(String word, float termFrequency, String path) {

        int documentFrequency = getDocumentFrequency(index, word, precision);

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
        boolean isInTitle = false;

        PathsCouple pathsCouple = new PathsCouple();

        String[] splitedPath;
        String s;
        String sEnd;
        for (String p : paths) {
            splitedPath = p.split("/");
            isInArticle = false;
            isInHeader = false;
            isInBody = false;
            isInTitle = false;
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
                    case "title":
                        if (isInHeader) {
                            isInTitle = true;
                        }
                        break;
                }
                if (splitedPath[i].replaceAll("\\[\\d+\\]", "").equals("article")
                        || (isInArticle && (isInHeader || isInBody))) {
                    s = s + "/" + splitedPath[i];
                }
            }
            if (s.startsWith(this.precision)) {
                sEnd = s.split(precision.split("/")[precision.split("/").length - 1])[1].split("/")[0];
                s = this.precision;
                splitedPath = s.split("/");
                s = "";
                for (int j = 1; j < splitedPath.length; j++) {
                    if (!pathsCouple.compressedPaths.contains(s + "/" + splitedPath[j].replaceAll("\\[\\d+\\]", ""))) {
                        pathsCouple.compressedPaths.add(s + "/" + splitedPath[j].replaceAll("\\[\\d+\\]", ""));
                        if (j == splitedPath.length - 1) {
                            pathsCouple.originalPaths.add(p.split(splitedPath[j].replaceAll("\\[\\d+\\]", ""))[0] + splitedPath[j] + sEnd);
                        } else {
                            pathsCouple.originalPaths.add(p.split(splitedPath[j].replaceAll("\\[\\d+\\]", ""))[0] + splitedPath[j]);
                        }
                    }
                    s = s + "/" + splitedPath[j];
                }
            }
        }
        if (!pathsCouple.compressedPaths.contains("/article")) {
            pathsCouple.compressedPaths.add("/article");
            pathsCouple.originalPaths.add("/article[1]");
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

}
