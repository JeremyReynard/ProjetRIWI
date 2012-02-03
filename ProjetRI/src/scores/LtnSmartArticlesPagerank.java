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
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public class LtnSmartArticlesPagerank extends Score implements CommonsScoreInterface{
    
    /**
     * The constructor
     * @param request the request
     * @param index the index
     */
    public LtnSmartArticlesPagerank(String request, Index index) {
        super(request, index);
    }
    
    @Override
    public Map<String, Double> getScores() {
        
        Map<String, Double> scores = new HashMap<>();
        
        String documentNumber;
        
        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }
        
        return scores;
    }
    
    
    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;
        
        if(this.index.getPagerank().get(documentNumber)==null)
            this.pageRank = 1;
        else
            this.pageRank = this.index.getPagerank().get(documentNumber).size();
        
        for (String word : this.request.split("[\\W]+")) {
            int dl = index.getDl(documentNumber,"/article");
            score += getDocumentWordScore(word,getTermFrequency(index, word, documentNumber) , dl);
        }
        
        return score;
    }
    
    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {
        
        int documentFrequency = getDocumentFrequency(index, word);
        
        return Math.log(1.0 + termFrequency)
                * (index.getN().get("/article") /(documentFrequency) )
                * Math.log(1+this.pageRank);
    }
   
}
