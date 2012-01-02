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
        
        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            String documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }
        //System.out.println(scores.toString());
        
        
        return scores;
    }
    
    
    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;
        
        for (String word : this.request.split("\\W")) {
            //System.out.println("[word][getRequestScore]"+word);
            int dl = index.getDlMap().get(documentNumber).intValue();
            // System.out.println("[dl][getRequestScore] "+dl);
            //ICI
            int termFrequency = getTermFrequency(index, word, documentNumber);
            score += getDocumentWordScore(word,termFrequency , dl);
        }
        
        return score;
    }
    
    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {
        
        int documentFrequency = getDocumentFrequency(index, word);
        System.out.println("[termFrequency][DocumentWordScore]"+termFrequency);
        // System.out.println("[index.getN()][DocumentWordScore]"+index.getN());
        // System.out.println("[documentFrequency][DocumentWordScore]"+documentFrequency);
        
        double result =  Math.log(1.0 + termFrequency) * (index.getN() /(documentFrequency) ) ;
        
        //System.out.println("[result][DocumentWordScore]"+result);
        return result;
    }
    
    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexSerialized.serial");
        
        //  System.out.println("dl : " + index.getDlMap().toString());
        
        LtnSmartArticles score = new LtnSmartArticles("states", index);
        // System.out.println("[request]"+score.getRequest());
        // System.out.println("[index]"+score.getIndex().toString());
        // System.out.println("[main][score.getRequestScore]"+String.valueOf(score.getRequestScore("10003934")));
        
        Map<String, Double> scores = score.getXBestScore(3);
        
        System.out.println("[main][scores]"+scores.toString());
        
        
    }

}
