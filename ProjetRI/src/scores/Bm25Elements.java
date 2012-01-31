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
 * Class Bm25Elements
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Bm25Elements extends ScoreElements {

    double b;
    double k1;
    String precision;

    public Bm25Elements(String request, Index index, double k1, double b, String precision) {
        super(request, index);
        this.b = b;
        this.k1 = k1;
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

        // optimisation
        int dl = 0;
        int N = 0;
        double avdl = 0;

        String p;
        //For each word of request
        for (String word : this.request.split("\\W")) {
            //System.out.println("word : "+word);
            //For each path of the current word
            if (index.getCollectionData().get(word) != null) {
                //If tf !=0
                if (index.getCollectionData().get(word).get(documentNumber) != null) {
                    //Generate all compressed paths
                    pathsCouple = generatePathsList(index.getCollectionData().get(word).get(documentNumber));
                    for (int i = 0; i < pathsCouple.compressedPaths.size(); i++) {
                        p = pathsCouple.compressedPaths.get(i);
                        try {
                            dl = index.getDl(documentNumber, p);
                            N = index.getN(p);
                            avdl = index.getAvdl(p);
                        } catch (NullPointerException e) {
                           // System.out.println(word + "  compressedPath : " + p + " | documentNumber F" + documentNumber);
                            e.printStackTrace();
                            return null;
                        }
                        score += getDocumentWordScore(word, getTermFrequency(index, word, documentNumber, pathsCouple.originalPaths.get(i)), dl, N, avdl, p);
                        if (!pathsScores.containsKey(p)) {
                            pathsScores.put(pathsCouple.originalPaths.get(i), score);
                        }
                    }
                }
            }
        }
        return pathsScores;
    }

    public double getDocumentWordScore(String word, float tf, int dl, int N, double avdl, String path) {

        int df = getDocumentFrequency(index, word, precision);

        if (df == -1) {
            return 0;
        }

        double wtd = ((tf * (k1 + 1)) / (k1 * (1 - b + b * (dl / avdl)) + tf)) * Math.log((N - df + 0.5) / (df + 0.5));

        return wtd;
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
            if (!pathsCouple.compressedPaths.contains("/article")) {
                pathsCouple.compressedPaths.add("/article");
                pathsCouple.originalPaths.add("/article[1]");
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
       // System.out.println(pathsCouple.compressedPaths.toString());
       // System.out.println(pathsCouple.originalPaths.toString());
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

        System.out.println("Begin of deserialization...");
        Index index = IndexDeserialization.deserialize("fileSerialization/indexXML1000.serial");
        System.out.println("End of deserialization.");

        System.out.println("dlMap : " + index.getDlMap());
        System.out.println(" index " + index.getCollectionData().size());

        Bm25Elements score = new Bm25Elements("olive oil health benefit", index, 1, 0.5, "/article[1]/header");

        Map<String, Map<String, Double>> scores = score.getScores();

        System.out.println("Scores : " + scores);
    }
}
