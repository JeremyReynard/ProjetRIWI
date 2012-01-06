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
 * test
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Bm25Articles extends Score implements CommonsScoreInterface {

    int k1;
    double b;

    public Bm25Articles(String request, Index index, int k1, double b) {
        super(request, index);
        this.b = b;
        this.k1 = k1;
    }

    @Override
    public Map<String, Double> getScores() {

        Map<String, Double> scores = new HashMap<>();
        
        String documentNumber;
        Iterator it;

        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }
        
        return scores;

    }

    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;

        for (String word : this.request.split("\\W")) {
            int dl = index.getDlMap().get(documentNumber).intValue();
            score += getDocumentWordScore(word, getTermFrequency(index, word, documentNumber), dl);
        }

        return score;
    }

    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {

        int df = getDocumentFrequency(index, word);

        if (df == -1) {
            return 0;
        }

        double wtd = ((termFrequency * (k1 + 1)) / (k1 * (1 - b + b * (documentLength / index.getAvdl())) + termFrequency)) * Math.log((index.getN() - df + 0.5) / (df + 0.5));

        return wtd;
    }

    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/index.serial");

        Bm25Articles score = new Bm25Articles("largely", index, 1, 0.5);

        Map<String, Double> scores = score.getScores();
        
        System.out.println("Scores : "+scores);

    }
}
