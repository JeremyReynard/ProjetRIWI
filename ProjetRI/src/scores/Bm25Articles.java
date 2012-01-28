/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import serialization.IndexDeserialization;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Bm25Articles extends Score implements CommonsScoreInterface {

    double k1;
    double b;

    public Bm25Articles(String request, Index index, double k1, double b) {
        super(request, index);
        this.b = b;
        this.k1 = k1;
    }

    @Override
    public Map<String, Double> getScores() {

        Map<String, Double> scores = new HashMap<>();

        String documentNumber;

        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }

        return scores;

    }

    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;

        int dl = index.getDl(documentNumber, "/article");
        int N = index.getN().get("/article");
        double avdl = index.getAvdl("/article");

        for (String word : this.request.split("\\W")) {
            score += getDocumentWordScore(word, getTermFrequency(index, word, documentNumber), dl, N, avdl);
        }

        return score;
    }

    public double getDocumentWordScore(String word, float tf, int dl, int N, double avdl) {

        int df = getDocumentFrequency(index, word);

        /*  System.out.println("Avdl : "+index.getAvdl("/article"));
        System.out.println("df : "+df);
        System.out.println("dl : "+documentLength);
        System.out.println("tf : "+termFrequency);
        System.out.println("N : "+index.getN().get("/article"));*/

        if (df == -1) {
            return 0;
        }

        double wtd = ((tf * (k1 + 1)) / (k1 * (1 - b + b * (dl /avdl)) + tf)) * Math.log((N - df + 0.5) / (df + 0.5));

        return wtd;
    }

    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexXML1000.serial");

        Bm25Articles score = new Bm25Articles("states", index, 1, 0.5);

        Map<String, Double> scores = score.getScores();

        System.out.println("Scores : " + scores);

    }

    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
