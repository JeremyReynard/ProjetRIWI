/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scores;

import java.util.Map;

/**
 *
 * @author Michaël BARD
 * @author Mickaël LHOSTE
 * @author Jérémy REYNARD
 */
public interface CommonsScoreInterface {
    
    /**
     * Get the score's map of all documents
     * @return the score's map
     */
    public Map<String, Double> getScores();
    
    /**
     * Get the request Score for the document
     * @param documentNumber the document Number
     * @return the score
     */
    public double getRequestScore(String documentNumber);
    
    /**
     * Get the document word score
     * @param word a Word
     * @param termFrequency the Term Frequency
     * @param documentLength the document Length
     * @return the document word score
     */
    public double getDocumentWordScore(String word, float termFrequency, int documentLength);
    
    

}
