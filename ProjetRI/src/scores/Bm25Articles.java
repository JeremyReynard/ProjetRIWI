/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
import java.util.Iterator;
import java.util.Map;
import serialization.IndexDeserialization;

/**
 *
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class Bm25Articles extends Score {

    int k1;
    int b;

    public Bm25Articles(String request, Index index) {
        super(request, index);
    }

    public double getRequestScore() {
        double score = 0;

        for (String word : this.request.split("\\W")) {
            score += getWordScore(word);
        }

        return score;
    }

    public double getWordScore(String word) {

        double wt = 0;

        Map<String, Integer> mapOfWord = index.getCollectionData().get(word);

        for (Iterator i = mapOfWord.keySet().iterator(); i.hasNext();) {
            String documentId = i.next().toString();
            //TODO Have the document length and the avdl
            int documentLength = 10;
            wt += getDocumentWordScore(word, getTermFrequency(index, word, documentId), documentLength);
        }

        return wt;
    }

    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {

        int df = getDocumentFrequency(index, word);

        double wtd = ((termFrequency * (k1 + 1)) / (k1 * (1 - b + b * (documentLength / index.getAvdl())) + termFrequency)) * Math.log((index.getN() - df + 0.5) / (df + 0.5));

        return wtd;
    }

    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/index.serial");

        Bm25Articles score = new Bm25Articles("Statistics", index);

        System.out.println("Score : " + score.getRequestScore());



    }
}
