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
 *
 * @author mlh
 */
public class MJMscore extends Score implements CommonsScoreInterface{

    private Bm25Articles scorer; 
    private final int k1 = 1;
    private final double b = 0.5;
    
    private double k;
        
    public MJMscore(String request, Index index, double k) {
        super(request, index);
        
        this.scorer = new Bm25Articles(request, index, k1, b);
        
        this.k = k;
    }

        
    @Override
    public Map<String, Double> getScores() {
        
        Map<String, Double> scores = new HashMap<>();
        
        String documentNumber;
      
        for (Iterator i = this.index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }
        
        return scores;
    }

    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;
        double wordCoeff = 1.0;
        int dl = 0;
        
        for (String word : this.request.split("\\W")) {
            dl = index.getDlMap().get(documentNumber).intValue();
            
            score += wordCoeff * 
                    getDocumentWordScore(word, getTermFrequency(index, word, documentNumber), dl);
            
            // update wordCoeff
            wordCoeff = wordCoeff / k;
        }

        return score;
    }

    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {

        if (getDocumentFrequency(index, word) == -1) {
            return 0;
        }
        return  (this.scorer.getDocumentWordScore(word, termFrequency, documentLength));
    }
}
