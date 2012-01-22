/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
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

    public Bm25Elements(String request, Index index, double k1, double b) {
        super(request, index);
        this.b = b;
        this.k1 = k1;
    }

    public Map<String, Double> getScores() {

        Map<String, Double> scores = new HashMap<>();

        String documentNumber;
 
        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }

        return scores;

    }

    public double getRequestScore(String documentNumber) {
        double score = 0;

        for (String word : this.request.split("\\W")) {
            int dl = index.getDlMap().get(documentNumber).intValue();
            //score += getDocumentWordScore(word, getTermFrequency(index, word, documentNumber), dl);
        }

        return score;
    }

    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {

        int df = 0;//getDocumentFrequency(index, word);

        if (df == -1) {
            return 0;
        }

        double wtd = ((termFrequency * (k1 + 1)) / (k1 * (1 - b + b * (documentLength / index.getAvdl())) + termFrequency)) * Math.log((index.getN().get("article") - df + 0.5) / (df + 0.5));

        return wtd;
    }
}
