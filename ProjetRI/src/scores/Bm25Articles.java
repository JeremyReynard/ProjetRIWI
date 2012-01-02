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

    int k1;
    int b;

    public Bm25Articles(String request, Index index) {
        super(request, index);
    }
    @Override
    public Map<String, Double> getXBestScore(int X) {

        Map<String, Double> scores = new HashMap<>();
        Map<String, Double> bestScores = new HashMap<>();

        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            String documentNumber = i.next().toString();
            System.out.println("DocNumber : " + documentNumber);
            scores.put(documentNumber, getRequestScore(documentNumber));
        }

        return scores;

    }
    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;

        for (String word : this.request.split("\\W")) {
            int dl = index.getDlMap().get(documentNumber).intValue();
            System.out.println(dl);
            score += getDocumentWordScore(word, getTermFrequency(index, word, documentNumber), dl);
        }

        return score;
    }
    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {

        int df = getDocumentFrequency(index, word);

        double wtd = ((termFrequency * (k1 + 1)) / (k1 * (1 - b + b * (documentLength / index.getAvdl())) + termFrequency)) * Math.log((index.getN() - df + 0.5) / (df + 0.5));

        return wtd;
    }

    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/index.serial");

        System.out.println("dl : " + index.getDlMap().toString());

        Bm25Articles score = new Bm25Articles("Statistics", index);


        Map<String, Double> scores = score.getXBestScore(3);

        System.out.println(scores.toString());


    }
}
