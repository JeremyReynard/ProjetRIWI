/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
import java.util.Map;

/**
 *
 * @author Lulu
 */
public class Bm25fArticles extends Score implements CommonsScoreInterface{

    
     public Bm25fArticles(String request, Index index) {
        super(request, index);

    }
     
    @Override
    public Map<String, Double> getScores() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getRequestScore(String documentNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
