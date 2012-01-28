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
        ArrayList<String> paths;
        double score = 0;

        for (String word : this.request.split("\\W")) {
            if (index.getCollectionData().get(word) != null) {
                if (index.getCollectionData().get(word).get(documentNumber) != null) {
                    paths = generatedPathsList(index.getCollectionData().get(word).get(documentNumber));
                    for (String p : paths) {
                        int dl = index.getDl(documentNumber, p);
                        score = getDocumentWordScore(word, getTermFrequency(index, word, documentNumber, p), dl, p);
                        if (!pathsScores.containsKey(p)) {
                            pathsScores.put(p, score);
                        }
                    }
                }
            }
        }

        return pathsScores;
    }

    public double getDocumentWordScore(String word, float tf, int dl, int N, double avdl, String path) {

        int df = getDocumentFrequency(index, word, path);



        if (df == -1) {
            return 0;
        }

        double wtd = ((tf * (k1 + 1)) / (k1 * (1 - b + b * (dl / avdl)) + tf)) * Math.log((N - df + 0.5) / (df + 0.5));

        return wtd;
    }

    public double getDocumentWordScore(String word, float tf, int dl, String path) {

        int df = getDocumentFrequency(index, word, path);

        /*  System.out.println("Avdl : "+index.getAvdl("/article"));
        System.out.println("df : "+df);
        System.out.println("dl : "+documentLength);
        System.out.println("tf : "+termFrequency);
        System.out.println("N : "+index.getN().get("/article"));*/

        if (df == -1) {
            return 0;
        }

        double wtd = ((tf * (k1 + 1)) / (k1 * (1 - b + b * (dl / index.getAvdl(path))) + tf))
                * Math.log((index.getN(path) - df + 0.5) / (df + 0.5));

        return wtd;
    }

    private ArrayList<String> generatedPathsList(ArrayList<String> paths) {
        ArrayList<String> pathsList = new ArrayList<>();

        String[] splitedPath;
        String s;
        for (String p : paths) {
            if (p.startsWith(this.precision)) {
                p = this.precision;
                splitedPath = p.split("/");
                for (int i = 1; i < splitedPath.length; i++) {
                    s = "";
                    for (int j = 1; j <= i; j++) {
                        s = s + "/" + splitedPath[j];
                    }
                    if (!pathsList.contains(s)) {
                        pathsList.add(s);
                    }
                }
            }
        }

        return pathsList;
    }
}
