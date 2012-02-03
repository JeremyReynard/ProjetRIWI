/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import index.Index;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public class LtcSmartArticles  extends Score implements CommonsScoreInterface{
    
    /**
     * The constructor
     * @param request the request
     * @param index the index
     */
    public LtcSmartArticles(String request, Index index) {
        super(request, index);
    }
    
    @Override
    public Map<String, Double> getScores() {
        Map<String, Double> scores = new HashMap<>();
        
        String documentNumber;
        int ind=0;
        int size = index.getDlMap().size();
        //For all document
        for (Iterator i = index.getDlMap().keySet().iterator(); i.hasNext();) {
            ind++;
            System.out.println("Document traité:"+ind+"/"+size);
            documentNumber = i.next().toString();
            scores.put(documentNumber, getRequestScore(documentNumber));
        }
        
        return scores;
    }
    
    @Override
    public double getRequestScore(String documentNumber) {
        double score = 0;
        
        List<String> vocabularyList = this.index.getVocabulary().get(documentNumber);
        
        for (String word : this.request.split("[\\W]+")) {
            int dl = index.getDl(documentNumber,"/article");
            score += getDocumentWordScore(word,getTermFrequency(index, word, documentNumber) , dl,documentNumber,vocabularyList);
        }
        
        return score;
    }
    
    @Override
    public double getDocumentWordScore(String word, float termFrequency, int documentLength) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public double getDocumentWordScore(String word,float termFrequency,int documentLength, String documentNumber,List<String>vocabularyList){
        
        int documentFrequency = getDocumentFrequency(index, word);
        
        int N = index.getN().get("/article");
        
        int termTemporaryFrequency = 0;
        int termTemporaryDocumentFrequency = 0;
        int sommePonderations=0;
        //For all word in the vocabulary
        for(String wordOfvocabulary : vocabularyList)
        {
            
            //Get the term frequency of an existing word
            termTemporaryFrequency = getTermFrequency(index, wordOfvocabulary, documentNumber);
            
            //Get the term frenquency of an existing word in a specific document
            termTemporaryDocumentFrequency = getDocumentFrequency(index, wordOfvocabulary);
            
            
            //Formula
            sommePonderations+=Math.pow((Math.log(1+termTemporaryFrequency)*(N /termTemporaryDocumentFrequency)),2);
            
        }
        //Score formula
        Double result = Math.log(1+ termFrequency)/Math.sqrt(sommePonderations)* (N/documentFrequency );
        
        return result;
    }

}
