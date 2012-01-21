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
 * Class LtnSmartElements
 * @author Michaël Bard <michael.bard@laposte.net>
 */
public class LtnSmartElements extends Score {
    
    public LtnSmartElements(String request, Index index) {
        super(request, index);
    }
    
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
    
    
    public double getRequestScore(String documentNumber) {
        double score = 0;
        
        for (String word : this.request.split("[\\W]+")) {
            int dl = index.getDlMap().get(documentNumber).intValue();
            score += getDocumentWordScore(word,getTermFrequency(index, word, documentNumber) , dl);
        }
        
        return score;
    }
    
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {
        
        int documentFrequency = getDocumentFrequency(index, word);
        
        return Math.log(1.0 + termFrequency) * (index.getN().get("article") /(documentFrequency) ) ;
    }
    
    public static void main(String[] args) {
        Index index = IndexDeserialization.deserialize("fileSerialization/indexSerialized.serial");
        
        LtnSmartElements score = new LtnSmartElements("states", index);
        
        Map<String, Double> scores = score.getScores();
        
        System.out.println("[main][scores]"+scores.toString());        
    }
    
}
