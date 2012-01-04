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
 *LtnSmart Algorithm
 */
public class LtnSmartArticles extends Score implements CommonsScoreInterface{
    
    public LtnSmartArticles(String request, Index index) {
        super(request, index);
    }
    
    @Override
    public Map<String, Double> getXBestScore(int X) {

        Map<String, Double> scores = new HashMap<>();
        Map<String, Double> bestScores = new HashMap<>();
        
        String documentNumber;
        Iterator it;

        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }
        
        if (X > this.index.getN()){
            X = this.index.getN();
        }

        scores = this.sortMap(scores);
        
        if (X > scores.size()){
            X = scores.size();
        }
        
        for (int i = 0; i < X; i++) {
            it = scores.keySet().iterator();
            documentNumber = it.next().toString();
            bestScores.put(documentNumber, scores.get(documentNumber));
            scores.remove(documentNumber);
        }

        return bestScores;
    }
    
    
    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;
        
        for (String word : this.request.split("[\\W]+")) {
            int dl = index.getDlMap().get(documentNumber).intValue();
            score += getDocumentWordScore(word,getTermFrequency(index, word, documentNumber) , dl);
        }
        
        return score;
    }
    
    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {
        
        int documentFrequency = getDocumentFrequency(index, word);
        
        return Math.log(1.0 + termFrequency) * (index.getN() /(documentFrequency) ) ;
    }
    
    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexSerialized.serial");
        
        LtnSmartArticles score = new LtnSmartArticles("states", index);
        
        Map<String, Double> scores = score.getXBestScore(3);
        
        System.out.println("[main][scores]"+scores.toString());        
    }

}
