/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
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
        throw new UnsupportedOperationException("Not supported yet.");
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
       
         int documentFrequency = getDocumentFrequency(index, word);
         
         return Math.log(1.0 + (double)termFrequency) * ((double)index.getN() /(double)documentFrequency) ;
    }
    
    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexSerialized.serial");

        System.out.println("dl : " + index.getDlMap().toString());

        LtnSmartArticles score = new LtnSmartArticles("States", index);
        System.out.println(score.getRequestScore("10003934"));
                                                    
        //Map<String, Double> scores = score.getXBestScore(3);

      //  System.out.println(scores.toString());


    }
    
}
